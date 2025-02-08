package org.voidmirror.voicechat.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.voidmirror.voicechat.events.BaseEvent;
import org.voidmirror.voicechat.events.EventHandler;
import org.voidmirror.voicechat.events.EventService;
import org.voidmirror.voicechat.events.EventType;
import org.voidmirror.voicechat.misc.ComponentInitializer;
import org.voidmirror.voicechat.misc.RuntimeConfig;
import org.voidmirror.voicechat.model.ChatMessage;
import org.voidmirror.voicechat.model.ConnectionData;
import org.voidmirror.voicechat.service.ClockService;
import org.voidmirror.voicechat.service.ContactService;
import org.voidmirror.voicechat.service.MessageService;
import org.voidmirror.voicechat.udp.UdpChoreographer;
import org.voidmirror.voicechat.voice.LineHolder;

import javax.sound.sampled.FloatControl;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class MainController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
//    @FXML
//    private Button btnStartServer;
//    @FXML
//    private Button btnDisconnectServer;
    @FXML
    private Button btnMinimize;
    @FXML
    private ToggleButton btnMuteMicro;
    @FXML
    private TextField tfHost;
    @FXML
    private ImageView ivServerConnectionStatus;
    @FXML
    private Slider slVolume;
    @FXML
    private Label lblPing;

    private FrontSwitcher frontSwitcher;
    private LineHolder lineHolder;
    private RuntimeConfig runtimeConfig;
    private ContactService contactService;

    public void initialize() {
        onMouseDragEntered();
        setUpEventHandler();
        EventService.getInstance().enableEventReceiver();

        frontSwitcher = FrontSwitcher.getInstance();
        frontSwitcher
                .addButtonToHolder(btnConnect, btnConnect.getId())
//                .addButtonToHolder(btnStartServer, btnStartServer.getId())
//                .addButtonToHolder(btnDisconnectServer, btnDisconnectServer.getId())
                .addButtonToHolder(btnDisconnect, btnDisconnect.getId())

                .addToggleButtonToHolder(btnMuteMicro, btnMuteMicro.getId())

                .addSliderToHolder(slVolume, "volumeSpeakers")

                .addLabelToHolder(lblPing, "lblPing")

                .addImageViewToHolder(ivServerConnectionStatus, ivServerConnectionStatus.getId());

        ComponentInitializer.getInstance().setVolumeSlider(slVolume);

        lineHolder = LineHolder.getInstance();
        runtimeConfig = RuntimeConfig.getInstance();
        contactService = ContactService.getInstance();

        onMicroMuteInit();

    }

    public void setUpEventHandler() {
        Thread thread = new Thread(() -> {
            ConcurrentLinkedQueue<BaseEvent> eventQueue = ComponentInitializer.getInstance().getEventQueue();
            EventHandler eventHandler = EventHandler.getInstance();
            while (Thread.currentThread().isAlive()) {
                if (!eventQueue.isEmpty()) {
                    eventHandler.handle(eventQueue.poll());
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void onMouseDragEntered() {
        backgroundPane.setOnMousePressed(pressEvent -> {
            backgroundPane.setOnMouseDragged(dragEvent -> {
                ((Node) pressEvent.getSource()).getScene().getWindow().setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                ((Node) pressEvent.getSource()).getScene().getWindow().setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    public void onMicroMuteInit() {
        btnMuteMicro.setOnAction(actionEvent -> {
            runtimeConfig.setMicroActive(!btnMuteMicro.isSelected());
        });
    }

    public void onMinimize() {
        ((Stage) btnMinimize.getScene().getWindow()).setIconified(true);
    }

//    public void onConnect() {
//        connect();
//    }

//    public void onServerStart() {
//        int serverLocalPort = 9034;
//        int remotePort = 9033; // same as client receive
//
//        btnStartServer.setDisable(true);
//
//        UdpChoreographer udpChoreographer = new UdpChoreographer();
//        ConnectionData connectionData = new ConnectionData();
//        connectionData.setLocalPort(serverLocalPort);
//        connectionData.setRemotePort(remotePort);
//        udpChoreographer.startUdpServer(connectionData);
//    }

    public void onDisconnectClient() {
        closeApp();
    }

//    public void onDisconnectServer() {
//        closeApp();
//    }

    public void onConnect() {
        String getHost = tfHost.getText()
                .replaceAll(" ", "")
                .replaceAll("\\.+", ".");
        String host = Pattern.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", getHost)
                ? getHost.strip()
                : "127.0.0.1";

//        int localPort = 9033;
//        int serverPort = 9034;

        btnConnect.setDisable(true);
        tfHost.setDisable(true);
        contactService.addContact("current", host);
        BaseEvent connectionEvent = new BaseEvent();
        connectionEvent.setType(EventType.CONNECT);
        EventService.getInstance().sendEvent(connectionEvent, contactService.getContactIp("current"));
        ClockService.getInstance().sendClockSync();

//        UdpChoreographer udpChoreographer = new UdpChoreographer();
//        ConnectionData connectionData = new ConnectionData();
//        connectionData.setLocalPort(localPort);
//        connectionData.setRemotePort(serverPort);
//        connectionData.setRemoteHost(host);
//        udpChoreographer.startUdpClient(connectionData);
    }

    public void closeApp() {
        System.exit(0);
    }

}
