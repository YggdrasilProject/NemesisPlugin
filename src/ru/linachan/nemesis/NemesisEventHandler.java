package ru.linachan.nemesis;

import ru.linachan.bifrost.BifrostPlugin;
import ru.linachan.bifrost.BifrostSSHAuth;
import ru.linachan.bifrost.BifrostSSHConnection;
import ru.linachan.bifrost.BifrostSSHException;
import ru.linachan.yggdrasil.service.YggdrasilService;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class NemesisEventHandler extends YggdrasilService {

    private String serverHost;
    private Integer serverPort;
    private BifrostSSHAuth serverAuth;

    @Override
    protected void onInit() {
        serverHost = core.getConfig("NemesisGitHost", "localhost");
        serverPort = Integer.parseInt(core.getConfig("NemesisGitPort", "29418"));
        String serverUser = core.getConfig("NemesisSSHUser", "gerrit2");
        File serverKey = new File(core.getConfig("NemesisSSHKey", "/root/.ssh/id_rsa"));
        String serverPassPhrase = core.getConfig("NemesisSSHPassPhrase", "");

        if (serverPassPhrase.length() > 0) {
            serverAuth = new BifrostSSHAuth(serverKey, serverUser, serverPassPhrase);
        } else {
            serverAuth = new BifrostSSHAuth(serverKey, serverUser);
        }

        core.logInfo("BifrostEventListener: Connecting: ssh://" + serverUser + "@" + serverHost + ":" + serverPort);
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public void run() {
        while (isRunning()) {
            NemesisEvent event = core.getPluginManager().getPlugin(NemesisPlugin.class).getEvent();
            if (event != null) {
                handleEvent(event);
            }
        }
    }

    private void handleEvent(NemesisEvent event) {
        core.logInfo("NemesisEvent: " + event.getChangeRequest().getProject() + " : " + event.getEventType().getEventType());
        if (event.getEventType() == NemesisEventType.COMMENT_ADDED) {
            if (Pattern.matches("^(Patch Set [0-9]+:\\n\\n)?\\s*verify\\s*$", event.getComment())) {
                core.logInfo("Voting...");
                review(event, NemesisApprovalType.VERIFIED, 1);
            }
        }
    }

    public void review(NemesisEvent event, NemesisApprovalType approval, Integer value) {
        try {
            BifrostSSHConnection serverLink = core.getPluginManager().getPlugin(BifrostPlugin.class).connect(serverHost, serverPort, serverAuth);

            Integer changeNumber = event.getChangeRequest().getChangeNumber();
            Integer patchSetNumber = event.getPatchSet().getPatchSetNumber();
            String approvalOption;

            switch(approval) {
                case CODE_REVIEW:
                    approvalOption = "--code-review";
                    break;
                case VERIFIED:
                    approvalOption = "--verified";
                    break;
                case WORKFLOW:
                    approvalOption = "--workflow";
                    break;
                default:
                    approvalOption = "";
                    break;
            }
            String approvalCommand = "gerrit review " + changeNumber + "," + patchSetNumber + " " + approvalOption + " " + value + " --submit";

            core.logInfo("NemesisEvent: Executing '" + approvalCommand + "'");

            serverLink.executeCommand(approvalCommand);
        } catch (IOException e) {
            core.logException(e);
        }
    }
}
