package com.huang.gai.rpc.poisondemoconsumer;

import com.huang.gai.inf.PoisonRpcTestInterface;
import com.huang.gai.rpc.core.RpcProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PoisonDemoConsumerApplication {

    @Autowired
    private RpcProxy rpcProxy;

    public static void main(String[] args) {
        SpringApplication.run(PoisonDemoConsumerApplication.class, args);
    }

    @PostConstruct
    public void test() {
        PoisonRpcTestInterface o = rpcProxy.create(PoisonRpcTestInterface.class);
        String s = o.saySomeThing("info ssss");
        System.out.println(s);
    }
}
