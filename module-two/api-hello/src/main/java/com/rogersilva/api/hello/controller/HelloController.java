package com.rogersilva.api.hello.controller;

import com.rogersilva.api.hello.services.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Qualifier("v1")
    @Autowired
    private IMessageService messageServiceV1;

    @Qualifier("v2")
    @Autowired
    private IMessageService messageServiceV2;

    @GetMapping("/v1")
    public String sayHelloV1() {
        return messageServiceV1.sayCustomMessage("Hello World! Roger Silva");
    }

    @GetMapping("/v2")
    public String sayHelloV2() {
        return messageServiceV2.sayCustomMessage("Hello World! Roger Silva");
    }
}
