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
            this.SuspendLayout();
            // 
            // content
            // 
            this.content.Font = new System.Drawing.Font("Microsoft Sans Serif", 20F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(254)));
            this.content.Location = new System.Drawing.Point(12, 47);
            this.content.Multiline = true;
            this.content.Name = "content";
            this.content.ReadOnly = true;
            this.content.Size = new System.Drawing.Size(754, 182);
            this.content.TabIndex = 0;
            this.content.TextChanged += new System.EventHandler(this.content_TextChanged);
            // 
            // btncopy
            // 
            this.btncopy.Location = new System.Drawing.Point(558, 251);
            this.btncopy.Name = "btncopy";
            this.btncopy.Size = new System.Drawing.Size(161, 49);
            this.btncopy.TabIndex = 1;
            this.btncopy.Text = "Copy";
            this.btncopy.UseVisualStyleBackColor = true;
            this.btncopy.Click += new System.EventHandler(this.btncopy_Click);
            // 
            // Hint
            // 
            this.Hint.Location = new System.Drawing.Point(481, 268);
            this.Hint.Name = "Hint";
            this.Hint.Size = new System.Drawing.Size(71, 32);
            this.Hint.TabIndex = 2;
            // 
            // Connected
            // 
            this.Connected.Location = new System.Drawing.Point(40, 265);
            this.Connected.Name = "Connected";
            this.Connected.Size = new System.Drawing.Size(131, 32);
            this.Connected.TabIndex = 3;
            this.Connected.Text = "Disconnected";
            // 
            // ServerInfo
            // 
            this.ServerInfo.Location = new System.Drawing.Point(12, 12);
            this.ServerInfo.Name = "ServerInfo";
            this.ServerInfo.Size = new System.Drawing.Size(290, 32);
            this.ServerInfo.TabIndex = 4;
            this.ServerInfo.Click += new System.EventHandler(this.label1_Click);
            // 
            // notifyIcon1
            // 
            this.notifyIcon1.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyIcon1.Icon")));
            this.notifyIcon1.Text = "EasySync";
            this.notifyIcon1.Visible = true;
            this.notifyIcon1.Click += new EventHandler(notifyIcon1_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(778, 330);
            this.Controls.Add(this.ServerInfo);
            this.Controls.Add(this.Connected);
            this.Controls.Add(this.Hint);
            this.Controls.Add(this.btncopy);
            this.Controls.Add(this.content);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "Form1";
            this.Text = "EasySync";
            this.Load += new System.EventHandler(this.Form1_Load);

            this.Resize += new System.EventHandler(this.Form1_Resize);
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
    }
}

