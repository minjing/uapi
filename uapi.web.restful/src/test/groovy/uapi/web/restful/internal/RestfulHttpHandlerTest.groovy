/*
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.web.restful.internal

import spock.lang.Specification
import uapi.service.ArgumentMeta
import uapi.service.IStringCodec
import uapi.service.ServiceMeta
import uapi.service.TypeMapper
import uapi.web.restful.IRestfulService
import uapi.web.restful.NamedArgumentMapping
import uapi.web.http.HttpMethod
import uapi.web.http.IHttpRequest
import uapi.web.http.IHttpResponse
import uapi.web.restful.ArgumentFrom
import uapi.web.restful.IRestfulInterface

/**
 * Test case for RestfulHttpHandler
 */
class RestfulHttpHandlerTest extends Specification {

    def 'Test discovery request'() {
        def namedArg = Mock(NamedArgumentMapping) {
            getName() >> 'argName'
            getFrom() >> ArgumentFrom.Uri
            getType() >> 'string'
        }
        List<ArgumentMeta> args = new ArrayList<>()
        args.add(namedArg)
        def svcMeta = Mock(ServiceMeta) {
            getId() >> 'hello'
            getName() >> 'hello'
            getReturnTypeName() >> 'string'
            getArgumentMetas() >> args
        }
        Map<ServiceMeta, List<HttpMethod>> methodMapping = new HashMap<>()
        methodMapping.put(svcMeta, [HttpMethod.GET] as List)
        def restIntf = Mock(IRestfulInterface) {
            getInterfaceId() >> intfId
            getMethodHttpMethodInfos() >> methodMapping
        }
        def request = Mock(IHttpRequest) {
            uri() >> '/rest?interface=' + intfId
            params() >> [ 'interface': [intfId] as List]
        }
        def response = Mock(IHttpResponse) {
            1 * write(_)
            1 * flush()
        }

        when:
        RestfulHttpHandler restfulHandler = new RestfulHttpHandler()
        restfulHandler._host = host
        restfulHandler._port = port
        restfulHandler._codecName = codec
        restfulHandler._codecs.put(codec, Mock(IStringCodec) {
            1 * decode(_, _)
        })
        restfulHandler._restIntfs.add(restIntf)

        then:
        restfulHandler.get(request, response)

        where:
        host        | port  | codec     | intfId
        'localhost' | 8080  | 'JSON'    | 'uapi.ITest'
    }

    def 'Test service invocation'() {
        def svc = Mock(IRestfulService) {
            getMethodArgumentsInfo(HttpMethod.GET) >> [Mock(NamedArgumentMapping) {
                getName() >> paramName
                getFrom() >> ArgumentFrom.Param
                getType() >> String.class.getName()
            }]
            invoke(HttpMethod.GET, _) >> result
        }
        def request = Mock(IHttpRequest) {
            method() >> HttpMethod.GET
            uri() >> '/rest/' + svcId + '?' + paramName + '=' + paramValue
            params() >> [ 'title' : [paramValue] as List]
        }
        def response = Mock(IHttpResponse) {
            1 * write(_)
            1 * flush()
        }

        when:
        RestfulHttpHandler restfulHandler = new RestfulHttpHandler()
        restfulHandler._host = host
        restfulHandler._port = port
        restfulHandler._codecName = codec
        restfulHandler._codecs.put(codec, Mock(IStringCodec) {
            1 * decode(_, _)
        })
        restfulHandler._restSvcs.put(svcId, svc)
        restfulHandler._typeMapper = Mock(TypeMapper) {
            getType(_) >> String.class
        }

        then:
        restfulHandler.get(request, response)

        where:
        host        | port  | codec     | svcId     | paramName | paramValue    | result
        'localhost' | 8080  | 'JSON'    | 'hello'   | 'title'   | 'Mr'          | 'Hello'
    }
}
