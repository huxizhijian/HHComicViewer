package org.huxizhijian.sdk.network.utils;

/**
 * Created by huxizhijian on 2016/11/16.
 */

public class Utils {
    public static boolean isExist(String className, ClassLoader loader) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
