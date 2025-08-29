package com.pblues.sportsshop.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendMail(String to, String subject, String body);
}