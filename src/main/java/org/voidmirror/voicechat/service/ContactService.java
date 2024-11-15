package org.voidmirror.voicechat.service;

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

    private HashMap<String, String> contactIpMap = new HashMap<>();

    public void addContact(String name, String ip) {
        contactIpMap.put(name, ip);
    }

    public String getContactIp(String name) {
        return contactIpMap.get(name);
    }

}
