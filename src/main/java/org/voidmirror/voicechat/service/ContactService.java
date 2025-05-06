package org.voidmirror.voicechat.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class ContactService {

    private static ContactService self = null;

    private ContactService() {}

    public static ContactService getInstance() {
        if (self == null) {
            self = new ContactService();
        }
        return self;
    }

    private final HashMap<String, String> contactIpMap = new HashMap<>();

    private final ObservableList<String> contactNames = FXCollections.observableArrayList();

    public void addContact(String name, String ip) {
        contactIpMap.put(name, ip);
        contactNames.add(name);
    }

    public String getContactIp(String name) {
        return contactIpMap.get(name);
    }

    public HashMap<String, String> getContactIpMap = new HashMap<>();

    public ObservableList<String> getContactNames() {
        updateContactNameList();
        return contactNames;
    }

    private void updateContactNameList() {
        contactNames.setAll(getContactIpMap.keySet());
    }

    private void loadContactListFromPersistence() {
        // TODO: Not yet implemented
    }

}
