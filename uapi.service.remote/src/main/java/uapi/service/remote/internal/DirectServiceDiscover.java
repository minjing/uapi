/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service.remote.internal;

import com.google.common.base.Strings;
import uapi.KernelException;
import uapi.Type;
import uapi.config.annotation.Config;
import uapi.helper.ArgumentChecker;
import uapi.rx.Looper;
import uapi.service.*;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.remote.*;
import uapi.web.JsonStringCodec;
import uapi.web.http.HttpMethod;
import uapi.web.restful.ArgumentFrom;
import uapi.web.restful.IndexedArgumentMapping;
import uapi.web.restful.NamedArgumentMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Discover service from remote directly
 */
@Service(IServiceDiscover.class)
public class DirectServiceDiscover implements IServiceDiscover {

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_COMM)
    String _communicatorName;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_HOST)
    String _host;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_PORT)
    int _port;

    @Config(path=IRemoteServiceConfigurableKey.DISCOVER_URI_PREFIX)
    String _uriPrefix;

    @Inject
    IRegistry _registry;

    @Inject
    Map<String, ICommunicator> _communicators = new HashMap<>();

    @Inject
    MessageLoader _msgLoader;

    @Override
    public ServiceInterfaceMeta discover(ServiceInterfaceMeta serviceInterfaceMeta) {
        ArgumentChecker.required(serviceInterfaceMeta, "serviceInterfaceMeta");
        ICommunicator communicator = this._communicators.get(this._communicatorName);
        if (communicator == null) {
            throw new KernelException("No communicator was found by name {}", this._communicatorName);
        }

        // The request url should be: HTTP GET /[context]/
        // The response JSON should be:
        // {
        //     interface-id: 'xxx',
        //     communication: 'RESTful',
        //     service-metas: [{
        //         name: 'xxx',
        //         return-type-name: 'xxx',
        //         uri: 'xxx',
        //         method: 'GET',
        //         encode: 'JSON',
        //         argument-metas: [{
        //             type-name: 'xxx',
        //             from: 'xxx',
        //             index: 2,
        //             name: 'xxx'
        //         }, ...]
        //     }, ...]
        // }
        // verify service interface meta from remote
        if (RestfulCommunicator.id.equals(communicator.getId())) {
            // do restful way
//            String url = StringHelper.makeString("http://{}:{}/{}",
//                    this._host, this._port, this._uriPrefix);

            NamedArgumentMapping namedArg = new NamedArgumentMapping(ArgumentFrom.Param, Type.Q_STRING, "interface");
            List<ArgumentMeta> args = new ArrayList<>();
            args.add(namedArg);
            List<HttpMethod> methods = new ArrayList<>();
            methods.add(HttpMethod.GET);
            RestfulServiceMeta svcMeta = new RestfulServiceMeta(
                    this._host,
                    this._port,
                    "DiscoverService",
                    ServiceDiscoveryResponse.class.getCanonicalName(),
                    args,
                    this._uriPrefix,
                    methods,
                    JsonStringCodec.NAME);
            // Using interface type name to query since server side do not know client service id (interface id)
            ServiceDiscoveryResponse response = (ServiceDiscoveryResponse) communicator.request(svcMeta, serviceInterfaceMeta.getInterfaceType().getName());
            if (response == null) {
                throw new KernelException("No response when request service - {}", svcMeta);
            }
            // update service interface
            List<ServiceMeta> svcMetas = parseResponse(response);
            serviceInterfaceMeta.updateServiceMetas(svcMetas);
            serviceInterfaceMeta.setCommunicatorName(response.getData().getCommunication());
            return serviceInterfaceMeta;
        } else {
            throw new KernelException("Unsupported communicator name - {}", this._communicatorName);
        }
    }

    private List<ServiceMeta> parseResponse(ServiceDiscoveryResponse response) {
        if (! CommonResponseCode.SUCCESS.equals(response.code)) {
            String msg = this._msgLoader.load(response.code);
            throw new KernelException("Discover service failed, reason is {}", msg);
        }
        if (response.data == null) {
            throw new KernelException("Discover service failed, no data was returned");
        }

        List<ServiceMeta> svcMetas = new ArrayList<>();
        String comm = response.data.communication;
        Looper.from(response.data.serviceMetas)
                .map(svcMeta -> {
                    if (RestfulCommunicator.id.equals(comm)) {
                        List<ArgumentMeta> args = new ArrayList<>();
                        if (svcMeta.argumentMetas != null && svcMeta.argumentMetas.length > 0) {
                            Looper.from(svcMeta.argumentMetas)
                                    .map(argMeta -> {
                                        if (! Strings.isNullOrEmpty(argMeta.name)) {
                                            return new NamedArgumentMapping(argMeta.from, argMeta.typeName, argMeta.name);
                                        } else {
                                            return new IndexedArgumentMapping(argMeta.from, argMeta.typeName, argMeta.index);
                                        }
                                    }).foreach(args::add);
                        }
                        RestfulServiceMeta restSvcMeta = new RestfulServiceMeta(
                                svcMeta.host, svcMeta.port, svcMeta.name, svcMeta.returnTypeName, args, svcMeta.context, svcMeta.methods, svcMeta.codec);
                        restSvcMeta.setId(svcMeta.getId());
                        return restSvcMeta;
                    } else {
                        throw new KernelException("Unsupported communication type - ", comm);
                    }
                }).foreach(svcMetas::add);
        return svcMetas;


//        List<Map> svcList = (List<Map>) response.get(Constant.SVC_METAS);
//        if (svcList == null || svcList.size() == 0) {
//            throw new KernelException("No service meta is defined in service interface - {}", response.get(Constant.INTF_ID));
//        }
//        for (Map svc : svcList) {
//            String commType = (String) svc.get(Constant.COMM_TYPE);
//            if (RestfulCommunicator.id.equals(commType)) {
//                String svcName = (String) svc.get(Constant.NAME);
//                String rtnType = (String) svc.get(Constant.RTN_TYPE_NAME);
//                String uri = (String) svc.get(Constant.URI);
//                HttpMethod method = HttpMethod.parse((String) svc.get(Constant.METHOD));
//                String format = (String) svc.get(Constant.FORMAT);
//                List<Map> argList = (List<Map>) svc.get(Constant.ARG_METAS);
//                List<ArgumentMapping> args = new ArrayList<>();
//                if (argList != null || argList.size() > 0) {
//                    for (Map arg : argList) {
//                        ArgumentMapping argMapping;
//                        String argName = (String) arg.get(Constant.NAME);
//                        String argType = (String) arg.get(Constant.TYPE_NAME);
//                        ArgumentFrom argFrom = ArgumentFrom.parse((String) arg.get(Constant.FROM));
//                        if (! Strings.isNullOrEmpty(argName)) {
//                            argMapping = new NamedArgumentMapping(argFrom, argType, argName);
//                        } else {
//                            int index = Integer.parseInt((String) arg.get(Constant.INDEX));
//                            argMapping = new IndexedArgumentMapping(argFrom, argType, index);
//                        }
//                        args.add(argMapping);
//                    }
//                }
//                RestfulServiceMeta svcMeta = new RestfulServiceMeta(svcName, rtnType, args, uri, method, format);
//                svcMetas.add(svcMeta);
//            } else {
//                throw new KernelException("Unsupported communication type - ", commType);
//            }
//        }
//        return svcMetas;
    }
}
