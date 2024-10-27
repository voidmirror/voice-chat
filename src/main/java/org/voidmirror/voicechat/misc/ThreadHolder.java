package org.voidmirror.voicechat.misc;

import java.util.HashMap;

public class ThreadHolder {

    private ThreadHolder() {}

    private static ThreadHolder self;

    public static ThreadHolder getInstance() {
        if (self == null) {
            self = new ThreadHolder();
        }
        return self;
    }

    private HashMap<String, Thread> threadHashMap = new HashMap<>();

    public void addThread(Thread thread, String name) {
        threadHashMap.put(name, thread);
    }

    public Thread getThread(String name) {
        return threadHashMap.get(name);
    }

}
