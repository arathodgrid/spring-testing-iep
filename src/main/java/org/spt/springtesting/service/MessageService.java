package org.spt.springtesting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Value("${app.message:Default Message}")
    private String message;

    public String getMessage() {
        return message;
    }
}
