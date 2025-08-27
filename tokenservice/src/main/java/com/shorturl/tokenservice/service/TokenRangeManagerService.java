package com.shorturl.tokenservice.service;

import com.shorturl.tokenservice.dto.RangeResponse;
import jakarta.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenRangeManagerService {
    private static final Logger log = LoggerFactory.getLogger(TokenRangeManagerService.class);
    private final CuratorFramework client;
    private final InterProcessMutex lock;
    private static final String lockPath = "/token-service/lock";
    private static final String rangePath = "/token-service/range";

    @Autowired
    public TokenRangeManagerService(CuratorFramework client) {
        this.client = client;
        this.lock = new InterProcessMutex(client, lockPath);
    }

    @PostConstruct
    public void handleRangePath() throws Exception {
        if (client.checkExists().forPath(rangePath) == null) {
            log.info("No such path exists. Setting rangePath in zookeeper to 100000");
            client.create().creatingParentsIfNeeded().forPath(rangePath, "100000".getBytes());
        }
    }

    public RangeResponse fetchNewRangeFromTRMS(int batchSize) throws Exception {
        boolean acquired = false;
        try {
            acquired = lock.acquire(5, TimeUnit.SECONDS);

            if (!acquired) throw new RuntimeException("Could not acquire lock");

            log.info("Lock Acquired");
            byte[] data = client.getData().forPath(rangePath);

            long currentMax = Long.parseLong(new String(data));
            log.info("Data acquired currentMax : {}", currentMax);
            long start = currentMax + 1;
            long end = currentMax + batchSize;

            client.setData().forPath(rangePath, String.valueOf(end).getBytes());

            return new RangeResponse(start, end);
        } finally {
            if (acquired) {
                lock.release();
                log.info("Lock Released");
            }
        }
    }
}
