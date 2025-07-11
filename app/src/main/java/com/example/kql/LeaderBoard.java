package com.example.kql;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LeaderBoard extends AppCompatActivity {

    ListView list;
    LeaderBoardAdapter participantAdapter;
    List<Participant> participants;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader_board);

        list = findViewById(R.id.list);
        participants = new ArrayList<>();
        participantAdapter = new LeaderBoardAdapter(this, participants);
        list.setAdapter(participantAdapter);

        HashSet<String> leaderboardEmails = new HashSet<>();

        firestore.collection("Rooms_Requests").get().addOnSuccessListener(roomSnapshots -> {
            int totalRooms = roomSnapshots.size();
            final int[] completedRooms = {0};

            for (DocumentSnapshot roomDoc : roomSnapshots.getDocuments()) {
                Boolean isFinished = roomDoc.getBoolean("isFinished");
                String roomCode = roomDoc.getString("Room Code");
                if (isFinished != null && isFinished && roomCode != null) {
                    firestore.collection("Rooms_Requests").document(roomCode)
                            .collection("AnswersGivenBy").get().addOnSuccessListener(answersSnap -> {
                                for (DocumentSnapshot doc : answersSnap.getDocuments()) {
                                    String email = doc.getString("AnsweredBy");
                                    if (email != null) {
                                        leaderboardEmails.add(email);
                                    }
                                }
                                completedRooms[0]++;
                                if (completedRooms[0] == totalRooms) {
                                    fetchLeaderboardScores(new ArrayList<>(leaderboardEmails));
                                }
                            });
                } else {
                    completedRooms[0]++;
                    if (completedRooms[0] == totalRooms) {
                        fetchLeaderboardScores(new ArrayList<>(leaderboardEmails));
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchLeaderboardScores(List<String> emails) {
        firestore.collection("LeaderBoard").get().addOnSuccessListener(snapshot -> {
            List<Participant> tempList = new ArrayList<>();
            for (String email : emails) {
                String safeKey = email.replace(".", "_");
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    if (doc.contains(safeKey)) {
                        try {
                            int score = Integer.parseInt(doc.getString(safeKey));
                            tempList.add(new Participant(email, score));
                        } catch (NumberFormatException ignored) {
                        }
                        break;
                    }
                }
            }

            // Sort by score descending
            tempList.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

            // Assign ranks with tie handling
            int rank = 1;
            int prevScore = -1;
            int sameRankCount = 0;

            for (int i = 0; i < tempList.size(); i++) {
                Participant p = tempList.get(i);
                if (p.getScore() == prevScore) {
                    p.setRank(rank); // Same score, same rank
                    sameRankCount++;
                } else {
                    rank = rank + sameRankCount; // Increment rank properly
                    p.setRank(rank);
                    sameRankCount = 1;
                    prevScore = p.getScore();
                }
                participants.add(p);
            }

            participantAdapter.notifyDataSetChanged();
        });
    }
}
