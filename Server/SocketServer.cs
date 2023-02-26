using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;
using System.Threading;

namespace EasySync
{
    internal class SocketServer
    {
        private TcpListener listener;

        private Form1 myForm;
        List<ClientHandler> ClientList = new List<ClientHandler>();

        public SocketServer(Form1 mainForm)
        {
            this.myForm = mainForm;
            Thread serverThread = new Thread(() =>
            {
                start();
            });
            serverThread.Start();
        }

        public void start()
        {
            try
            {
                IPAddress ipAddress = GetLocalIPAddress();
                int port = 30291;
                listener = new TcpListener(ipAddress, port);
                listener.Start();
                //AppendText($"Server started on port {port}.");
                myForm.updateServerInfo(ipAddress + " : " + port);

                while (true)
                {
                    TcpClient client = listener.AcceptTcpClient();
                    //AppendText($"Client connected: {client.Client.RemoteEndPoint}");
                    Thread clientThread = new Thread(() =>
                    {
                        HandleClient(client);
                    });
                    clientThread.Start();
                }
            }
            catch (SocketException ex)
            {
                if (ex.ErrorCode == 10048)
                {
                    myForm.PortNotAvilable();
                }
            }
            catch (Exception e)
            {
                shutdown();
            }
            finally
            {
                shutdown();
            }
        }

        public void shutdown()
        {
            try
            {
                if (listener != null) {
                    listener.Stop();
                }
                foreach (ClientHandler ch  in ClientList)
                {
                    if (ch != null) {
                        ch.shutdown();
                    }
                }
                ClientList.Clear();
            } catch(Exception e)
            {

            }
        }


        IPAddress GetLocalIPAddress()
        {
            var host = Dns.GetHostEntry(Dns.GetHostName());

            foreach (var ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork && !IPAddress.IsLoopback(ip))
                {
                    return ip;
                }
            }

            throw new Exception("No network adapters with an IPv4 address in the system!");
        }

        private void HandleClient(TcpClient client)
        {
            ClientHandler ch = new ClientHandler(client, myForm, ClientList);
            ClientList.Add(ch);
            ch.start();
        }

    }

    
}
