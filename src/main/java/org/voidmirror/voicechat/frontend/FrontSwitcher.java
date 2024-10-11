package org.voidmirror.voicechat.frontend;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * It is necessary to use class inside Platform.runLater()
 */
public class FrontSwitcher {

    private static FrontSwitcher self;

    private FrontSwitcher() {
    }

    public static FrontSwitcher getInstance() {
        if (self == null) {
            self = new FrontSwitcher();
        }
        return self;
    }

    private FxHolder fxHolder = new FxHolder();

    public FrontSwitcher addButtonToHolder(Button button, String fxId) {
        fxHolder.getFxButtons().put(fxId, button);
        return this;
    }

    public Button getButtonFromHolder(String fxId) {
        return fxHolder.getFxButtons().get(fxId);
    }

    public FrontSwitcher addTextFieldToHolder(TextField textField, String fxId) {
        fxHolder.getFxTextFields().put(fxId, textField);
        return this;
    }

    public TextField getTextFieldFromHolder(String fxId) {
        return fxHolder.getFxTextFields().get(fxId);
    }

    public FrontSwitcher addImageViewToHolder(ImageView imageView, String fxId) {
        fxHolder.getFxImageViews().put(fxId, imageView);
        return this;
    }

    public ImageView getImageViewFromHolder(String fxId) {
        return fxHolder.getFxImageViews().get(fxId);
    }

}

@NoArgsConstructor
class FxHolder {

    @Getter
    private HashMap<String, Button> fxButtons = new HashMap<>();
    @Getter
    private HashMap<String, TextField> fxTextFields = new HashMap<>();
    @Getter
    private HashMap<String, ImageView> fxImageViews = new HashMap<>();


}
