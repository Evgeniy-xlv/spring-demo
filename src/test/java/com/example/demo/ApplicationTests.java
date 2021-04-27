package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ApplicationTests {

    @Value("${com.example.demo.request.rate.limit}")
    private int requestRate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void contextLoads() {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);
        AtomicInteger fails = new AtomicInteger();
        for (int i = 0; i < requestRate; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    System.out.println(Thread.currentThread() + " " + finalI);
                    mockMvc.perform(MockMvcRequestBuilders.get("/example"))
                            .andExpect(MockMvcResultMatchers.status().isOk());
                    mockMvc.perform(MockMvcRequestBuilders.get("/example")
                                .header("X-Forwarded-For", "127.0.0." + (finalI + 2)))
                            .andExpect(MockMvcResultMatchers.status().isOk());
                } catch (Exception e) {
                    fails.getAndIncrement();
                    e.printStackTrace();
                    executorService.shutdownNow();
                }
            });
        }
        executorService.submit(() -> {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/example")
                            .header("X-Forwarded-For", "127.0.0.2"))
                        .andExpect(MockMvcResultMatchers.status().isOk());
                mockMvc.perform(MockMvcRequestBuilders.get("/example"))
                        .andExpect(MockMvcResultMatchers.status().isBadGateway());
            } catch (Exception e) {
                fails.getAndIncrement();
                e.printStackTrace();
                executorService.shutdownNow();
            }
        });
        long timeout = System.currentTimeMillis() + 10000L;
        while (executorService.getCompletedTaskCount() < requestRate + 1) {
            if (timeout - System.currentTimeMillis() <= 0) {
                Assertions.fail("timeout");
                return;
            }
            if (fails.get() > 0) {
                executorService.shutdownNow();
                Assertions.fail("uncompleted task");
                return;
            }
        }
    }
}
