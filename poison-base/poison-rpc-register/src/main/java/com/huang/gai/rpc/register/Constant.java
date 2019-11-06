package com.huang.gai.rpc.register;

/**
 * @author HuangGai
 * @date 2019/11/5
 */
public class Constant {
    private Constant() {
    }

    /**
     * zk超时时间
     */
    public static final int ZK_SESSION_TIMEOUT = 50000;

    /**
     * 注册节点
     */
    public static final String ZK_REGISTRY_PATH = "/register";
    /**
     * 节点
     */
    public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
