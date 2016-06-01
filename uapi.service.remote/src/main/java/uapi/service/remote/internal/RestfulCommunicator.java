/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal;

import okhttp3.*;
import rx.Observable;
import uapi.KernelException;
import uapi.helper.ArgumentChecker;
import uapi.helper.CollectionHelper;
import uapi.helper.Triple;
import uapi.log.ILogger;
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

    public static final String id = "Restful";

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
        RestfulServiceMeta restfulSvcMeta = (RestfulServiceMeta) serviceMeta;
        List<ArgumentMapping> argMappings = restfulSvcMeta.getArgumentMappings();
        if (argMappings.size() != args.length) {
            throw new KernelException("Found unmatched service {} argument size {}, expect {}",
                    serviceMeta.getName(), argMappings.size(), args.length);
        }

        String format = restfulSvcMeta.getFormat();
        HttpMethod reqMethod = restfulSvcMeta.getMethod();
        List<Triple<ArgumentMapping, String, Object>> uriArgs = new ArrayList<>();
        Map<String, Triple<ArgumentMapping, String, Object>> paramArgs = new HashMap<>();
        Map<String, Triple<ArgumentMapping, String, Object>> headerArgs = new HashMap<>();
        for (int i = 0; i < argMappings.size(); i++) {
            ArgumentMapping argMapping = argMappings.get(i);
            if (argMapping instanceof IndexedArgumentMapping) {
                int index = ((IndexedArgumentMapping) argMapping).getIndex();
                if (index >= args.length) {
                    throw new KernelException("The service {} argument {}'s index {} is out of real arguments index {}",
                            restfulSvcMeta.getName(), i, index, args.length - 1);
                }
                uriArgs.set(index, new Triple<>(argMapping, format, args[i]));
            } else if (argMapping instanceof NamedArgumentMapping) {
                String argName = ((NamedArgumentMapping) argMapping).getName();
                if (argMapping.getFrom() == ArgumentFrom.Param) {
                    paramArgs.put(argName, new Triple<>(argMapping, format, args[i]));
                } else if (argMapping.getFrom() == ArgumentFrom.Header) {
                    headerArgs.put(argName, new Triple<>(argMapping, format, args[i]));
                }
            } else {
                throw new KernelException("Unsupported argument mapping type {}", argMapping.getClass().getName());
            }
        }

        // Ensure urlArgs must be sequent
        if (CollectionHelper.hasNull(uriArgs)) {
            throw new KernelException("The uri arguments of service {} has empty argument: {}",
                    restfulSvcMeta.getName(), CollectionHelper.asString(uriArgs));
        }

        // Construct request uri from uriArgs
        Request.Builder reqBuild = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(restfulSvcMeta.getUri()).newBuilder();
        Observable.from(uriArgs).subscribe(uriArg -> urlBuilder.addPathSegment(encodeValue(uriArg)));

        // Construct request parameters
        if (headerArgs.size() > 0) {
            Observable.from(headerArgs.entrySet())
                    .subscribe(entry -> reqBuild.addHeader(
                            entry.getKey(), encodeValue(entry.getValue())));
        }
        if (reqMethod == HttpMethod.GET) {
            if (paramArgs.size() > 0) {
                Observable.from(paramArgs.entrySet())
                        .subscribe(entry -> urlBuilder.addQueryParameter(
                                entry.getKey(), encodeValue(entry.getValue())));
            }
            reqBuild.url(urlBuilder.build());
        } else if (reqMethod == HttpMethod.POST) {
            reqBuild.url(urlBuilder.build());
            if (paramArgs.size() > 0) {
                FormBody.Builder formBody = new FormBody.Builder();
                Observable.from(paramArgs.entrySet())
                        .subscribe(entry -> formBody.add(
                                entry.getKey(), encodeValue(entry.getValue())));
                reqBuild.post(formBody.build());
            }
        } else {
            throw new KernelException("Unsupported HTTP method {}", reqMethod);
        }

        Request request = reqBuild.build();
        try {
            Response response = httpClient.newCall(request).execute();
            return decodeResponse(new Triple<>(
                    restfulSvcMeta.getReturnTypeName(), restfulSvcMeta.getFormat(), response.body().string()));
        } catch (IOException ex) {
            throw new KernelException(ex, "Encounter an error when invoke service {}", restfulSvcMeta.toString());
        }
    }

    /**
     * Encode argument value to a formatted string
     *
     * @param   valueInfo
     *          The value is a Triple instance which is composed with argument mapping information,
     *          format name and argument value
     * @return  Formatted string
     */
    private String encodeValue(Triple<ArgumentMapping, String, Object> valueInfo) {
        String typeName = valueInfo.getLeftValue().getType();
        IStringResolver resolver = this._resolvers.get(typeName);
        if (resolver == null) {
            throw new KernelException("No resolver was mapped to name {}", typeName);
        }
        return resolver.encode(valueInfo.getRightValue(), valueInfo.getCenterValue());
    }

    /**
     * Decode response text to specific object
     *
     * @param   responseInfo
     *          The responseIfo is a Tuple instance which is composed with return type name,
     *          format name and response text
     * @return  The decoded object
     */
    private Object decodeResponse(Triple<String, String, String> responseInfo) {
        ArgumentChecker.required(responseInfo, "responseIfo");
        String rtnTypeName = responseInfo.getLeftValue();
        String format = responseInfo.getCenterValue();
        String respText = responseInfo.getRightValue();
        IStringResolver resolver = this._resolvers.get(rtnTypeName);
        if (resolver ==  null) {
            throw new KernelException("No resolver was mapped to name {}", rtnTypeName);
        }
        return resolver.decode(respText, format);
    }
}
