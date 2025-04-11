package com.bkxt.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author 35238
 * @date 2023/7/31 0031 14:25
 */
@Component
//项目启动时，该类负责预处理一些代码。CommandLineRunner是spring提供的接口
public class myCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("程序初始化");
    }
}