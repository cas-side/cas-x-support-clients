package com.github.casside.cas.support.qywx;

/**
 * tnd
 *
 * 企业微信非标准oauth协议，code只能单独维护一下了
 *
 * 注意：code是一次性的
 */
public class QyWxCodeOnceHolder {

    private static ThreadLocal<String> codes = new ThreadLocal<>();

    public static void set(String code) {
        codes.set(code);
    }

    /**
     * return and remove
     *
     * @return code
     */
    public static String release() {
        return codes.get();
    }

}
