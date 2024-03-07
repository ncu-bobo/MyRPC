package wfb.rpc.core;

import wfb.rpc.common.entity.RpcRequest;
import wfb.rpc.core.serializer.CommonSerializer;

public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
