/*
 * Copyright 2018 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.jedis;

import com.ntc.configer.NConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author nghiatc
 * @since Mar 7, 2018
 */
public class JedisClient {
    private final Logger logger = LoggerFactory.getLogger(JedisClient.class);
	private static final ConcurrentHashMap<String, JedisClient> mapInstance = new ConcurrentHashMap<String, JedisClient>(16, 0.9f, 16);
	private static Lock lock = new ReentrantLock();

	private JedisPool pool;

    private JedisClient(String prefix) {
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setTestOnBorrow(true);
        cfg.setMaxTotal(100);
        String host = NConfig.getConfig().getString(prefix + ".host", "127.0.0.1");
        int port = NConfig.getConfig().getInt(prefix + ".port", 6379);
        System.out.println("host: " + host);
        System.out.println("port: " + port);
        pool = new JedisPool(cfg, host, port);
    }

	public static JedisClient getInstance(String prefix) {
		JedisClient instance = mapInstance.get(prefix);
		if(instance == null) {
			lock.lock();
			try {
				instance = mapInstance.get(prefix);
				if(instance == null) {
					instance = new JedisClient(prefix);
					mapInstance.put(prefix, instance);
				}
			} finally {
				lock.unlock();
			}
		}
		return instance;
	}
    
    public Jedis borrowJedis(){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
        } catch (Exception e) {
            logger.error("borrowJedis: ", e);
        }
        return jedis;
    }
    
    public void returnJedis(Jedis jedis){
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error("returnJedis: ", e);
        }
    }
    
}
