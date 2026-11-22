package com.example.accountservice.dto;

public record ErrorResponse(int status, String error, String message) { }
