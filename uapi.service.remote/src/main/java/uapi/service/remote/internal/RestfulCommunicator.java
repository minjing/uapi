package uapi.service.remote.internal;

import okhttp3.*;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.log.ILogger;
import uapi.service.IRegistry;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.ICommunicator;
import uapi.service.remote.ServiceMeta;
import uapi.service.web.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The driver use restful and json format to invoke remote service
 */
@Service(ICommunicator.class)
public class RestfulCommunicator implements ICommunicator {

    public static final String id = "Restful-Json";

    @Inject
    IRegistry _registry;

    @Inject
    ILogger _logger;

    @Inject
    Map<String, IStringResolver> _resolvers = new HashMap<>();

    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    public String getId() {
        return id;
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

        HttpMethod reqMethod = restfuSvcMeta.getMethod();
        List<Object> uriArgs = new ArrayList<>();
        Map<String, Object> paramArgs = new HashMap<>();
        Map<String, Object> headerArgs = new HashMap<>();
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
                if (argMapping.getFrom() == ArgumentFrom.Param) {
                    paramArgs.put(argName, args[i]);
                } else if (argMapping.getFrom() == ArgumentFrom.Header) {
                    headerArgs.put(argName, args[i]);
                }
            } else {
                throw new KernelException("Unsupported argument mapping type {}", argMapping.getClass().getName());
            }
        }

        // Ensure urlArgs must be sequent
        if (CollectionHelper.hasNull(uriArgs)) {
            throw new KernelException("The uri arguments of service {} has empty argument: {}",
                    restfuSvcMeta.getName(), CollectionHelper.asString(uriArgs));
        }

        // Construct request uri from uriArgs
        Request.Builder reqBuild = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(restfuSvcMeta.getUri()).newBuilder();
        for (Object uriArg : uriArgs) {
            urlBuilder.addPathSegment(encodeValue(uriArg));
        }
        // Construct request parameters
        if (headerArgs.size() > 0) {
            Observable.from(headerArgs.entrySet())
                    .subscribe(entry -> reqBuild.addHeader(entry.getKey(), encodeValue(entry.getValue())));
        }
        if (reqMethod == HttpMethod.GET) {
            if (paramArgs.size() > 0) {
                for (Map.Entry<String, Object> entry : paramArgs.entrySet()) {
                    urlBuilder.addQueryParameter(entry.getKey(), encodeValue(entry.getValue()));
                }
            }
            reqBuild.url(urlBuilder.build());
        } else if (reqMethod == HttpMethod.POST) {
            reqBuild.url(urlBuilder.build());
            if (paramArgs.size() > 0) {
                FormBody.Builder formBody = new FormBody.Builder();
                for (Map.Entry<String, Object> entry : paramArgs.entrySet()) {
                    formBody.add(entry.getKey(), encodeValue(entry.getValue()));
                }
                reqBuild.post(formBody.build());
            }
        } else {
            throw new KernelException("Unsupported HTTP method {}", reqMethod);
        }

        Request request = reqBuild.build();
        try {
            Response response = httpClient.newCall(request).execute();
            return decodeResponse(response.body().string());
        } catch (IOException ex) {
            throw new KernelException(ex, "Encounter an error when invoke service {}", restfuSvcMeta.toString());
        }
    }

    private String encodeValue(Object value) {
        // TODO: parse value to string
        return value.toString();
    }

    private Object decodeResponse(String responseText) {
        return null;
    }
}
