## UAPI (Universal Application Platform Infrastructure)

[![Build Status](https://travis-ci.org/minjing/uapi.svg?branch=master)](https://travis-ci.org/minjing/uapi)
[![Code Coverage](https://img.shields.io/codecov/c/github/minjing/uapi/master.svg)](https://codecov.io/github/minjing/uapi?branch=master)

## Introduction

Welcome to UAPI project!

The UAPI project will provide a generic platform/framework and some basic facilities to make build application easier and faster.

It contains below components:
* Kernel - The core framework core, it contains a extensible code generation framework based on java APT and a service injection library.
* Service - Support Spring bean, remote service injection.
* Config - The configuration framework
* Log - The log framework
* Job - The multiple thread handling framework
* Flow - The generic work flow framework
* Web - The web framework to support RESTful service

*This framework is still under developing, most features are unstable and the APIs may be changed frequently, welcome to evaluate and give me the feedback*

## A DI framework

The core component of UAPI is a DI framework which is Annotation based.

### Inject Service by type

Declare a managed service first:
```java
@Service
public class Greeting {
    public void greeting() {
        System.out.print("Hello!");
    }
}
```

Then you can reference the service from other service
```java
@Service
public class SayGreeting {

    @Inject
    Greeting _greeting;

    public void sayGreeting() {
        this._greeting.greeting();
    }
}
```

The @Inject annotation will make DI framework inject Greeting service into SayGreeting service.

### Inject service by name

In default the service injection is service type based, but sometime you may want to inject service by its name, you can follow:

```java
@Service(ids="greeting")
public class Greeting {
    public void greeting() {
        System.out.print("Hello!");
    }
}

@Service
public class SayGreeting {

    @Inject("greeting")
    Greeting _greeting;

    public void sayGreeting() {
        this._greeting.greeting();
    }
}
```

### Use Service Factory

In general, the service is singleton, which mean if multiple services reference one service A, only one service A instance is injected to multiple services.
So if you want to multiple services reference different service A instance, you can use IServiceFactory:

```java
@Service
public class GreetingFactory implement IServiceFactory<Greeting> {

    @Override
    public Greeting createService(Object serveFor) {
        return new Greeting();
    }
}
```
The serveFor is tell the factory that which service will reference the created service.

## Configuration

Most of application is configurable, so the configuration is basic functionality. UAPI support simple configuration injection.

### Inject simple configuration

For some basic configuration item like String, int, double..., they can be injected directly by Config annotation:

```java
@Service
public class ConfigurableService {

    @Config(path="name")
    String _name;

    @Config(path="age")
    int _age;

    @Inject
    ILogger _logger;

    @Inject
    IRegistry _svcReg;

    @Init
    public void init() {
        this._logger.info("Configured {} = {}", "name", this._name);
        this._logger.info("Configured {} = {}", "age", this._age);
        this._logger.info("Configured {} = {}", "address", this._address);
    }
}
```
The ConfigurableService declare it depends on two configurations: name and age, the configuration path is name and age.
But where are the configurations defined? Ok, let's defined it in a yaml file named config.yml which is put in application conf folder:

```yaml
name: Hello
age: 22
```

And then you need launch application with *-config=conf/config.yml" option, the configuration will be inject to the service automatically.

*Note:* The IRegistry is required, since the Configuration framework need use it to find out dependent service.

### Inject complex configuration

For some complex configuration object, you may want to access it by Java object, you can using a configuration parser:
```java
@Service
public class ConfigurableService {

    @Config(path="address", parser=AddressParser.class)
    Address _address;

    @Inject
    ILogger _logger;

    @Inject
    IRegistry _svcReg;

    @Init
    public void init() {
        this._logger.info("Configured {} = {}", "address", this._address);
    }
}
```

The config object and its parser:
```java
public class Address {

    public String home;
    public String office;

    public String toString() {
        return "home=" + home + ",office=" + office;
    }
}

@Service(IConfigValueParser.class)
public class AddressParser implements IConfigValueParser {

    private static final String[] supportedTypesIn = new String[] {
            List.class.getCanonicalName()
    };
    private static final String[] supportedTypesOut = new String[] {
            Address.class.getCanonicalName()
    };

    @Override
    public boolean isSupport(String inType, String outType) {
        return CollectionHelper.isContains(supportedTypesIn, inType) && CollectionHelper.isContains(supportedTypesOut, outType);
    }

    @Override
    public String getName() {
        return AddressParser.class.getCanonicalName();
    }

    @Override
    public Address parse(Object value) {
        List<Map> addrList = (List<Map>) value;
        Address address = new Address();
        for (Map addr : addrList) {
            if (addr.get("home") != null) {
                address.home = addr.get("home").toString();
            }
            if (addr.get("office") != null) {
                address.office = addr.get("office").toString();
            }
        }
        return address;
    }
}
```

The configuration framework will try to find the parser from IRegistry, if it is found then the framework will try use it to parse the configurations and inject to the service.

## Web framework

### RESTful service

UAPI Web framework embed a Servlet container (Jetty) to provide HTTP service, it support expose a normal service to a RESTful service:
```java
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
```

The Web framework is developing, it will be a part of remote service framework, when remote service framework done then you can inject a RESTful service into your service like inject a local service without any http invocation knowledge.

The service will be exposed as a RESTful service, the access URL like: http://localhost/restful/hello/[name]?test=test
The @Exposure declare the service will be exposed as RESTful service with "hello" context.
The [name] will be mapped to name argument of sayHello method, and the test query parameter will be mapped to test argument of sayHello method.

## Version History
* v0.2 - in working
  1. Remote service injection which is interface based.
  1. Generate remote service proxy based on specific interface.
  1. Implement restful remote proxy.
  1. Multiple restful services can be aggregated with a interface and can be exposed to a restful service.

* v0.1
  1. Enhance service class at build time and based on customized annotation.
  1. Support local service injection.
  1. Support Spring service injection.
  1. Annotation based configuration.
  1. A simple log service provider which is Logback based.
  1. Embedded web server which is Jetty based.
  1. Support a simple restful service register to embedded web service based on annotation.