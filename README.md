# poison-rpc
### easy-rpc 1.0.0-beta.1
### 功能介绍：
此框架为一个简单的rpc框架，快速，稳定，简洁，使用简单。支持服务注册发现，调用负载均衡，可以理解为dubbo的简单版。内部依赖注册中心zookeeper，序列化Protostuff框架，服务端和客户端通过netty高性能nio处理异步调用返回结果
客户端采用动态代理发送请求
