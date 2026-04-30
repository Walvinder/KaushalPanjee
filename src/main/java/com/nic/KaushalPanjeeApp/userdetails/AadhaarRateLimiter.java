package com.nic.KaushalPanjeeApp.userdetails;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AadhaarRateLimiter {

    private static class RequestInfo {
        List<Long> timestamps = new ArrayList<>();
        long blockedTill = 0;
    }

    private static final Map<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

    public static synchronized boolean isBlocked(String aadhaar) {
        long currentTime = System.currentTimeMillis();

        RequestInfo info = requestMap.getOrDefault(aadhaar, new RequestInfo());
        if (info.blockedTill > currentTime) {
            return true;
        }
        info.timestamps.removeIf(t -> currentTime - t > 60000);
        info.timestamps.add(currentTime);
        if (info.timestamps.size() >= 2) {
            info.blockedTill = currentTime + 10000; 
            info.timestamps.clear();
        }

        requestMap.put(aadhaar, info);

        return false;
    }
}