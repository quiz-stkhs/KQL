package com.example.kql;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QuizResults extends AppCompatActivity {

    ListView list;
    ParticipantAdapter participantAdapter;
    List<Participant> participants = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String roomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_results);

        list = findViewById(R.id.list);
        participantAdapter = new ParticipantAdapter(this, participants);
        list.setAdapter(participantAdapter);

        roomCode = getIntent().getStringExtra("Room");
        if (roomCode == null) {
            Toast.makeText(this, "Room code not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchAnsweringEmails();
        applyInsets();
    }

    private void fetchAnsweringEmails() {
        firestore.collection("Rooms_Requests").document(roomCode)
                .collection("AnswersGivenBy").get()
                .addOnSuccessListener(answerSnap -> {
                    List<DocumentSnapshot> docs = answerSnap.getDocuments();
                    List<String> emails = new ArrayList<>();

                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        String email = doc.getString("AnsweredBy");

                        if (email != null && !email.equals("None")) {
                            emails.add(email);
                        }
                    }

                    loadScores(emails);
                });
    }

    private void loadScores(List<String> emails) {
        if (emails.isEmpty()) return;

        firestore.collection("Rooms_Requests").document(roomCode)
                .get()
                .addOnSuccessListener(roomSnap -> {
                    if (roomSnap == null || !roomSnap.exists()) return;

                    for (int i = 0; i < emails.size(); i++) {
                        String email = emails.get(i);
                        String safeKey = email.replace(".", "_");
                        String scoreStr = roomSnap.getString(safeKey);

                        int score = 0;
                        if (scoreStr != null) {
                            try {
                                score = Integer.parseInt(scoreStr);
                            } catch (NumberFormatException ignored) {}
                        }

                        processParticipant(email, safeKey, score, emails.size());
                    }
                });
    }

    private void processParticipant(String email, String safeKey, int score, int totalCount) {

        if(getIntent().getExtras().containsKey("Origin"))
        {
            firestore.collection("LeaderBoard").document(email)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        int previousScore = 0;

                        if (userDoc.exists()) {
                            String previousStr = userDoc.getString(safeKey);
                            if (previousStr != null) {
                                try {
                                    previousScore = Integer.parseInt(previousStr);
                                } catch (NumberFormatException ignored) {}
                            }
                        }

                        int totalScore = previousScore + score;


                        HashMap<String, Object> updateUser = new HashMap<>();
                        updateUser.put(safeKey, String.valueOf(totalScore));

                        firestore.collection("LeaderBoard").document(email)
                                .set(updateUser, SetOptions.merge());
                    });
        }
        // Update user's overall leaderboard


        // Update per-room leaderboard
//        HashMap<String, Object> updateRoom = new HashMap<>();
//        updateRoom.put(safeKey, score);
//        firestore.collection("LeaderBoard").document(roomCode)
//                .set(updateRoom, SetOptions.merge());

        // Add to local list and update view if last one
        participants.add(new Participant(email, score));

        if (participants.size() == totalCount) {
            Collections.sort(participants, (a, b) -> Integer.compare(b.getScore(), a.getScore()));

            for (int i = 0; i < participants.size(); i++) {
                participants.get(i).setRank(i + 1);
            }

            participantAdapter.notifyDataSetChanged();
        }
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
