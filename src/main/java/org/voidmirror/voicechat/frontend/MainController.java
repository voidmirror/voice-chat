package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.events.BaseEvent;
import org.voidmirror.voicechat.events.EventHandler;
import org.voidmirror.voicechat.events.EventService;
import org.voidmirror.voicechat.events.EventType;
import org.voidmirror.voicechat.misc.ComponentInitializer;
import org.voidmirror.voicechat.misc.RuntimeConfig;
import org.voidmirror.voicechat.service.ClockService;
import org.voidmirror.voicechat.service.ContactService;
import org.voidmirror.voicechat.voice.LineHolder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

@Slf4j
public class MainController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private AnchorPane contactsListPane;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Button btnMinimize;
    @FXML
    private ToggleButton btnContacts;
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

    private boolean isContactsListShown = false;
    private HashMap<String, StageParams> stageHolder = new HashMap<>();

    public void initialize() {
        onDragMainWindow();
        setUpEventHandler();
        EventService.getInstance().enableEventReceiver();

        frontSwitcher = FrontSwitcher.getInstance();
        frontSwitcher
                .addButtonToHolder(btnConnect, btnConnect.getId())
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

    public void onContactsListButtonToggle() {
        if (!isContactsListShown) {
            openContactsList();
        } else {
            closeContactsList();
        }
        isContactsListShown = !isContactsListShown;
    }

    public void onDragMainWindow() {
        backgroundPane.setOnMousePressed(pressEvent -> {
            backgroundPane.setOnMouseDragged(dragEvent -> {
                double x = dragEvent.getScreenX() - pressEvent.getSceneX();
                double y = dragEvent.getScreenY() - pressEvent.getSceneY();
                ((Node) pressEvent.getSource()).getScene().getWindow().setX(x);
                ((Node) pressEvent.getSource()).getScene().getWindow().setY(y);
                for (Map.Entry<String, StageParams> stageEntry : stageHolder.entrySet()) {
                    StageParams stageParams = stageEntry.getValue();
                    stageParams.getStage().setX(x + stageParams.getXShift());
                    stageParams.getStage().setY(y + stageParams.getYShift());
                }
            });
        });
    }

    public void onDragContactsListWindow() {
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

    public void openContactsList() {
        try {
            Stage contactsListStage = StageCreator.create(
                    "contactsList",
                    btnContacts.getScene().getWindow().getX() + backgroundPane.getWidth(),
                    btnContacts.getScene().getWindow().getY(),
                    "Contacts"
            );
            stageHolder.put("contactsList", new StageParams(contactsListStage, backgroundPane.getWidth(), 0));
            contactsListStage.show();
        } catch (IOException e) {
            log.error("Loading ContactsList FXML error: {}", e.getMessage());
        }

    }

    public void closeContactsList() {
        stageHolder.get("contactsList").getStage().close();
    }

    public void onDisconnectClient() {
        closeApp();
    }

    public void onConnect() {
        String getHost = tfHost.getText()
                .replaceAll(" ", "")
                .replaceAll("\\.+", ".");
        String host = Pattern.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", getHost)
                ? getHost.strip()
                : "127.0.0.1";

        btnConnect.setDisable(true);
        tfHost.setDisable(true);
        contactService.addContact("current", host);
        BaseEvent connectionEvent = new BaseEvent();
        connectionEvent.setType(EventType.CONNECT);
        EventService.getInstance().sendEvent(connectionEvent, contactService.getContactIp("current"));
        ClockService.getInstance().sendClockSync();

    }

    public void closeApp() {
        System.exit(0);
    }

}
