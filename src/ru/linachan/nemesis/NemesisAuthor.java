package ru.linachan.nemesis;

import org.json.simple.JSONObject;

public class NemesisAuthor {

    private String name;
    private String eMail;
    private String userName;

    public NemesisAuthor(JSONObject authorData) {
        name = (String) authorData.get("name");
        eMail = (String) authorData.get("email");
        userName = (String) authorData.get("username");
    }

    public String getName() {
        return name;
    }

    public String getEMail() {
        return eMail;
    }

    public String getUserName() {
        return userName;
    }
}
