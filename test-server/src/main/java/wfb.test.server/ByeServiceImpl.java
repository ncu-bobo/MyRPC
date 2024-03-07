package wfb.test.server;

import wfb.rpc.core.annotation.Service;
import wfb.rpc.api.ByeService;

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
