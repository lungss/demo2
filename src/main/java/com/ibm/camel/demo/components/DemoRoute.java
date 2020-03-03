package com.ibm.camel.demo.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DemoRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        restConfiguration().component("netty-http").host("0.0.0.0").port(80).bindingMode(RestBindingMode.json);
//        .setJsonDataFormat("json-jackson");

        from("rest://post:2echo").unmarshal().json(JsonLibrary.Jackson).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getBody());
                Map bodyMap = (Map) exchange.getIn().getBody();
                exchange.getMessage().setHeader("postEcho", bodyMap.get("echo"));
            }
        })
        .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http.HttpMethods.GET))
        // .to("http://localhost:8080/echoGet/helo?bridgeEndpoint=true");
        .to("http://istio-ingressgateway-istio-system.mycluster-383951-d4a42c1d53effecb3a546f2cced5589b-0000.sng01.containers.appdomain.cloud/echoget/helloworld?bridgeEndpoint=true");

        from("rest://get:2echoGet/{echoValue}").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(exchange.getIn().getHeader("echoValue"));
                exchange.getMessage().setBody(exchange.getIn().getHeader("echoValue"));
            }
        });



//        rest("hi").get("/get").to("direct:processGet");
//
//        from("direct:processGet").process(new Processor() {
//            @Override
//            public void process(Exchange exchange) throws Exception {
//                System.out.println("processGet exchanges" + exchange.getIn().getHeaders());
//            }
//        });

    }
}
