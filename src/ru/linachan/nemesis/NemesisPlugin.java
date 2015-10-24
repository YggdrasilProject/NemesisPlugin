package ru.linachan.nemesis;

import ru.linachan.bifrost.BifrostPlugin;
import ru.linachan.yggdrasil.component.YggdrasilPlugin;

import java.util.PriorityQueue;
import java.util.Queue;

public class NemesisPlugin extends YggdrasilPlugin {

    private Queue<NemesisEvent> eventQueue;

    @Override
    protected void setUpDependencies() {
        dependsOn(BifrostPlugin.class);
    }

    @Override
    protected void onInit() {
        eventQueue = new PriorityQueue<>();

        core.getServiceManager().startService(new NemesisEventListener());
        core.getServiceManager().startService(new NemesisEventHandler());
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
        return true;
    }

    public void putEvent(NemesisEvent event) {
        eventQueue.add(event);
    }

    public NemesisEvent getEvent() {
        return (!eventQueue.isEmpty()) ? eventQueue.remove() : null;
    }
}
