package ru.linachan.nemesis;

import ru.linachan.bifrost.BifrostPlugin;
import ru.linachan.bifrost.BifrostSSHAuth;
import ru.linachan.bifrost.BifrostSSHConnection;
import ru.linachan.yggdrasil.service.YggdrasilService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class NemesisEventListener extends YggdrasilService {

    private BifrostSSHConnection serverLink;

    @Override
    protected void onInit() {
        try {
            String serverHost = core.getConfig("NemesisGitHost", "localhost");
            Integer serverPort = Integer.parseInt(core.getConfig("NemesisGitPort", "29418"));
            String serverUser = core.getConfig("NemesisSSHUser", "gerrit2");
            File serverKey = new File(core.getConfig("NemesisSSHKey", "/root/.ssh/id_rsa"));
            String serverPassPhrase = core.getConfig("NemesisSSHPassPhrase", "");

            BifrostSSHAuth serverAuth;
            if (serverPassPhrase.length() > 0) {
                serverAuth = new BifrostSSHAuth(serverKey, serverUser, serverPassPhrase);
            } else {
                serverAuth = new BifrostSSHAuth(serverKey, serverUser);
            }

            core.logInfo("BifrostEventListener: Connecting: ssh://" + serverUser + "@" + serverHost + ":" + serverPort);

            serverLink = core.getPluginManager().getPlugin(BifrostPlugin.class).connect(serverHost, serverPort, serverAuth);
        } catch (IOException e) {
            core.logException(e);
        }
    }

    @Override
    protected void onShutdown() {
        if (serverLink != null) {
            serverLink.disconnect();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader eventReader = new BufferedReader(serverLink.executeCommandReader("gerrit stream-events"));

            while (isRunning()) {
                String eventData = eventReader.readLine();
                if (eventData != null && eventData.length() > 0) {
                    NemesisEvent event = new NemesisEvent(eventData);

                    core.getPluginManager().getPlugin(NemesisPlugin.class).putEvent(event);
                }
            }

        } catch (IOException e) {
            core.logException(e);
        }
    }
}
