package ru.linachan.nemesis;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class NemesisEvent {

    private NemesisEventType eventType;
    private NemesisChangeRequest changeRequest = null;
    private NemesisPatchSet patchSet = null;
    private NemesisAuthor author = null;
    private String comment;
    private Long eventTime;
    private List<NemesisApproval> approvals;

    public NemesisEvent(String eventData) {
        try {
            JSONObject eventObject = (JSONObject) new JSONParser().parse(eventData);

            eventType = NemesisEventType.getEventType((String) eventObject.get("type"));

            if (eventObject.containsKey("change")) {
                changeRequest = new NemesisChangeRequest((JSONObject) eventObject.get("change"));
            }

            if (eventObject.containsKey("patchSet")) {
                patchSet = new NemesisPatchSet((JSONObject) eventObject.get("patchSet"));
            }

            switch(eventType) {
                case CHANGE_ABANDONED:
                    author = new NemesisAuthor((JSONObject) eventObject.get("abandoner"));
                    comment = (String) eventObject.get("reason");
                    eventTime = (Long) eventObject.get("eventCreatedOn");
                    break;
                case CHANGE_MERGED:
                    author = new NemesisAuthor((JSONObject) eventObject.get("submitter"));
                    comment = (String) eventObject.get("newRev");
                    eventTime = (Long) eventObject.get("eventCreatedOn");
                    break;
                case CHANGE_RESTORED:
                    author = new NemesisAuthor((JSONObject) eventObject.get("restorer"));
                    comment = (String) eventObject.get("reason");
                    eventTime = (Long) eventObject.get("eventCreatedOn");
                    break;
                case COMMENT_ADDED:
                    JSONArray approvalsData = (JSONArray) eventObject.get("approvals");

                    author = new NemesisAuthor((JSONObject) eventObject.get("author"));
                    comment = (String) eventObject.get("comment");
                    eventTime = (Long) eventObject.get("eventCreatedOn");

                    if (approvalsData != null) {
                        approvals = new ArrayList<>();
                        for (Object approval : approvalsData) {
                            approvals.add(new NemesisApproval((JSONObject) approval));
                        }
                    }
                    break;
                case MERGE_FAILED:
                    author = new NemesisAuthor((JSONObject) eventObject.get("submitter"));
                    comment = (String) eventObject.get("reason");
                    eventTime = (Long) eventObject.get("eventCreatedOn");
                    break;
                case PATCHSET_CREATED:
                    author = new NemesisAuthor((JSONObject) eventObject.get("uploader"));
                    eventTime = (Long) eventObject.get("eventCreatedOn");
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public NemesisEventType getEventType() {
        return eventType;
    }

    public NemesisChangeRequest getChangeRequest() {
        return changeRequest;
    }

    public NemesisPatchSet getPatchSet() {
        return patchSet;
    }

    public NemesisAuthor getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public List<NemesisApproval> getApprovals() {
        return approvals;
    }

    public String toString() {
        String representation = "";

        representation += (author != null) ? author.getName() + " " : "";
        representation += "[" + eventType + "]";
        representation += (changeRequest != null) ? " " + changeRequest.getProject() + " :: " + changeRequest.getSubject() : "";

        return representation;
    }
}
