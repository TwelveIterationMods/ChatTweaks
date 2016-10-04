package net.blay09.mods.bmc.chat.emotes;

import com.google.common.collect.Lists;

import java.util.LinkedList;

public class AsyncEmoteLoader implements Runnable {

    private static final AsyncEmoteLoader instance = new AsyncEmoteLoader();

    public static AsyncEmoteLoader getInstance() {
        return instance;
    }

    private final LinkedList<IEmote> loadQueue = Lists.newLinkedList();
    private boolean running;

    public AsyncEmoteLoader() {
        running = true;
        Thread thread = new Thread(this, "EmoteLoader");
        thread.start();
    }

    public void loadAsync(IEmote emote) {
        synchronized(loadQueue) {
            loadQueue.push(emote);
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                synchronized (loadQueue) {
                    int i = 0;
                    while (!loadQueue.isEmpty()) {
                        i++;
                        if(i > 5) {
                            break;
                        }
                        IEmote emote = loadQueue.pop();

                        emote.getLoader().loadEmoteImage(emote);
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

}
