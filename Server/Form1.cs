using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Net.Mime.MediaTypeNames;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;


namespace EasySync
{
    public partial class Form1 : Form
    {
        private SocketServer server;
        delegate void UpdateTextBoxDelegate(string text);
        UpdateTextBoxDelegate updateTextBoxDelegate;
        Process process;

        private bool isMouseDown = false;
        private Point startPoint;
        private Rectangle selectionRectangle;
        private Pen dashPen;
        private Cursor cursorCross;
        private bool mClientConnected = false;
        private string curfileName;
        public Form1()
        {
            InitializeComponent();

            this.ShowInTaskbar = false;

            this.FormClosing += new FormClosingEventHandler(Form1_FormClosing);
            Thread serverThread = new Thread(()=>
            {
                StartServer();
            });
            serverThread.Start();
            process = Process.GetCurrentProcess();
            this.Icon = new Icon("D:\\Projects\\EasySync\\easysync.ico");
            notifyIcon1.Visible = true;

            dashPen = new Pen(Color.Black);
            dashPen.DashStyle = DashStyle.Dash;

            //cursorCross = new Cursor(Properties.Resources.crosshair.Handle);
            //DockForm df = new DockForm();
            //df.Show();
        }

        private void StartServer()
        {
            // Create a new instance of the SocketServer class
            server = new SocketServer(this);
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }


        public void updateConnectStatus(bool connected)
        {
            new Task(new Action(() =>
            {
                this.Invoke(new Action(() =>
                {
                    if (connected)
                        Connected.Text = "Connected";
                    else
                        Connected.Text = "Disconnected";
                    mClientConnected = connected;
                }));
            })).Start();
        }

        public void updateServerInfo(string text)
        {
            new Task(new Action(() =>
            {
                this.Invoke(new Action(() =>
                {
                    ServerInfo.Text = text;
                }));
            })).Start();
        }

        public void UpdateTextBox(string text)
        {
            //Invoke(new Action<string>(UpdateTextBox), text);
            //content.Invoke(updateTextBoxDelegate);
            /*
            if (InvokeRequired)
            {
                Invoke(new Action<string>(UpdateTextBox), text);
            }
            else
            {
                content.AppendText(text + Environment.NewLine);
            }
            */
            new Task(new Action(() =>
            {
                this.Invoke(new Action(() =>
                {
                    content.Text = text;
                    Hint.Text = "Sent";


                    content.Show();
                    pictureBox.Hide();
                    //BringToFront();
                    //Activate();
                }));
            })).Start();
        }

        public void PortNotAvilable()
        {
            new Task(new Action(() =>
            {
                this.Invoke(new Action(() =>
                {
                    MessageBox.Show("Port 30291 not available, please check.");
                    ShutDown();
                }));
            })).Start();
        }

        private void btncopy_Click(object sender, EventArgs e)
        {
            /*
            if (Clipboard.ContainsText())
            {
                Clipboard.Clear();
            }
            if (!string.IsNullOrEmpty(content.Text))
            {
                Clipboard.SetText(content.Text);
                Hint.Text = "Copied";
            }
            */
            if (!string.IsNullOrEmpty(sendbox.Text) && mClientConnected)
            {
                server.sendTextToClient(sendbox.Text);
                Hint.Text = "Sent";
                sendbox.Text = "";

            }
        }

