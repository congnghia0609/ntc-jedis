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

import java.util.Arrays;

/**
 *
 * @author nghiatc
 * @since Mar 7, 2018
 */
public class TestJedisShard {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String s = "127.0.0.1:1111:;127.0.0.2:2222;127.0.0.3:3333:cccc";
        String[] arr = s.split(";");
        System.out.println("arr: " + Arrays.asList(arr));
        for (String hp : arr){
            String[] arrt = hp.split(":");
            System.out.println("arrt: " + Arrays.asList(arrt));
        }
    }

}
