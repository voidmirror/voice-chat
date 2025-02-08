package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ContactsListController {

    @FXML
    private Button btnAddContact;
    @FXML
    private Button btnEditContact;



    public void onAddContact() {
        if (!FrontSwitcher.isAddContactStageShown()) {
            FrontSwitcher.setAddContactStageShown(true);
            try {
                Stage addContactStage = StageCreator.create("contactsAdd",
                        btnAddContact.getScene().getWindow().getX(),
                        btnAddContact.getScene().getWindow().getY() + btnAddContact.getScene().getWindow().getHeight(),
                        "Add contact");
                addContactStage.show();
            } catch (IOException e) {
                log.error("Loading ContactsList FXML error: {}", e.getMessage());
            }
        }
    }

}
