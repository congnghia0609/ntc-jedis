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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 *
 * @author nghiatc
 * @since Mar 7, 2018
 */
public class JedisShardPoolClient {
    private static final Logger logger = LoggerFactory.getLogger(JedisShardPoolClient.class);
	private static final ConcurrentHashMap<String, JedisShardPoolClient> mapInstance = new ConcurrentHashMap<String, JedisShardPoolClient>(16, 0.9f, 16);
	private static Lock lock = new ReentrantLock();

	private ShardedJedisPool pool;
    List<JedisShardInfo> listShards = new ArrayList<JedisShardInfo>();

    private JedisShardPoolClient(String prefix) {
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setTestOnBorrow(true);
        cfg.setMaxTotal(100); // DEFAULT_MAX_TOTAL = 8 ==> the number instance in pool.
        // "127.0.0.1:1111:;127.0.0.2:2222;127.0.0.3:3333:cccc" | host:port:password
        String shards = NConfig.getConfig().getString(prefix + ".shards", "127.0.0.1:6379");
        System.out.println("shards: " + shards);
        if (shards != null && !shards.isEmpty()) {
            String[] arrShards = shards.split(";");
            System.out.println("arrShards: " + Arrays.asList(arrShards));
            for (String shard : arrShards){
                String[] sh = shard.split(":");
                System.out.println("sh: " + Arrays.asList(sh));
                if (sh.length == 2) {
                    String host = sh[0];
                    int port = Integer.valueOf(sh[1]);
                    JedisShardInfo jsi = new JedisShardInfo(host, port);
                    listShards.add(jsi);
                } else if (sh.length == 3) {
                    String host = sh[0];
                    int port = Integer.valueOf(sh[1]);
                    String password = sh[2];
                    JedisShardInfo jsi = new JedisShardInfo(host, port);
                    jsi.setPassword(password);
                    listShards.add(jsi);
                }
            }
        }
        pool = new ShardedJedisPool(cfg, listShards);
    }

	public static JedisShardPoolClient getInstance(String prefix) {
		JedisShardPoolClient instance = mapInstance.get(prefix);
		if(instance == null) {
			lock.lock();
			try {
				instance = mapInstance.get(prefix);
				if(instance == null) {
					instance = new JedisShardPoolClient(prefix);
					mapInstance.put(prefix, instance);
				}
			} finally {
				lock.unlock();
			}
		}
		return instance;
	}
    
    public ShardedJedis borrowJedis(){
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
        } catch (Exception e) {
            logger.error("borrowJedis: ", e);
        }
        return jedis;
    }
    
    public static void returnJedis(ShardedJedis jedis){
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error("returnJedis: ", e);
        }
    }
    
}
