package com.alkemy.springboot.app.email;

public interface EmailSender {

    void send(String to, String email);
}
