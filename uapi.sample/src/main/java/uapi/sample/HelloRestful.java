package uapi.sample;

import uapi.service.annotation.Exposure;
import uapi.service.annotation.Service;
import uapi.web.HttpMethod;
import uapi.web.IRestfulService;
import uapi.web.annotation.FromParam;
import uapi.web.annotation.FromUri;
import uapi.web.annotation.Restful;

/**
 * The restful service for hello
 */
@Service(IRestfulService.class)
@Exposure("hello")
public class HelloRestful {

    @Restful(HttpMethod.Get)
    public String sayHello(
            @FromUri(0) String name,
            @FromParam("test") String test
    ) {
        return "Hello " + name + ", " + test;
    }
}
