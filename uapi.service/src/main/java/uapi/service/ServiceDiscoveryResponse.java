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
import uapi.web.ArgumentFrom;
import uapi.web.HttpMethod;

import java.util.List;

/**
 * Created by xquan on 6/14/2016.
 */
public class ServiceDiscoveryResponse extends Response {

    public Data data;

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static final class Data {

        @JsonProperty("interface-id")
        public String interfaceId;

        @JsonProperty("communication")
        public String communication;

        @JsonProperty("service-meta")
        public List<ServiceMeta> serviceMetas;

        public void setInterfaceId(String interfaceId) {
            this.interfaceId = interfaceId;
        }

        public void setCommunication(String communication) {
            this.communication = communication;
        }

        public void setServiceMetas(List<ServiceMeta> serviceMetas) {
            this.serviceMetas = serviceMetas;
        }

        public String getInterfaceId() {
            return interfaceId;
        }

        public String getCommunication() {
            return communication;
        }

        public List<ServiceMeta> getServiceMetas() {
            return serviceMetas;
        }
    }

    public static final class ServiceMeta {

        public String id;

        public String name;

        @JsonProperty("return-type-name")
        public String returnTypeName;

        @JsonProperty
        public String host;

        @JsonProperty
        public int port;

        @JsonProperty("context")
        public String context;

        @JsonProperty("methods")
        public List<HttpMethod> methods;

        @JsonProperty("codec")
        public String codec;

        @JsonProperty("argument-metas")
        public ArgumentMeta[] argumentMetas;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReturnTypeName() {
            return returnTypeName;
        }

        public void setReturnTypeName(String returnTypeName) {
            this.returnTypeName = returnTypeName;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public List<HttpMethod> getMethods() {
            return methods;
        }

        public void setMethods(List<HttpMethod> methods) {
            this.methods = methods;
        }

        public String getCodec() {
            return codec;
        }

        public void setCodec(String codec) {
            this.codec = codec;
        }

        public ArgumentMeta[] getArgumentMetas() {
            return argumentMetas;
        }

        public void setArgumentMetas(ArgumentMeta[] argumentMetas) {
            this.argumentMetas = argumentMetas;
        }
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

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public ArgumentFrom getFrom() {
            return from;
        }

        public void setFrom(ArgumentFrom from) {
            this.from = from;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
