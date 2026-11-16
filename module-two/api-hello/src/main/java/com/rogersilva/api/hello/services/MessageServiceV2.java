package com.rogersilva.api.hello.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("v2")
public class MessageServiceV2 implements IMessageService{
    @Override
    public String sayCustomMessage(String original) {
        return original.replace(" ", "-");
    }
}
