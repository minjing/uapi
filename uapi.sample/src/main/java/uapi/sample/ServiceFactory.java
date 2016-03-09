package uapi.sample;

import uapi.service.IServiceFactory;
import uapi.service.annotation.Service;

/**
 * Created by xquan on 3/9/2016.
 */
@Service
public class ServiceFactory implements IServiceFactory<String> {

    @Override
    public String createService(Object serveFor) {
        return null;
    }
}
