package org.voidmirror.voicechat.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.voidmirror.voicechat.service.ContactService;

public class ContactAddController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private Button btnAddContact;
    @FXML
    private TextField tfContactIp; // TODO: next change to contact's name if 'online contacts' will be implemented
    @FXML
    private TextField tfContactName;

    private ContactService contactService;

    public void initialize() {
//        movable();

        // init services
        contactService = ContactService.getInstance();
    }

    /**
     * Makes window to move with all other windows
     */
    public void movable() {
        StageCreator.makeStageMovable(backgroundPane, "contactsAdd");
    }

    public void addContact() {
        // TODO: not yet implemented
        String name = tfContactName.getText();
        String ip = tfContactIp.getText();
        contactService.addContact(name, ip);
        closeStage();
    }

    public void cancel() {
        closeStage();
    }

    private void closeStage() {
        FrontSwitcher.setAddContactStageShown(false);
        FrontSwitcher.getInstance().getStageHolder().remove("contactsAdd");
        ((Stage) btnAddContact.getScene().getWindow()).close();
    }

}
