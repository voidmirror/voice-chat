package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ContactAddController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private Button btnAddContact;
    @FXML
    private TextField tfContactIp; // TODO: next change to contact's name if 'online contacts' will be implemented

    public void initialize() {
//        movable();
    }

    /**
     * Makes window to move with all other windows
     */
    public void movable() {
        StageCreator.makeStageMovable(backgroundPane, "contactsAdd");
    }

    public void addContact() {
        closeStage();
    }

    private void closeStage() {
        FrontSwitcher.setAddContactStageShown(false);
        FrontSwitcher.getInstance().getStageHolder().remove("contactsAdd");
        ((Stage) btnAddContact.getScene().getWindow()).close();
    }

}
