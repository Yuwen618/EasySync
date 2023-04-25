using System;

namespace EasySync
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.content = new System.Windows.Forms.TextBox();
            this.btncopy = new System.Windows.Forms.Button();
            this.Hint = new System.Windows.Forms.Label();
            this.Connected = new System.Windows.Forms.Label();
            this.ServerInfo = new System.Windows.Forms.Label();
            this.notifyIcon1 = new System.Windows.Forms.NotifyIcon(this.components);
            this.button1 = new System.Windows.Forms.Button();
            this.pictureBox = new System.Windows.Forms.PictureBox();
            this.sendbox = new System.Windows.Forms.TextBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox)).BeginInit();
            this.SuspendLayout();
            // 
            // content
            // 
            this.content.Font = new System.Drawing.Font("Microsoft Sans Serif", 20F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(254)));
            this.content.Location = new System.Drawing.Point(8, 31);
            this.content.Margin = new System.Windows.Forms.Padding(2);
            this.content.Multiline = true;
            this.content.Name = "content";
            this.content.ReadOnly = true;
            this.content.Size = new System.Drawing.Size(504, 192);
            this.content.TabIndex = 0;
            this.content.TextChanged += new System.EventHandler(this.content_TextChanged);
            this.content.Click += new System.EventHandler(this.content_Click);
            // 
            // btncopy
            // 
            this.btncopy.Location = new System.Drawing.Point(442, 228);
            this.btncopy.Margin = new System.Windows.Forms.Padding(2);
            this.btncopy.Name = "btncopy";
            this.btncopy.Size = new System.Drawing.Size(64, 28);
            this.btncopy.TabIndex = 1;
            this.btncopy.Text = "Send";
            this.btncopy.UseVisualStyleBackColor = true;
            this.btncopy.Click += new System.EventHandler(this.btncopy_Click);
            // 
            // Hint
            // 
            this.Hint.Location = new System.Drawing.Point(461, 7);
            this.Hint.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.Hint.Name = "Hint";
            this.Hint.Size = new System.Drawing.Size(47, 21);
            this.Hint.TabIndex = 2;
            // 
            // Connected
            // 
            this.Connected.Location = new System.Drawing.Point(330, 8);
            this.Connected.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.Connected.Name = "Connected";
            this.Connected.Size = new System.Drawing.Size(87, 21);
            this.Connected.TabIndex = 3;
            this.Connected.Text = "Disconnected";
            // 
            // ServerInfo
            // 
            this.ServerInfo.Location = new System.Drawing.Point(8, 8);
            this.ServerInfo.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.ServerInfo.Name = "ServerInfo";
            this.ServerInfo.Size = new System.Drawing.Size(193, 21);
            this.ServerInfo.TabIndex = 4;
            this.ServerInfo.Click += new System.EventHandler(this.label1_Click);
            // 
            // button1
            // 
            this.button1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("button1.BackgroundImage")));
            this.button1.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.button1.Location = new System.Drawing.Point(10, 228);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(30, 29);
            this.button1.TabIndex = 6;
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // pictureBox
            // 
            this.pictureBox.Location = new System.Drawing.Point(8, 31);
            this.pictureBox.Name = "pictureBox";
            this.pictureBox.Size = new System.Drawing.Size(504, 191);
            this.pictureBox.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox.TabIndex = 7;
            this.pictureBox.TabStop = false;
            this.pictureBox.Click += new System.EventHandler(this.pictureBox_Click);
            // 
            // sendbox
            // 
            this.sendbox.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(254)));
            this.sendbox.Location = new System.Drawing.Point(47, 229);
            this.sendbox.Name = "sendbox";
            this.sendbox.Size = new System.Drawing.Size(390, 26);
            this.sendbox.TabIndex = 8;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(519, 267);
            this.Controls.Add(this.sendbox);
            this.Controls.Add(this.pictureBox);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.ServerInfo);
            this.Controls.Add(this.Connected);
            this.Controls.Add(this.Hint);
            this.Controls.Add(this.btncopy);
            this.Controls.Add(this.content);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Margin = new System.Windows.Forms.Padding(2);
            this.MaximizeBox = false;
            this.Name = "Form1";
            this.Text = "EasySync";
            this.Load += new System.EventHandler(this.Form1_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }


        #endregion

        private System.Windows.Forms.TextBox content;
        private System.Windows.Forms.Button btncopy;
        private System.Windows.Forms.Label Hint;
        private System.Windows.Forms.Label Connected;
        private System.Windows.Forms.Label ServerInfo;
        private System.Windows.Forms.NotifyIcon notifyIcon1;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.PictureBox pictureBox;
        private System.Windows.Forms.TextBox sendbox;
    }
}

