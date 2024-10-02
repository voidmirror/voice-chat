module voice.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    exports org.voidmirror.voicechat;
    opens org.voidmirror.voicechat.frontend to javafx.fxml;
}