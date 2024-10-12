module voice.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    requires org.slf4j;
    requires static lombok;
    exports org.voidmirror.voicechat;
    exports org.voidmirror.voicechat.model;
    opens org.voidmirror.voicechat.frontend to javafx.fxml;
}