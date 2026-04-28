package com.remizov.deal.service;

public interface DocumentService {
    void sendDocuments(String statementId);
    void signDocuments(String statementId);
    void codeDocuments(String statementId);
}