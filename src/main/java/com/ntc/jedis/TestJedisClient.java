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

import java.util.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 *
 * @author nghiatc
 * @since Mar 7, 2018
 * 
 * https://github.com/xetorthio/jedis
 */
public class TestJedisClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // 1. test1
            test1();
            
            // 2. testTransaction
//            testTransaction(false);
            
            // 3. testPipeline
//            testPipeline(false);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void test1(){
        // Twemproxy
        Jedis jedis = JedisPoolClient.getInstance("test").borrowJedis();
        try {
            jedis.set("foo", "bar");
            String foobar = jedis.get("foo");
            System.out.println("foobar: " + foobar);
            jedis.zadd("sose", 0, "car");
            jedis.zadd("sose", 0, "bike");
            Set<String> sose = jedis.zrange("sose", 0, -1);
            System.out.println("sose: " + sose);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolClient.returnJedis(jedis);
        }
    }

    /**
     * Dung Transaction khi can bao ve 1 so hanh dong lien tuc voi nhau.
     * @param proxy use proxy Twemproxy
     */
    public static void testTransaction(boolean proxy){
        Jedis jedis = null;
        try {
            if (proxy) {
                // Twemproxy no support Transactions
                jedis = JedisPoolClient.getInstance("test").borrowJedis();
            } else {
                // No Twemproxy proxy, use directly Redis Single node --> support transaction
                // jedis = new Jedis("127.0.0.1", 6380);
                jedis = JedisPoolClient.getInstance("transaction").borrowJedis();
            }
            Transaction t = jedis.multi();
            t.set("fool", "bar"); 
            Response<String> result1 = t.get("fool");

            t.zadd("foo", 1, "barowitch");
            t.zadd("foo", 0, "barinsky");
            t.zadd("foo", 0, "barikoviev");
            Response<Set<String>> sose = t.zrange("foo", 0, -1);   // get the entire sortedset
            t.exec();                                              // dont forget it

            String foolbar = result1.get();                       // use Response.get() to retrieve things from a Response
            System.out.println("foolbar: " + foolbar);
            int soseSize = sose.get().size();                      // on sose.get() you can directly call Set methods!
            System.out.println("soseSize: " + soseSize);
            System.out.println("sose: " + sose.get().toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolClient.returnJedis(jedis);
        }
    }
    
    /**
     * Dung Pipeline khi can lam 1 so luong lon cac hoat dong xuong Redis.
     * @param proxy use proxy Twemproxy
     */
    public static void testPipeline(boolean proxy){
        Jedis jedis = null;
        try {
            if (proxy) {
                // Twemproxy no support Pipeline
                jedis = JedisPoolClient.getInstance("test").borrowJedis();
            } else {
                // No Twemproxy proxy, use directly Redis Single node --> support Pipeline
                // jedis = new Jedis("127.0.0.1", 6380);
                jedis = JedisPoolClient.getInstance("pipeline").borrowJedis();
            }
            Pipeline p = jedis.pipelined();
            p.set("fool", "bar"); 
            p.zadd("pfoo", 1, "barowitch");  p.zadd("pfoo", 0, "barinsky"); p.zadd("pfoo", 0, "barikoviev");
            Response<String> pipeString = p.get("fool");
            Response<Set<String>> sose = p.zrange("pfoo", 0, -1);
            p.sync();

            System.out.println("pipeString: " + pipeString.get());
            int soseSize = sose.get().size();
            System.out.println("soseSize: " + soseSize);
            Set<String> setBack = sose.get();
            System.out.println("setBack: " + setBack);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolClient.returnJedis(jedis);
        }
    }
}
