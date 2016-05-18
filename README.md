## uapi

[![Build Status](https://travis-ci.org/minjing/uapi.svg?branch=master)](https://travis-ci.org/minjing/uapi)
[![Code Coverage](https://img.shields.io/codecov/c/github/minjing/uapi/master.svg)](https://codecov.io/github/minjing/uapi?branch=master)

Welcome to UAPI project!

The UAPI (Universal Application Platform Infrastructure) project will provide a generic platfor/framework and some basic facilities to make build application easier and faster.

It contains below components:
* Kernel - The core framework core, it contains a extensible code generation framework based on java APT and a service injection library.
* Service - Support Spring bean, remote service injection.
* Config - The configuration framework
* Log - The log framework
* Job - The multiple thread handling framework
* Flow - The generic work flow framework
* Web - The web framework

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