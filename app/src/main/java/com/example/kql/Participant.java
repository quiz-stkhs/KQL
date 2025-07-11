package com.example.kql;

public class Participant {
    private String name;
    private int score;
    private int rank;  // ✅ Added rank

    public Participant(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }

    // Setter for rank
    public void setRank(int rank) {
        this.rank = rank;
    }
}
