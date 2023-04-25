using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace EasySync
{
    internal class ClientHandler
    {
        StreamWriter writer;
        NetworkStream stream;
        StreamReader reader;
        TcpClient client;
        Form1 myForm;
        List<ClientHandler> allClientList;
        public ClientHandler(TcpClient c, Form1 form, List<ClientHandler> thelist)
        {
            myForm = form;
            client = c;
            allClientList = thelist;
        }

        public void start()
        {
            myForm.updateConnectStatus(true);
            try
            {
                stream = client.GetStream();
                reader = new StreamReader(stream, Encoding.UTF8);

                string line;
                while ((line = reader.ReadLine()) != null)
                {
                    string date = DateTime.Now.ToString("yyyy/MM/dd HH:mm:ss");
                    AppendText(line);

                    // Save to file
                    SaveToFile($"[{date}] {line}" + "---Received");
                }

                myForm.updateConnectStatus(false);
                reader.Close();
                stream.Close();
                client.Close();
                allClientList.Remove(this);
            }
            catch (Exception e)
            {
                reader.Close();
                stream.Close();
                client.Close();
                myForm.updateConnectStatus(false);
                allClientList.Remove(this);
            }
        }

        public void shutdown()
        {
            reader.Close();
            stream.Close();
            client.Close();
            myForm.updateConnectStatus(false);

        }

        private void AppendText(string text)
        {

            myForm.UpdateTextBox(text);

        }

        private void SaveToFile(string line)
        {
            string filePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "easysynclog.txt");
            using (StreamWriter sw = File.AppendText(filePath))
            {
                sw.WriteLine(line);
            }
        }

        public void sendImg(byte[] imageData)
        {
            byte[] newdata = new byte[imageData.Length + 1];
            newdata[0] = 0x00;
            imageData.CopyTo(newdata, 1);

            NetworkStream stream = client.GetStream();
            stream.Write(newdata, 0, newdata.Length);
        }

        public void sendText(string message)
        {
            NetworkStream stream = client.GetStream();

            // 将字符串转换成字节数组
            byte[] data = Encoding.UTF8.GetBytes(message+"\n");
            byte[] newdata = new byte[data.Length + 1];
            newdata[0] = 0x01;
            data.CopyTo(newdata, 1);

            // 发送数据
            stream.Write(newdata, 0, newdata.Length);

            string date = DateTime.Now.ToString("yyyy/MM/dd HH:mm:ss");
            AppendText(message);

            // Save to file
            SaveToFile($"[{date}] {message}" + "---Sent");
        }
    }
}
