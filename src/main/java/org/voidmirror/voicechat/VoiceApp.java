package org.voidmirror.voicechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.voidmirror.voicechat.frontend.FrontSwitcher;
import org.voidmirror.voicechat.frontend.StageCreator;
import org.voidmirror.voicechat.frontend.StageParams;

public class VoiceApp extends Application {
    public static void main(String[] args) {
        System.out.println("Current time: " + System.currentTimeMillis());
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));

        primaryStage.setTitle("Voice Chat App");
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root));

        FrontSwitcher.getInstance().getStageHolder().put("main", new StageParams(primaryStage, 0, 0));

        primaryStage.show();

    }
}