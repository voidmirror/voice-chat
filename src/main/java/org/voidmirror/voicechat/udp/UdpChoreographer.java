package org.voidmirror.voicechat.udp;

import org.voidmirror.voicechat.model.ConnectionData;
import org.voidmirror.voicechat.model.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class UdpChoreographer {

    public void startUdpServer(ConnectionData connectionData) {

//        CompletableFuture<Boolean> start = new CompletableFuture<>();
        ConnectionState state = new ConnectionState();
        try {
            DatagramSocket datagramSocketFirstReceive = new DatagramSocket(connectionData.getLocalPort());
            final byte[] udpInputBuffer = new byte[128];
            DatagramPacket dpRec = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            DatagramSocket datagramSocketFirstSend = new DatagramSocket();

            System.out.println("### Socket created");

            CompletableFuture<SocketAddress> start = CompletableFuture.supplyAsync(() -> {
                try {
//                    System.out.println("### Future async receive waiting");
                    datagramSocketFirstReceive.receive(dpRec);
//                    System.out.println("### Future async Received");
                    connectionData.setRemoteHost(String.valueOf(dpRec.getSocketAddress()));
                    System.out.println(connectionData);
                    byte[] connectionPermission = "connection available".getBytes();
                    System.out.println(Arrays.toString(connectionPermission));
                    DatagramPacket dpSend = new DatagramPacket(
                            connectionPermission, connectionPermission.length,
                            InetAddress.getByName(connectionData.getRemoteHost()), connectionData.getRemotePort());
                    System.out.println(dpSend.toString());
//                    System.out.println("### Future async Received Before Sleep");
//                    Thread.sleep(2000);
//                    System.out.println("### Future async Received After Sleep");
                    datagramSocketFirstSend.send(dpSend);
//                    System.out.println("### Future async After send");

                    return dpRec.getSocketAddress();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            start.whenCompleteAsync((address, throwable) -> {
//                System.out.println("Address: " + address.toString());
//                System.out.println("When Complete Async started");
                datagramSocketFirstReceive.close();
                datagramSocketFirstSend.close();
//                System.out.println(address.toString());
                System.out.println(connectionData);
//                connectionData.setRemoteHost(address.toString());
                Thread receiver = new Thread(new UdpReceiver(connectionData.getLocalPort()));
                Thread sender = new Thread(new UdpSender(connectionData.getRemotePort(), connectionData.getRemoteHost()));
                receiver.setDaemon(true);
                sender.setDaemon(true);
                receiver.start();
                sender.start(); //TODO: setDaemon(true)
            });
            System.out.println("### Future async Created");
//            start.get();  // blocking
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    public void startUdpClient(ConnectionData connectionData) {
        ConnectionState state = new ConnectionState();
        try {

            DatagramSocket datagramSocketFirstReceive = new DatagramSocket(connectionData.getLocalPort());
            final byte[] udpInputBuffer = new byte[128];
            DatagramPacket dpRec = new DatagramPacket(udpInputBuffer, udpInputBuffer.length);

            DatagramSocket datagramSocketFirstSend = new DatagramSocket();

            final byte[] udpOutputBuffer = new byte[128];

            DatagramPacket dpSend = new DatagramPacket(
                    udpOutputBuffer, udpOutputBuffer.length,
                    InetAddress.getByName(connectionData.getRemoteHost()), connectionData.getRemotePort());

            System.out.println("### Socket created");
            CompletableFuture<String> pingServer = CompletableFuture.supplyAsync(() -> {
//                while (state.isState() != true)
                try {
                    System.out.println("### DataPacket ready");
                    datagramSocketFirstSend.send(dpSend);
                    System.out.println("### DataPacket sent, waiting for receive");
                    datagramSocketFirstReceive.receive(dpRec);
                    System.out.println("### DataPacket received");
                    System.out.println(new String(dpRec.getData()));

                    return "connection successful";
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            pingServer.whenComplete((str, throwable) -> {
                Thread receiver = new Thread(new UdpReceiver(connectionData.getLocalPort()));
                Thread sender = new Thread(new UdpSender(connectionData.getRemotePort(), connectionData.getRemoteHost()));
                receiver.setDaemon(true);
                sender.setDaemon(true);
                receiver.start();
                sender.start();
            });
//            pingServer.get(); // blocking


        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }



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

//    public void connectionWatcher(Thread thread,)

}
