package com.interfaces.iat.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/* 使用方式
* PropUtil.modifyProperties(this.getClass().getClassLoader().getResource("application.properties").getPath(), "api.host", http://www.baidu.com);
* */
public class PropUtil {
    /**
     * @description:修改配置文件里面内容（单个属性）
     * @param propPath 文件路径
     * @param name 要修改的属性
     * @param value 修改后的值
     */
    public static void modifyProperties(String propPath, String name, String value) {
        Properties p = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(propPath);
            p.load(fis);
            fis.close();
            p.setProperty(name, value);
            fos = new FileOutputStream(propPath);
            p.store(fos, null);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fis.close();
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
