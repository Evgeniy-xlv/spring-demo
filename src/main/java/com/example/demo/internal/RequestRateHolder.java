package com.example.demo.internal;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * It holds all request times by its ip addresses and provides an opportunity to count requests by
 * ip address and period in millis.
 * */
@Component
public class RequestRateHolder {

    /**
     * It stores a request ip address as key and a list of request times as value.
     * All time is stored here in millis.
     * */
    private final Map<String, LinkedList<Long>> requestTimesMap = new HashMap<>();

    /**
     * It calculates and returns the count of requests by specified address as input.
     * @param requestAddress ip address of request
     * @param rate request rate. it is necessary to optimize the algorithm and avoid OutOfMemory problems
     *             when one of the ip addresses will send a lot of requests within a short period of time
     * @param period request history period in millis
     * */
    public synchronized int countRequests(String requestAddress, int rate, long period) {
        int count = 0;
        long earliestTimeMillis = 0;
        LinkedList<Long> times = requestTimesMap.computeIfAbsent(requestAddress, k -> new LinkedList<>());
        Iterator<Long> iterator = times.iterator();
        while (iterator.hasNext()) {
            Long requestTimeMillis = iterator.next();
            if (System.currentTimeMillis() - requestTimeMillis < period) {
                if (count == 0)
                    earliestTimeMillis = requestTimeMillis;
                count++;
            } else
                iterator.remove();
        }
        if (count >= rate)
            if (System.currentTimeMillis() - 1000 < earliestTimeMillis)
                if (count > 1)
                    times.removeLast();
        times.add(System.currentTimeMillis());
        return count;
    }
}
