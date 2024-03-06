package wfb.rpc.core;

import wfb.rpc.common.entity.RpcRequest;

public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

}
