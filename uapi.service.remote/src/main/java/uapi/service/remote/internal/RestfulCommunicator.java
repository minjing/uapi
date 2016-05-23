package uapi.service.remote.internal;

import com.sun.org.apache.xpath.internal.Arg;
import javafx.beans.NamedArg;
import okhttp3.*;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.Pair;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.ServiceMeta;
import uapi.service.web.ArgumentMapping;
import uapi.service.web.HttpMethod;
import uapi.service.web.IndexedArgumentMapping;
import uapi.service.web.NamedArgumentMapping;

import java.util.ArrayList;
import java.util.HashMap;
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
//@Service(ICommunicator.class)
public class RestfulCommunicator implements ICommunicator {

    public static final String id = "Restful-Json";

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
    public Object request(ServiceMeta serviceMeta, Object... args) {
        ArgumentChecker.required(serviceMeta, "serviceMeta");
        ArgumentChecker.required(args, "args");

        if (! (serviceMeta instanceof RestfulServiceMeta)) {
            throw new KernelException("The {} can't handle : {}", this.getClass().getName(), serviceMeta.getClass().getName());
        }
        RestfulServiceMeta restfuSvcMeta = (RestfulServiceMeta) serviceMeta;
        List<ArgumentMapping> argMappings = restfuSvcMeta.getArgumentMappings();
        if (argMappings.size() != args.length) {
            throw new KernelException("Found unmatched service {} argument size {}, expect {}",
                    serviceMeta.getName(), argMappings.size(), args.length);
        }

        String baseUri = restfuSvcMeta.getUri();
        HttpMethod reqMethod = restfuSvcMeta.getMethod();
        List<Object> uriArgs = new ArrayList<>();
        Map<String, Object> paramArgs = new HashMap<>();
        for (int i = 0; i < argMappings.size(); i++) {
            ArgumentMapping argMapping = argMappings.get(i);
            if (argMapping instanceof IndexedArgumentMapping) {
                int index = ((IndexedArgumentMapping) argMapping).getIndex();
                if (index >= args.length) {
                    throw new KernelException("The service {} argument {}'s index {} is out of real arguments index {}",
                            restfuSvcMeta.getName(), i, index, args.length - 1);
                }
                uriArgs.set(index, args[i]);
            } else if (argMapping instanceof NamedArgumentMapping) {
                String argName = ((NamedArgumentMapping) argMapping).getName();
                paramArgs.put(argName, args[i]);
            } else {
                throw new KernelException("Unsupported argument mapping type {}", argMapping.getClass().getName());
            }
        }

        Request request = null;
        if (reqMethod == HttpMethod.GET) {
            Request.Builder reqBuild = new Request.Builder();
        } else if (reqMethod == HttpMethod.POST) {

        } else {
            throw new KernelException("Unsupported HTTP method {}", reqMethod);
        }

        return null;
    }
}
