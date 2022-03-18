package com.interfaces.iat.util;

import java.util.Random;


public class RandomUtil {
    /**
     * 随机生成一个手机号
     * @return
     */
    public static String randomPhone(){
        Random random = new Random();
        String phonePrefix="138";
        for(int i=0 ;i<8 ; i++){
            int num = random.nextInt(9);
            phonePrefix = phonePrefix+num;
        }
        return phonePrefix;
    }

    /**
     * 随机生成用户名
     * @return
     */
    public static String randomUsername(){
        return randomString(10);
    }

    /**
     * 根据长度随机生成字符串
     * @param length
     * @return
     */
    public static String randomString(int length){
        String result = "";
        Random random = new Random();
        for(int i = 0; i < length; i++) {
            //输出是大写字母还是小写字母
            int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
            result += (char)(random.nextInt(26) + temp);
        }
        return result;
    }
}
