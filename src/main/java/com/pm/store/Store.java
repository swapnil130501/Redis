package com.pm.store;

import java.util.concurrent.ConcurrentHashMap;

public class Store {

    private static class Entry {
        String value;
        long expiresAt;

        Entry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            if(expiresAt == -1) return false;
            return System.currentTimeMillis() > expiresAt;
        }
    }

    private final ConcurrentHashMap<String, Entry> map = new ConcurrentHashMap<>();

    public void set(String key, String value, long exDurationMs) {
        long expiresAt = exDurationMs == -1 ? -1 : System.currentTimeMillis() + exDurationMs;
        map.put(key, new Entry(value, expiresAt));
    }

    public String get(String key) {
        Entry entry = map.get(key);
        if (entry == null) return null;
        if (entry.isExpired()) {
            map.remove(key);
            return null;
        }

        return entry.value;
    }

    public int delete(String... keys) {
        int cnt = 0;
        for(String key : keys) {
            Entry entry = map.get(key);

            if(entry != null && !entry.isExpired()) {
                map.remove(key);
                cnt++;
            }
        }

        return cnt;
    }

    public boolean exists(String key) {
        return get(key) != null;
    }

    public long ttl(String key) {
        Entry entry = map.get(key);

        if(entry == null) {
            return -2;
        }

        if(entry.expiresAt == -1) {
            return -1;
        }

        if(entry.isExpired()) {
            map.remove(key);
            return -2;
        }

        return (entry.expiresAt - System.currentTimeMillis()) / 1000;
    }
}