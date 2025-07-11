package com.example.kql;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ParticipantList extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ListView listView;
    ArrayList<ParticipantModel> participants;
    String roomCode;
    TextView nxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_list);

        listView = findViewById(R.id.participantListView);
        participants = new ArrayList<>();
        roomCode = getIntent().getStringExtra("Room");
        nxt = findViewById(R.id.nxt);
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParticipantList.this, MainQuiz.class).putExtra("Room", getIntent().getExtras().getString("Room")));
                finish();
            }
        });
        firestore.collection("Rooms_Requests")
                .document(roomCode)
                .collection("Attendance").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        participants.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String name = doc.getString("Name");
                            String email = doc.getString("Email");
                            String role = doc.getString("Role");
                            participants.add(new ParticipantModel(name, email, role));
                        }

                        ParticipantListAdapter adapter = new ParticipantListAdapter(ParticipantList.this, participants);
                        listView.setAdapter(adapter);
                    }
                });
    }
}
