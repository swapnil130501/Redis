package com.pm.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class Store {
    private static final Logger logger = LoggerFactory.getLogger(Store.class);

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

    public int setExpTime(String key, long ttlSeconds) {
        Entry entry = map.get(key);
        if(entry == null || entry.isExpired()) {
            return 0;
        }

        entry.expiresAt = System.currentTimeMillis() + ttlSeconds * 1000;
        return 1;
    }

    public void deleteExpiredKeys() {
        int sampled = 0;
        int deleted = 0;

        List<String> keyList = new ArrayList<>(map.keySet());
        Collections.shuffle(keyList);

        for(String key : keyList) {
            if(sampled >= 20) {
                break;
            }

            Entry entry = map.get(key);
            if(entry == null) {
                continue;
            }

            sampled++;

            if(entry.isExpired()) {
                map.remove(key);
                deleted++;
            }
        }

        double expiredRatio = sampled == 0 ? 0 : (double) deleted / sampled;

        logger.info("[CRON] Sampled={} Deleted={} Ratio={}",
                sampled, deleted, String.format("%.2f", expiredRatio));

        if(expiredRatio > 0.25) {
            logger.info("[CRON] Expired ratio > 25%, running again...");
            deleteExpiredKeys();
        }
    }
}