        private void sendbox_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                if (!string.IsNullOrEmpty(sendbox.Text) && mClientConnected)
                {
                    server.sendTextToClient(sendbox.Text);
                    Hint.Text = "Sent";
                }
            }
        }

        private void pictureBox_Click(object sender, EventArgs e)
        {
            if (curfileName != null)
            {
                Process.Start("explorer.exe", curfileName);
            }

        }

        private void ShutDown()
        {
            if (server != null)
            {
                server.shutdown();
            }
            // 关闭时杀掉进程
            if (process != null && !process.HasExited)
            {
                process.Kill();
            }
            System.Windows.Forms.Application.Exit();
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason == CloseReason.UserClosing)
            {
                // 阻止关闭窗体
                e.Cancel = true;

                // 隐藏窗体
                this.Hide();
                if (df == null)
                {
                    df = new DockForm(this);
                }
                df.Show();
            }

            //ShutDown();
        }
        DockForm df = null;

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void content_Click(object sender, EventArgs e)
        {
 
            if (content.Text != "")
            {
                if (content.Text.StartsWith("http://") || content.Text.StartsWith("https://"))
                {
                    Process.Start("chrome.exe", content.Text);
                }
                else
                {
                    Clipboard.SetText(content.Text);
                    Hint.Text = "Copied";
                }
            }
            
        }

        private void content_TextChanged(object sender, EventArgs e)
        {

            if (content.Text != "")
            {
                if (content.Text.StartsWith("http://") || content.Text.StartsWith("https://"))
                {
                    Process.Start("chrome.exe", content.Text);
                }
                else
                {
                    Clipboard.SetText(content.Text);
                    Hint.Text = "Copied";
                }
            }

        }

        private async void button1_Click(object sender, EventArgs e)
        {
            Hide();
            await Task.Delay(500);
            ScreenshotForm screenshotForm = new ScreenshotForm();
            screenshotForm.ShowDialog();

            Show();

            System.Drawing.Image clipboardImage = Clipboard.GetImage();
            if (clipboardImage != null)
            {
                // 设置到PictureBox控件中显示
                pictureBox.Image = clipboardImage;
                content.Hide();
                pictureBox.Show();
                curfileName = DateTime.Now.ToString("yyyyMMdd_HHmmss") + ".png";
                clipboardImage.Save(curfileName, ImageFormat.Png);

                //send to client
                if (mClientConnected)
                {
                    MemoryStream ms = new MemoryStream();
                    clipboardImage.Save(ms, ImageFormat.Png);
                    byte[] imageData = ms.ToArray();
                    server.sendImgToClient(imageData);
                }
            }
        }



        private Bitmap CaptureScreen()
        {
            var screenBounds = Screen.GetBounds(Point.Empty);
            var bitmap = new Bitmap(screenBounds.Width, screenBounds.Height);
            using (var g = Graphics.FromImage(bitmap))
            {
                g.CopyFromScreen(Point.Empty, Point.Empty, screenBounds.Size);
            }
            return bitmap;
        }




    }


    public partial class ScreenshotForm : Form
    {
        private Point startPoint;
        private Rectangle rect = new Rectangle();

        private bool isMouseDown = false;
        private Cursor cursorCross;

        public ScreenshotForm()
        {


            // 设置窗体为无边框样式
            FormBorderStyle = FormBorderStyle.None;

            // 设置窗体全屏
            WindowState = FormWindowState.Maximized;

            // 用自定义光标替换鼠标默认光标

            Cursor = cursorCross;

            // 截取全屏幕图像
            this.BackgroundImage = new Bitmap(Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height);
            using (var g = Graphics.FromImage(this.BackgroundImage))
            {
                g.CopyFromScreen(0, 0, 0, 0, this.BackgroundImage.Size);
            }

            // 设置截屏窗体的一些属性
            this.FormBorderStyle = FormBorderStyle.None;
            this.WindowState = FormWindowState.Maximized;
            this.Cursor = Cursors.Cross;
            this.DoubleBuffered = true;
            this.KeyDown += new KeyEventHandler(ScreenshotForm_KeyDown);

        }

        protected override void OnMouseDown(MouseEventArgs e)
        {
            base.OnMouseDown(e);

            if (e.Button == MouseButtons.Left)
            {
                startPoint = e.Location;

                isMouseDown = true;
            }
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);

            if (isMouseDown)
            {
                Point tempEndPoint = e.Location;

                rect.Location = new Point(
                    Math.Min(startPoint.X, tempEndPoint.X),
                    Math.Min(startPoint.Y, tempEndPoint.Y));

                rect.Size = new Size(
                    Math.Abs(startPoint.X - tempEndPoint.X),
                    Math.Abs(startPoint.Y - tempEndPoint.Y));

                Invalidate();
            }
        }

        protected override void OnMouseUp(MouseEventArgs e)
        {
            base.OnMouseUp(e);

            if (e.Button == MouseButtons.Left)
            {
                isMouseDown = false;

                // 根据所选区域截取屏幕并复制到剪贴板
                Bitmap screenshot = new Bitmap(rect.Width, rect.Height);
                Graphics graphics = Graphics.FromImage(screenshot);

                rect.Location = new Point(rect.Location.X+1, rect.Location.Y+1);
                rect.Size = new Size(rect.Size.Width-1, rect.Size.Height-1);

                graphics.CopyFromScreen(rect.Location, Point.Empty, rect.Size);

                Clipboard.SetImage(screenshot);

                Close();
            }
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);

            // 绘制虚线矩形框
            using (Pen pen = new Pen(Color.Red))
            {
                pen.DashStyle = System.Drawing.Drawing2D.DashStyle.Dash;

                e.Graphics.DrawRectangle(pen, rect);
            }
        }

        private void ScreenshotForm_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Escape)
            {
                this.Close();
            }
        }
    }
}
