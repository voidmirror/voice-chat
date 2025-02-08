package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;

public class StageCreator {

    private static HashMap<String, String> scenePathMap = new HashMap<>();

    private static void fillScenePathMap() {
        scenePathMap.put("contactsList", "/contacts-list.fxml");
        scenePathMap.put("contactsAdd", "/contacts-add.fxml");
    }

    public static Stage create(String fxml, double x, double y, String title) throws IOException {
        if (scenePathMap.isEmpty()) {
            fillScenePathMap();
        }
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setX(x);
        stage.setY(y);
        Parent root = FXMLLoader.load(StageCreator.class.getResource(scenePathMap.get(fxml)));
        Scene scene = new Scene(root);
        stage.setScene(scene);

        return stage;
    }

}
