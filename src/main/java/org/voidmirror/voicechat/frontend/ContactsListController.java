package org.voidmirror.voicechat.frontend;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.voidmirror.voicechat.model.Contact;
import org.voidmirror.voicechat.service.ContactService;

import java.io.IOException;

@Slf4j
public class ContactsListController {

    @FXML
    private AnchorPane backgroundPane;
    @FXML
    private ListView<String> contactListView;
    @FXML
    private Button btnAddContact;
    @FXML
    private Button btnEditContact;

    private ContactService contactService;

    public void initialize() {
//        movable();

        // init services
        contactService = ContactService.getInstance();

        initializeContactList();
    }

    /**
     * Makes window to move with all other windows
     */
    public void movable() {
        StageCreator.makeStageMovable(backgroundPane, "contactsList");
    }

    private void initializeContactList() {
        contactListView.setItems(contactService.getContactNames());
    }

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
