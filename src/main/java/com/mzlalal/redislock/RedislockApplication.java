package com.mzlalal.redislock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @description:  入口
 * @author:       Mzlalal
 * @date:         2020/1/7 17:00
 * @version:      1.0
 */
@SpringBootApplication
public class RedislockApplication {

    static Integer num = 3;

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(RedislockApplication.class, args);

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                // 获取模板服务
                RedissonClient redissonClient = context.getBean(RedissonClient.class);

                // 获取对象锁
                RLock rLock = redissonClient.getLock("incrementLock");

                // 是否获取到锁
                boolean locked = false;
                try {
                    // 在此阻塞
                    locked = rLock.tryLock(10L, 10L, TimeUnit.SECONDS);

                    // 如果没获取到所则不执行
                    if (locked) {
                        if (num > 0) {
                            num--;
                            System.out.println(Thread.currentThread().getName()+"获取到锁incrementLock并且减去1,当前i的值为:"+num);
                        } else {
                            System.out.println(Thread.currentThread().getName()+"获取到锁incrementLock,当前i已经不大于0,当前i的值为:"+num);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    rLock.unlock();
                }
            }).start();
        }

//        for (int i = 0; i < 6; i++) {
//            new Thread(() -> {
//                synchronized (num) {
//                if (num > 0) {
//                    num--;
//                    System.out.println(Thread.currentThread().getName()+"获取到锁incrementLock并且减去1,当前i的值为:"+num);
//                } else {
//                    System.out.println(Thread.currentThread().getName()+"获取到锁incrementLock,当前i已经不大于0,当前i的值为:"+num);
//                }
//            }
//            }).start();
//        }
    }

}
