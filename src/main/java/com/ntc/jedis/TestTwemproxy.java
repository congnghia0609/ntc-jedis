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

/**
 *
 * @author nghiatc
 * @since Mar 6, 2018
 */
public class TestTwemproxy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 22122);
        
//        String value = jedis.get("order:6");
//        System.out.println("value: " + value);
//        List<String> mvalue = jedis.mget("userkey", "somekey", "anotherkey", "order:1", "order:2", "order:3", "order:4", "order:5", "order:6", "userkey1", "somekey1", "anotherkey1", "z1");
//        System.out.println("mvalue: " + mvalue);
        
        String foobar = jedis.get("foo");
        System.out.println("foobar: " + foobar);
        Set<String> sose = jedis.zrange("sose", 0, -1);
        System.out.println("sose: " + sose);

        System.out.println("done...");
    }

}
