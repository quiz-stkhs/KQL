package com.example.kql;

public class QuizRequest {
 String quizName;
String date;
String mail;
 String name;
String time;
 String roomcode;
 String status;

    // Constructor
    public QuizRequest(String quizName, String date, String mail, String name, String time, String roomcode, String status) {
        this.quizName = quizName;
        this.date = date;
        this.mail = mail;
        this.name = name;
        this.time = time;
        this.roomcode = roomcode;
        this.status = status;
    }

    // Empty constructor for Firestore
    public QuizRequest() {}

    // Getters and setters

}
