package ru.linachan.nemesis;

import org.json.simple.JSONObject;

public class NemesisApproval {

    private NemesisApprovalType type;
    private String description;
    private Integer value;

    public NemesisApproval(JSONObject approvalData) {
        type = NemesisApprovalType.getApprovalType((String) approvalData.get("type"));
        description = (String) approvalData.get("description");
        value = Integer.valueOf((String) approvalData.get("value"));
    }

    public NemesisApprovalType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Integer getValue() {
        return value;
    }

    public String toString() {
        return type.toString() + ": " + value;
    }
}
