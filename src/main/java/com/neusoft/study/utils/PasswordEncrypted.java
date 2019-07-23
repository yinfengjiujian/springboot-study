package com.neusoft.study.utils;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * <p>Title: com.neusoft.study.utils</p>
 * <p>Company:东软集团(neusoft)</p>
 * <p>Copyright:Copyright(c)</p>
 * User: Administrator
 * Date: 2019/7/15 0015 22:09
 * Description: 密码处理工具类
 */
public class PasswordEncrypted {


    /**
     * 用来密码加密，并返回数组，第一个参数为加密后的值，第二个为加密盐
     *
     * @param password
     * @return
     */
    public static String[] encryptPassword(String password) {
        String salt = PasswordEncrypted.getSalt();
        String encryPassword = new Md5Hash(password, salt, 3).toString(); //生成的密文

        String[] arrayString = new String[]{encryPassword, salt};
        return arrayString;
    }

    /**
     * 获得本次输入的密码的密文
     *
     * @param password
     * @param salt
     * @return
     */
    public static String getInputPasswordCiph(String password, String salt) {
        if (salt == null) {
            password = "";
        }
        //生成的密文
        return new Md5Hash(password, salt, 3).toString();
    }

    /**
     * 获取随机用户加密需要的盐
     *
     * @return
     */
    public static String getSalt() {
        return new SecureRandomNumberGenerator().nextBytes().toHex();
    }

}
