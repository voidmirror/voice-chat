package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StageCreator {

    private static HashMap<String, String> scenePathMap = new HashMap<>();

    private static void fillScenePathMap() {
        scenePathMap.put("main", "/main.fxml");
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

    public static void makeStageMovable(Pane pane, String fxml) {
        pane.setOnMousePressed(pressEvent -> {
            pane.setOnMouseDragged(dragEvent -> {
                double x = dragEvent.getScreenX() - pressEvent.getSceneX();
                double y = dragEvent.getScreenY() - pressEvent.getSceneY();
                ((Node) pressEvent.getSource()).getScene().getWindow().setX(x);
                ((Node) pressEvent.getSource()).getScene().getWindow().setY(y);
                for (Map.Entry<String, StageParams> stageEntry : FrontSwitcher.getInstance().getStageHolder().entrySet()) {
                    if (!stageEntry.getKey().equals(fxml)) {
                        StageParams stageParams = stageEntry.getValue();
                        stageParams.getStage().setX(x + stageParams.getXShift());
                        stageParams.getStage().setY(y + stageParams.getYShift());
                    }
                }
            });
        });
    }

}
