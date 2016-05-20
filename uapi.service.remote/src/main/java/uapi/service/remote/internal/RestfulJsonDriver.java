package uapi.service.remote.internal;

import okhttp3.*;
import okio.BufferedSink;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicationDriver;
import uapi.service.web.HttpMethod;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The driver use restful and json format to invoke remote service
 *
 * JSON format:
 * {
 *     type: [query | request],
 *
 * }
 */
//@Service(ICommunicationDriver.class)
public class RestfulJsonDriver implements ICommunicationDriver {

    private static final String id = "Restful-Json";

    private static final String CFG_URI     = "uri";
    private static final String CFG_METHOD  = "method";

//    @Config(path=IRemoteServiceConfigurableKey.DRV_RESTFUL_HOST)
//    String _host;
//
//    @Config(path=IRemoteServiceConfigurableKey.DRV_RESTFUL_PORT)
//    int _port;
//
//    @Config(path=IRemoteServiceConfigurableKey.DRV_RESTFUL_URI_PREFIX, optional=true)
//    String _uriPrefix = Constant.DEF_RESTFUL_URI_PREFIX;
//
//    @Config(path=IRemoteServiceConfigurableKey.DRV_RESTFUL_CALL_TYPE, optional=true)
//    String _callType;
//
//    @Inject
//    IRegistry _registry;

    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    public String getId() {
        return id;
    }

//    @Override
    public Object request(Map<String, Object> config, Object... params) {
        ArgumentChecker.required(config, "config");

        String uri = (String) config.get(CFG_URI);
        HttpMethod method = (HttpMethod) config.get(CFG_METHOD);
        ArgumentChecker.required(uri, "uri");
        ArgumentChecker.required(method, "method");

        Request request = null;
        if (method == HttpMethod.GET) {
            request = new Request.Builder().url(uri).get().build();
        } else {
            throw new KernelException("Unsupported http method {}", method);
        }

//        Response response = this.httpClient.newCall(request).execute();
//        response.body().
        return null;
    }

    @Override
    public Object request(List<Pair> params, Object... args) {
        return null;
    }
}
