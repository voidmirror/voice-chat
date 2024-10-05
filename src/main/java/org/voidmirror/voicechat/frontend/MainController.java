package org.voidmirror.voicechat.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.Arrays;
import java.util.regex.Pattern;

public class MainController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnStartServer;
    @FXML
    private Button btnDisconnectServer;
    @FXML
    private TextField tfHost;
    @FXML
    private ImageView connectionStatus;


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
        Thread send = new Thread(new Sender(port, connectionStatus, btnStartServer));
        send.setDaemon(true);
        send.start();
    }

    public void onDisconnectClient() {
        closeApp();
    }

    public void onDisconnectServer() {
        closeApp();
    }

    private void connect() {
        String getHost = tfHost.getText()
                .replaceAll(" ", "")
                .replaceAll("\\.+", ".");
        String host = Pattern.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", getHost)
                ? getHost
                : "127.0.0.1";
        System.out.println("### Host is " + host);
        int port = 9009;
        Thread receive = new Thread(new Receiver(host, port, btnConnect, tfHost));
        receive.setDaemon(true);
        receive.start();
    }

    public void closeApp() {
        System.exit(0);
    }

}

class Receiver implements Runnable {

    private final int port;
    private final String host;
    private SourceDataLine speakers;
    private TargetDataLine microphone = null;
    private final Button btnConnect;
    private final TextField tfHost;

    public Receiver(String host, int port, Button btnConnect, TextField tfHost) {
        this.port = port;
        this.host = host;
        this.btnConnect = btnConnect;
        this.tfHost = tfHost;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host, port);

            InputStream in = socket.getInputStream();
            AudioFormat format = new AudioFormat(
                    16000, 16, 2, true, true);

            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format);
            speakers.start();

            OutputStream out = socket.getOutputStream();
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info outInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(outInfo);
            microphone.open(format);
            microphone.start();

            Thread speakerThread = new Thread(() -> {
                byte[] inputBuffer = new byte[8192];
                int bufferVarInput = 0;
                try {
                    while ((bufferVarInput = in.read(inputBuffer)) > 0) {
                        if (((bufferVarInput & (bufferVarInput - 1)) == 0)) {
                            speakers.write(
                                    inputBuffer,
                                    0,
                                    bufferVarInput
                            );
                        }
                    }
                } catch (IOException e) {
                    System.out.println("### IO read exception");
                }
            });
            Thread microphoneThread = new Thread(new Runnable() {
                final byte[] outputBuffer = new byte[8192];
                int bufferVarOutput = 0;
                @Override
                public void run() {
                    try {
                        while ((bufferVarOutput = microphone.read(outputBuffer, 0, 8192)) > 0 ) {
                            out.write(outputBuffer, 0, bufferVarOutput);
                        }
                    } catch (IOException e) {
                        System.out.println("### IO read exception");
                    }
                }
            });
            speakerThread.setDaemon(true);
            microphoneThread.setDaemon(true);
            speakerThread.start();
            microphoneThread.start();

            Platform.runLater(() -> {
                btnConnect.setDisable(true);
                tfHost.setDisable(true);
            });

        } catch (IOException | LineUnavailableException e) {
            System.out.println("### Runtime exception");
            throw new RuntimeException(e);
        }

    }
}

class Sender implements Runnable {

    private final int port;
    private SourceDataLine speakers;
    private TargetDataLine microphone = null;
    private final ImageView connectionStatus;
    private final Button btnStartServer;

    public Sender(int port, ImageView connectionStatus, Button btnStartServer) {
        this.port = port;
        this.connectionStatus = connectionStatus;
        this.btnStartServer = btnStartServer;
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
            Platform.runLater(() -> {
                connectionStatus.setImage(new Image(getClass().getResourceAsStream("/assets/done.png")));
                btnStartServer.setDisable(true);
            });
            Socket client = socket.accept();

            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);

            InputStream in = client.getInputStream();
            DataLine.Info inInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(inInfo);
            speakers.open(format);
            speakers.start();

            System.out.println(speakers.getFormat());
            System.out.println(speakers.getLineInfo());
            System.out.println(speakers.getBufferSize());
            System.out.println(speakers.getLevel());
            System.out.println(Arrays.toString(speakers.getControls()));

            System.out.println(inInfo);
            System.out.println(speakers);

            OutputStream out = client.getOutputStream();
            DataLine.Info outInfo = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(outInfo);
            microphone.open(format);
            microphone.start();

            Thread speakerThread = new Thread(() -> {
                byte[] inputBuffer = new byte[8192];
                int bufferVarInput = 0;
                try {
                    while ((bufferVarInput = in.read(inputBuffer)) > 0) {
                        if (((bufferVarInput & (bufferVarInput - 1)) == 0)) {
                            speakers.write(
                                    inputBuffer,
                                    0,
                                    bufferVarInput
                            );
                        }
                    }
                } catch (IOException e) {
                    System.out.println("### IO read exception");
                }
            });
            Thread microphoneThread = new Thread(new Runnable() {
                final byte[] outputBuffer = new byte[8192];
                int bufferVarOutput = 0;
                @Override
                public void run() {
                    try {
                        while ((bufferVarOutput = microphone.read(outputBuffer, 0, 8192)) > 0) {
                            out.write(outputBuffer, 0, bufferVarOutput);
                        }
                    } catch (IOException e) {
                        System.out.println("### IO read exception");
                    }
                }
            });
            speakerThread.setDaemon(true);
            microphoneThread.setDaemon(true);
            speakerThread.start();
            microphoneThread.start();

        } catch (IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}