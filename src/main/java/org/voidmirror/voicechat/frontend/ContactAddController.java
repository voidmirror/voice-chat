package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ContactAddController {

    @FXML
    private Button btnAddContact;
    @FXML
    private TextField tfContactIp; // TODO: next change to contact's name if 'online contacts' will be implemented

    public void addContact() {
        closeStage();
    }

    private void closeStage() {
        FrontSwitcher.setAddContactStageShown(false);
        ((Stage) btnAddContact.getScene().getWindow()).close();
    }

}
