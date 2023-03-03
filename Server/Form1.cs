using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
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
        public Form1()
        {
            InitializeComponent();
            this.FormClosing += new FormClosingEventHandler(Form1_FormClosing);
            Thread serverThread = new Thread(()=>
            {
                StartServer();
            });
            serverThread.Start();
            process = Process.GetCurrentProcess();
            this.Icon = new Icon("D:\\Projects\\EasySync\\myico.ico");
            notifyIcon1.Visible = true;
        }

        private void StartServer()
        {
            // Create a new instance of the SocketServer class
            server = new SocketServer(this);
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void Form1_Resize(object sender, EventArgs e)
        {
            if (WindowState == FormWindowState.Minimized)
            {
                notifyIcon1.Visible = true;
                ShowInTaskbar = false;
                Hide();
            }
            else
            {
                notifyIcon1.Visible = false;
                ShowInTaskbar = true;
            }
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
                    Hint.Text = "";
                    this.ShowInTaskbar = true;
                    this.WindowState = FormWindowState.Normal;
                    this.Show();
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
            if (Clipboard.ContainsText())
            {
                Clipboard.Clear();
            }
            if (!string.IsNullOrEmpty(content.Text))
            {
                Clipboard.SetText(content.Text);
                Hint.Text = "Copied";
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
            notifyIcon1.Dispose();
            ShutDown();
        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void content_TextChanged(object sender, EventArgs e)
        {

        }

        private void notifyIcon1_Click(object sender, EventArgs e)
        {
            this.ShowInTaskbar = true;
            this.WindowState = FormWindowState.Normal;
            this.Show();
        }

    }
}
