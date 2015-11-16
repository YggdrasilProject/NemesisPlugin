package ru.linachan.nemesis;

import ru.linachan.bifrost.BifrostPlugin;
import ru.linachan.bifrost.BifrostSSHAuth;
import ru.linachan.bifrost.BifrostSSHConnection;
import ru.linachan.yggdrasil.component.YggdrasilPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NemesisPlugin extends YggdrasilPlugin {

    private String serverHost;
    private Integer serverPort;
    private BifrostSSHAuth serverAuth;

    private List<NemesisEventHandler> eventHandlers;

    @Override
    protected void setUpDependencies() {
        dependsOn(BifrostPlugin.class);
    }

    @Override
    protected void onInit() {
        eventHandlers = new ArrayList<>();

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

        core.getServiceManager().startService(new NemesisEventListener());
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
        return true;
    }

    public void registerHandler(NemesisEventHandler handler) {
        eventHandlers.add(handler);
        handler.onInit(core);
    }

    public void handleEvent(NemesisEvent event) {
        core.logInfo("NemesisEvent: " + event.toString());
        for (NemesisEventHandler handler: eventHandlers) {
            handler.handleEvent(event);
        }
    }

    public BifrostSSHConnection getConnection() {
        try {
            return core.getPluginManager().getPlugin(BifrostPlugin.class).connect(serverHost, serverPort, serverAuth);
        } catch (IOException e) {
            core.logException(e);
            return null;
        }
    }

    public void reviewChange(NemesisEvent event, NemesisApprovalType approval, Integer value, boolean trySubmit, String comment) {
        try {
            BifrostSSHConnection serverLink = getConnection();

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

            String approvalCommand = "gerrit review";
            approvalCommand += " " + changeNumber + "," + patchSetNumber;
            approvalCommand += " " + approvalOption + " " + value;
            approvalCommand += (comment != null) ? " --comment " + comment + " ": "";
            approvalCommand += (trySubmit) ? " --submit" : "";

            core.logInfo("NemesisEvent: Voting " + approval.toString() + ": " + value + " for " + changeNumber + "," + patchSetNumber);

            serverLink.executeCommand(approvalCommand);
        } catch (IOException e) {
            core.logException(e);
        }
    }
}
