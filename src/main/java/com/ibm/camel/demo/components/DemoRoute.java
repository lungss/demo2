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

        restConfiguration().component("netty-http").host("0.0.0.0").port(8081).bindingMode(RestBindingMode.json);
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
        // .to("http://istio-ingressgateway-istio-system.mycluster-na-d4a42c1d53effecb3a546f2cced5589b-0000.sjc03.containers.appdomain.cloud/echoget/helloworld?bridgeEndpoint=true");
        .to("http://epf-account:8080/echoget/helloworld?bridgeEndpoint=true");

        from("rest://get:2echoGet/{echoValue}").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String echoValue = exchange.getIn().getHeader("echoValue").toString();
                if (echoValue.compareToIgnoreCase("FAIL") == 0) {
                	throw new Exception("fail");
                } else if (echoValue.compareToIgnoreCase("SLEEP") == 0) {
                	Thread.sleep(5000);
                }
                System.out.println(echoValue);
                exchange.getMessage().setBody(exchange.getIn().getHeader("echoValue"));
            }
        });

        /*
		from("file:C:/inboxPOST?noop=true").process(new CreateEmployeeProcessor()).marshal(jsonDataFormat)
		.setHeader(Exchange.HTTP_METHOD, simple("POST"))
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json")).to("http://localhost:8080/employee")
		.process(new MyProcessor());
		*/

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
