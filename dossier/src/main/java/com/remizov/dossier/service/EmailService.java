package com.remizov.dossier.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}