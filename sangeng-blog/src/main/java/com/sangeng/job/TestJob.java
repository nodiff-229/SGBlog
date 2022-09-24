package com.sangeng.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class TestJob {


    @Scheduled(cron = "0/5 * * * * ?")
    public void testJos() {
        //要执行的代码
        System.out.println("111");

    }
}
