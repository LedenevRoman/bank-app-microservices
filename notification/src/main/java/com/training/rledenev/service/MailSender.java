package com.training.rledenev.service;

import java.util.Map;

public interface MailSender {

    void send(String[] to, String subject, String template, Map<String, Object> templateVars);
}
