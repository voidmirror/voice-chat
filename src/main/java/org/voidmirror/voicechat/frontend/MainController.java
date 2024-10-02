package org.voidmirror.voicechat.frontend;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class MainController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnServerStart;
    @FXML
    private Button btnDisconnectServer;
    @FXML
    private TextField tfHost;

    public void initialize() {
        onMouseDragEntered();
    }

    public void onMouseDragEntered() {
        backgroundPane.setOnMousePressed(pressEvent -> {
            backgroundPane.setOnMouseDragged(dragEvent -> {
                ((Node) pressEvent.getSource()).getScene().getWindow().setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                ((Node) pressEvent.getSource()).getScene().getWindow().setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    public void onConnect() {
        connect();
    }

    public void onServerStart() {
        int port = 9009;
        Thread receive = new Thread(new Sender(port));
        receive.start();
    }

    public void onDisconnectClient() {
        closeApp();
    }

    public void onDisconnectServer() {
        closeApp();
    }

    private void connect() {
        String getHost = tfHost.getText();
        String host = Pattern.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+", getHost) ? getHost : "127.0.0.1";
        System.out.println("### Host is " + host);
        int port = 9009;
        Thread receive = new Thread(new Receiver(host, port));
        receive.start();
    }

    public void closeApp() {
        System.exit(0);
    }

}

class Receiver implements Runnable {

    int port;
    String host;
    Socket socket;
    SourceDataLine speakers;
    TargetDataLine microphone = null;

    public Receiver(String host, int port) {
        this.port = port;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            InputStream in = socket.getInputStream();
            AudioFormat format = new AudioFormat(
                    16000, 16, 2, true, true);
            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format);
            speakers.start();

            System.out.println(inInfo);
            System.out.println(speakers);

            OutputStream out = null;
            out = socket.getOutputStream();
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info outInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(outInfo);
            microphone.open(format);
            microphone.start();

            System.out.println(outInfo);
            System.out.println(microphone);

            byte[] inputBuffer = new byte[1024];
            byte[] outputBuffer = new byte[1024];

            int bufferVarInput = 0;
            int bufferVarOutput = 0;

            while (
                    ((bufferVarInput = in.read(inputBuffer)) > 0 || (bufferVarOutput = microphone.read(outputBuffer, 0, 1024)) > 0)
            ) {
                out.write(outputBuffer, 0, bufferVarOutput);
                speakers.write(inputBuffer, 0, bufferVarInput);
            }

        } catch (IOException | LineUnavailableException e) {
            System.out.println("### Runtime exception");
            throw new RuntimeException(e);
        }

    }
}

class Sender implements Runnable {

    int port;
    ServerSocket socket;
    SourceDataLine speakers;
    TargetDataLine microphone = null;
    Socket client = null;

    public Sender(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(port);
            client = socket.accept();

            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);

            InputStream in = client.getInputStream();
            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format);
            speakers.start();

            System.out.println(inInfo);
            System.out.println(speakers);

            OutputStream out = client.getOutputStream();
            DataLine.Info outInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(outInfo);
            microphone.open(format);
            microphone.start();

            System.out.println(outInfo);
            System.out.println(microphone);

            byte[] inputBuffer = new byte[1024];
            byte[] outputBuffer = new byte[1024];

            int bufferVarInput = 0;
            int bufferVarOutput = 0;

            while (
                    ((bufferVarOutput = microphone.read(outputBuffer, 0, 1024)) > 0 || (bufferVarInput = in.read(inputBuffer)) > 0)
            ) {
                out.write(outputBuffer, 0, bufferVarOutput);
                speakers.write(inputBuffer, 0, bufferVarInput);
            }


        } catch (IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}