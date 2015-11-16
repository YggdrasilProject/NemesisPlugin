package ru.linachan.nemesis;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class NemesisEventHandler {

    protected YggdrasilCore core;

    public void onInit(YggdrasilCore yggdrasilCore) {
        core = yggdrasilCore;
    }

    public abstract void handleEvent(NemesisEvent event);
}
