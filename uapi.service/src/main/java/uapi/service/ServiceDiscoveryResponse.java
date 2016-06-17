/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import uapi.service.web.ArgumentFrom;
import uapi.service.web.HttpMethod;

/**
 * Created by xquan on 6/14/2016.
 */
public class ServiceDiscoveryResponse extends Response {

    public Data data;

    public static final class Data {

        @JsonProperty("interface-id")
        public String interfaceId;

        @JsonProperty("communication")
        public String communication;

        @JsonProperty("service-meta")
        public ServiceMeta[] serviceMetas;
    }

    public static final class ServiceMeta {

        public String name;

        @JsonProperty("return-type-name")
        public String returnTypeName;

        @JsonProperty("uri")
        public String uri;

        @JsonProperty("method")
        public HttpMethod[] methods;

        @JsonProperty("codec")
        public String codec;

        @JsonProperty("argument-metas")
        public ArgumentMeta[] argumentMetas;
    }

    public static final class ArgumentMeta {

        @JsonProperty("type-name")
        public String typeName;

        @JsonProperty("from")
        public ArgumentFrom from;

        @JsonProperty("index")
        public int index;

        @JsonProperty("name")
        public String name;
    }
}
