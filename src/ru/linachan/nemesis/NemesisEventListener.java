package ru.linachan.nemesis;

import ru.linachan.bifrost.BifrostSSHConnection;
import ru.linachan.yggdrasil.service.YggdrasilService;

import java.io.BufferedReader;
import java.io.IOException;

public class NemesisEventListener extends YggdrasilService {

    private BifrostSSHConnection serverLink;

    @Override
    protected void onInit() {
        serverLink = core.getPluginManager().getPlugin(NemesisPlugin.class).getConnection();
    }

    @Override
    protected void onShutdown() {
        if (serverLink != null) {
            serverLink.disconnect();
        }
    }

    @Override
    public void run() {
        if (serverLink != null) {
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
        } else {
            core.logWarning("NemesisEventListener: No connection available");
        }
    }
}
