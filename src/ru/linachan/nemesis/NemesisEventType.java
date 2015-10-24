package ru.linachan.nemesis;

public enum NemesisEventType {
    CHANGE_ABANDONED("change-abandoned"),
    CHANGE_MERGED("change-merged"),
    CHANGE_RESTORED("change-restored"),
    COMMENT_ADDED("comment-added"),
    MERGE_FAILED("merge-failed"),
    PATCHSET_CREATED("patchset-created"),
    REVIEWER_ADDED("reviewer-added"),
    REF_UPDATED("ref-updated");

    private String eventType;

    NemesisEventType(String eventType) {
         this.eventType = eventType;
    }

    public static NemesisEventType getEventType(String eventType) {
        return NemesisEventType.valueOf(eventType.toUpperCase().replace("-", "_"));
    }

    public String getEventType() {
        return eventType;
    }
}
