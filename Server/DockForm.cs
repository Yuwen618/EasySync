using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace EasySync
{
    public partial class DockForm : Form
    {
        private DockStyle dockStyle = DockStyle.None;
        private Bitmap iconBitmap;
        private Form mMainForm;
        ContextMenuStrip contextMenu;
        public DockForm(Form mainForm)
        {
            //InitializeComponent();
            mMainForm = mainForm;

            this.FormBorderStyle = FormBorderStyle.None;
            //this.BackColor = Color.Transparent;
            this.TransparencyKey = Color.GreenYellow;
            this.BackColor = Color.GreenYellow;
            // Set the form size and position
            this.Size = new Size(40, 40);
            this.Location = new Point(0, 0);

            // Make the form topmost and show it on the desktop
            this.TopMost = true;
            this.ShowInTaskbar = false;
            this.Show();
            this.Visible = true;

            // Create a new instance of the icon and set its properties
            //Icon icon = new Icon("icon.ico");
            //this.Icon = icon;

            // Add event handlers for mouse events
            this.MouseDown += DockForm_MouseDown;
            this.MouseMove += DockForm_MouseMove;
            this.MouseUp += DockForm_MouseUp;


            Icon icon = new Icon("d:\\projects\\DockApp\\myico.ico");
            iconBitmap = new Bitmap(icon.Width, icon.Height, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
            Graphics g = Graphics.FromImage(iconBitmap);
            g.Clear(Color.Transparent);
            g.DrawIcon(icon, 0, 0);

            contextMenu = new ContextMenuStrip();
            contextMenu.Items.Add("Exit");

            contextMenu.Items[0].Click += new EventHandler(menuItem1_Click);
        }

        private void menuItem1_Click(object sender, EventArgs e)
        {
            Process.GetCurrentProcess().Kill();
        }

        protected override void OnDoubleClick(EventArgs e)
        {
            base.OnDoubleClick(e);
            // 在这里添加双击事件的处理代码
            mMainForm.Show();
            Hide();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);

            // Draw the icon on the form
            Pen pen = new Pen(Color.Red, 2);
            Rectangle rect = new Rectangle(0, 0, 40, 40);
            e.Graphics.DrawEllipse(pen, rect);

            // Draw the icon inside the circle
            Rectangle iconRect = new Rectangle(2, 2, 40, 40);
            e.Graphics.DrawImage(iconBitmap, iconRect);
        }
        private void DockForm_Load(object sender, EventArgs e)
        {

        }

        private void DockForm_MouseDown(object sender, MouseEventArgs e)
        {
            //this.Location = Cursor.Position;

            // Capture the mouse for dragging
            Capture = true;
            if (e.Button == MouseButtons.Right)
            {
                contextMenu.Show(this, e.Location);

            }

        }

        private void DockForm_MouseMove(object sender, MouseEventArgs e)
        {
            // Move the form
            if (this.Capture)
            {
                this.Location = new Point(Cursor.Position.X - this.Size.Width / 2, Cursor.Position.Y - this.Size.Height / 2);

                // 检测是否停靠在屏幕边缘
                if (this.Location.X < 200)
                {
                    this.dockStyle = DockStyle.Left;
                }
                else if (this.Location.X + this.Size.Width > Screen.PrimaryScreen.WorkingArea.Width - 200)
                {
                    this.dockStyle = DockStyle.Right;
                }
                else if (this.Location.Y < 200)
                {
                    this.dockStyle = DockStyle.Top;
                }
                else if (this.Location.Y + this.Size.Height > Screen.PrimaryScreen.WorkingArea.Height - 200)
                {
                    this.dockStyle = DockStyle.Bottom;
                }
                else
                {
                    this.dockStyle = DockStyle.None;
                }
            }
        }

        private void DockForm_MouseUp(object sender, MouseEventArgs e)
        {
            // Stop dragging the form and dock it to the screen edge if necessary
            this.Capture = false;
            this.Cursor = Cursors.Default;

            if (this.dockStyle != DockStyle.None)
            {
                this.DockToScreenEdge();
            }
        }

        private void DockToScreenEdge()
        {
            //this.Size = new Size(50, Screen.PrimaryScreen.WorkingArea.Height / 2);
            switch (this.dockStyle)
            {
                case DockStyle.Left:
                    this.Location = new Point(-20, Cursor.Position.Y);
                    
                    this.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Bottom;
                    break;
                case DockStyle.Right:
                    this.Location = new Point(Screen.PrimaryScreen.WorkingArea.Width - 20, Cursor.Position.Y);
                    //this.Size = new Size(50, Screen.PrimaryScreen.WorkingArea.Height / 2);
                    this.Anchor = AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Bottom;
                    break;
                case DockStyle.Top:
                    this.Location = new Point(Cursor.Position.X, -20);
                    //this.Size = new Size(Screen.PrimaryScreen.WorkingArea.Width / 2, 50);
                    this.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
                    break;
                case DockStyle.Bottom:
                    this.Location = new Point(Cursor.Position.X, Screen.PrimaryScreen.WorkingArea.Height);
                    //this.Size = new Size(Screen.PrimaryScreen.WorkingArea.Width / 2, 50);
                    this.Anchor = AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
                    break;
                default:
                    break;
            }
        }
    }

}
