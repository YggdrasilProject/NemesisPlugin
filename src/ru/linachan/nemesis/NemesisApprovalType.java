package ru.linachan.nemesis;

public enum NemesisApprovalType {
    CODE_REVIEW("Code-Review"),
    VERIFIED("Verified"),
    WORKFLOW("Workflow");

    private String approvalType;

    NemesisApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public static NemesisApprovalType getApprovalType(String approvalType) {
        return NemesisApprovalType.valueOf(approvalType.toUpperCase().replace("-", "_"));
    }

    public String getApprovalType() {
        return approvalType;
    }
}
