package com.shorturl.tokenservice.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {
    @Value("${zookeeper.connection.url}")
    public String zookeeperConnectionString;

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zookeeperConnectionString,
                new ExponentialBackoffRetry(1000, 3)
        );
        client.start();
        return client;
    }
}
