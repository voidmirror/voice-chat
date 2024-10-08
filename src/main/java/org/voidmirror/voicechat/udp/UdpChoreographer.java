package org.voidmirror.voicechat.udp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class UdpChoreographer {

    public void findServer(String host, int remotePort) {
        Thread thread = new Thread(() -> {
            try {
                Socket socket = new Socket(host, remotePort);

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                String hello = "Client Hello";

                out.write(hello.getBytes(), 0, hello.getBytes().length);
                byte[] serverPermission = new byte[128];
                in.read(serverPermission);
                System.out.println(new String(serverPermission));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

    }

    public void waitConnection(int port) {
        Thread thread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Waiting on port " + port);
                Socket socket = serverSocket.accept();

                InputStream in = socket.getInputStream();
                byte[] clientInfo = new byte[1024];
                int inputBuffer = in.read(clientInfo);
                System.out.println(new String(clientInfo));
                System.out.println(socket.getRemoteSocketAddress());
                System.out.println(socket.getInetAddress()); // right way
                OutputStream out = socket.getOutputStream();
                String permission = "let's go";
                out.write(permission.getBytes(), 0, permission.getBytes().length);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

    }

    public void connectionWatcher(Thread thread,)

}
