package com.example.kql;

public class Answer {
    String email;
    String name;
    String role;
    String ans;
    String rc;
    String qno;


    // Constructor
    public Answer(String name, String email, String role, String ans, String rc, String qno) {
        this.email = email;
        this.role = role;
        this.ans = ans;
        this.name = name;
        this.rc = rc;
        this.qno = qno;

    }

    // Empty constructor for Firestore
    public Answer() {}

    // Getters and setters

}
