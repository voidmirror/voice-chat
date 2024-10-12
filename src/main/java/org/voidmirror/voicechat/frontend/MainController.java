package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.voidmirror.voicechat.model.ConnectionData;
import org.voidmirror.voicechat.udp.UdpChoreographer;

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
    private Button btnMinimize;
    @FXML
    private TextField tfHost;
    @FXML
    private ImageView ivServerConnectionStatus;

    private FrontSwitcher frontSwitcher;


    public void initialize() {
        onMouseDragEntered();

        frontSwitcher = FrontSwitcher.getInstance();
        frontSwitcher
                .addButtonToHolder(btnConnect, btnConnect.getId())
                .addButtonToHolder(btnStartServer, btnStartServer.getId())
                .addButtonToHolder(btnDisconnectServer, btnDisconnectServer.getId())
                .addButtonToHolder(btnDisconnect, btnDisconnect.getId())

                .addImageViewToHolder(ivServerConnectionStatus, ivServerConnectionStatus.getId());
    }

    public void onMouseDragEntered() {
        backgroundPane.setOnMousePressed(pressEvent -> {
            backgroundPane.setOnMouseDragged(dragEvent -> {
                ((Node) pressEvent.getSource()).getScene().getWindow().setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                ((Node) pressEvent.getSource()).getScene().getWindow().setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    public void onMinimize() {
        ((Stage) btnMinimize.getScene().getWindow()).setIconified(true);
    }

    public void onConnect() {
        connect();
    }

    public void onServerStart() {
        int serverLocalPort = 9034;
        int remotePort = 9033; // same as client receive

        btnStartServer.setDisable(true);

        UdpChoreographer udpChoreographer = new UdpChoreographer();
        ConnectionData connectionData = new ConnectionData();
        connectionData.setLocalPort(serverLocalPort);
        connectionData.setRemotePort(remotePort);
        udpChoreographer.startUdpServer(connectionData);
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
        String host = Pattern.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", getHost)
                ? getHost.strip()
                : "127.0.0.1";

        int localPort = 9033;
        int serverPort = 9034;

        btnConnect.setDisable(true);
        tfHost.setDisable(true);

        UdpChoreographer udpChoreographer = new UdpChoreographer();
        ConnectionData connectionData = new ConnectionData();
        connectionData.setLocalPort(localPort);
        connectionData.setRemotePort(serverPort);
        connectionData.setRemoteHost(host);
        udpChoreographer.startUdpClient(connectionData);
    }

    public void closeApp() {
        System.exit(0);
    }

}
