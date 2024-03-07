package wfb.test.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wfb.rpc.api.HelloObject;
import wfb.rpc.api.HelloService;
import wfb.rpc.core.annotation.Service;

@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}", object.getMessage());
        Integer res = object.getA()+object.getB();
        logger.info("处理完毕，已返回请求计算结果：" + res);
        logger.info("等待新的连接中....");
        return object.getId() + ", 你好, 这是服务端计算的结果，res= " + res;
    }

}
