package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.GoogleAuthenticatorGenerator
 * @Date: 2023年12月06日 16:50
 * @Description:
 */
public class GoogleAuthenticator {

    public static void main(String[] args) {
        // 这里的 "secretKey" 应该是与用户账户关联的秘钥。
        // 在一个真实的应用中，这个秘钥应该是在用户启用两步验证时生成的，
        // 并且要安全地存储在服务器上。
        String secretKey = "4S6MKCZ6NG53AZHS";
        // 获取 TOTP
        generateTotp(secretKey);
    }

    /**
     * 生成Google Authenticator的TOTP密码
     * @param secretKey 用户账户关联的秘钥
     * @return TOTP密码
     */
    public static String generateTotp(String secretKey) {
        // 创建一个新的 Google Authenticator 实例。
        com.warrenstrange.googleauth.GoogleAuthenticator googleAuthenticator = new com.warrenstrange.googleauth.GoogleAuthenticator();
        int totp = googleAuthenticator.getTotpPassword(secretKey);
        // 将 TOTP 转换为6位数字符串，包括前导零
        String totpCode = String.format("%06d", totp);
        // 输出 TOTP
        return totpCode;
    }
}