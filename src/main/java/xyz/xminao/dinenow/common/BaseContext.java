package xyz.xminao.dinenow.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户ID
 * 每一个Http请求服务端分配一个线程
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    // 设置ID
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    // 获取ID
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
