package com.huang.gai.rpc.producer.test;

import com.huang.gai.core.PoisonRpcService;
import com.huang.gai.inf.PoisonRpcTestInterface;

/**
 * @author HuangGai
 * @date 2019/11/5
 */
@PoisonRpcService(PoisonRpcTestInterface.class)
public class TestService implements PoisonRpcTestInterface {
    @Override
    public String saySomeThing() {
        return "hello  niubi";
    }

    @Override
    public String saySomeThing(String info) {
        return info + "hello niubi";
    }
}
