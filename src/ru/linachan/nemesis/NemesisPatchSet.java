package ru.linachan.nemesis;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NemesisPatchSet {

    private Integer patchSetNumber;
    private String revision;
    private String ref;
    private NemesisAuthor uploader;
    private Long createdOn;
    private NemesisAuthor author;
    private Boolean isDraft;
    private Long sizeInsertions;
    private Long sizeDeletions;
    private List<NemesisApproval> approvals;

    public NemesisPatchSet(JSONObject patchSetData) {
        JSONObject uploaderData = (JSONObject) patchSetData.get("uploader");
        JSONObject authorData = (JSONObject) patchSetData.get("author");
        JSONArray approvalsData = (JSONArray) patchSetData.get("approvals");

        patchSetNumber = Integer.valueOf((String) patchSetData.get("number"));
        revision = (String) patchSetData.get("revision");
        ref = (String) patchSetData.get("ref");
        uploader = (uploaderData != null) ? new NemesisAuthor(uploaderData) : null;
        createdOn = (Long) patchSetData.get("createdOn");
        author = (authorData != null) ? new NemesisAuthor(authorData) : null;
        isDraft = (Boolean) patchSetData.get("isDraft");
        sizeInsertions = (Long) patchSetData.get("sizeInsertions");
        sizeDeletions = (Long) patchSetData.get("sizeDeletions");

        if (approvalsData != null) {
            approvals = new ArrayList<>();
            for (Object approval : approvalsData) {
                approvals.add(new NemesisApproval((JSONObject) approval));
            }
        }
    }

    public Integer getPatchSetNumber() {
        return patchSetNumber;
    }

    public String getRevision() {
        return revision;
    }

    public String getRef() {
        return ref;
    }

    public NemesisAuthor getUploader() {
        return uploader;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public NemesisAuthor getAuthor() {
        return author;
    }

    public Boolean getIsDraft() {
        return isDraft;
    }

    public Long getSizeInsertions() {
        return sizeInsertions;
    }

    public Long getSizeDeletions() {
        return sizeDeletions;
    }
}
