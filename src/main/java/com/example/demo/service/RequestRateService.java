package com.example.demo.service;

import com.example.demo.internal.RequestRateHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Service provides an opportunity to find out if the limit of requests by its ip address has been exceeded
 * */
@Service
@RequiredArgsConstructor
public class RequestRateService {

    @Value("${com.example.demo.request.rate.limit}")
    private int requestRateLimit;

    @Value("${com.example.demo.request.rate.limit.period.minutes}")
    private int periodInMinutes;

    private final RequestRateHolder requestRateHolder;

    @PostConstruct
    public void init() {
        periodInMinutes *= 60000;
    }

    /**
     * @return true if the request hasn't exceeded the limit of requests; otherwise false
     * */
    public boolean isRequestExceededLimit(HttpServletRequest httpServletRequest) {
        String requestAddress = getAddress(httpServletRequest);
        int count = requestRateHolder.countRequests(requestAddress, requestRateLimit, periodInMinutes);
        return count >= requestRateLimit;
    }

    /**
     * @return request's ip address
     * */
    private String getAddress(HttpServletRequest httpServletRequest) {
        String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null)
            return httpServletRequest.getRemoteAddr();
        return xfHeader.split(",")[0];
    }
}
