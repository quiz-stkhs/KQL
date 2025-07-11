package com.example.kql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainQuiz extends AppCompatActivity {
    LinearLayout main;
    TextView qnm, qno;
    TextView nxt;
    EditText qn;
    ListView listViewAns;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fb = FirebaseAuth.getInstance();
    String strQn, strQno, ans;
    List<Answer> ansList;
    AnsAdapter arrayAdapter;
    Button btnList;
    int q = 1;
    String roomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_quiz);
//        btnList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(MainQuiz.this, ParticipantList.class).putExtra("Room", getIntent().getExtras().getString("Room")));
//            }
//        });
        main = findViewById(R.id.main);
        qnm = findViewById(R.id.qnm);
        qno = findViewById(R.id.qno);
        qn = findViewById(R.id.qn);
        nxt = findViewById(R.id.nxt);
// Fetch and set the quiz name from Firestore
        db.collection("Rooms_Requests").document(getIntent().getExtras().getString("Room"))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String quizName = documentSnapshot.getString("Quiz Name"); // Pay attention to the exact key name
                        if (quizName != null && !quizName.isEmpty()) {
                            qnm.setText(quizName);
                        } else {
                            qnm.setText("Quiz");
                        }
                    } else {
                        qnm.setText("Quiz");
                    }
                })
                .addOnFailureListener(e -> {
                    qnm.setText("Quiz");
                    Toast.makeText(MainQuiz.this, "Failed to load quiz name", Toast.LENGTH_SHORT).show();
                });

        listViewAns = findViewById(R.id.listViewAns);

        roomCode = getIntent().getExtras().getString("Room");

        ansList = new ArrayList<>();
        arrayAdapter = new AnsAdapter(MainQuiz.this, ansList);
        listViewAns.setAdapter(arrayAdapter);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Active", true);
        hashMap.put("ActiveQn", "1");

        db.collection("Rooms_Requests").document(roomCode).update(hashMap);

        loadQuestion("1"); // Start with question 0
        q = 1;
        nxt.setText("Next Question       -->");

        nxt.setOnClickListener(v -> {
            ansList.clear();
            arrayAdapter.notifyDataSetChanged();



                String prevQ = String.valueOf(q - 1);
                db.collection("Rooms_Requests").document(roomCode)
                        .collection("Questions").document(prevQ).get()
                        .addOnSuccessListener(doc -> {
                            if (!doc.contains("AnsweredBy")) {
                                db.collection("Rooms_Requests").document(roomCode)
                                        .collection("Questions").document(prevQ)
                                        .update("AnsweredBy", "None");
                            }

                            loadQuestion(String.valueOf(q));
                            q++;
                        });

        });

        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadQuestion(String questionNo) {
        db.collection("Rooms_Requests").document(roomCode)
                .collection("Questions").document(questionNo).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        strQn = doc.getString("QN");
                        strQno = doc.getString("Qno");
                        ans = doc.getString("Answer");

                        qn.setText(strQn);
                        qno.setText(strQno);

                        db.collection("Rooms_Requests").document(roomCode)
                                .update("ActiveQn", questionNo);

                        listenForAnswers(questionNo);
                    } else {
                        db.collection("Rooms_Requests").document(roomCode)
                                .update("isFinished", true);
                        Intent intent = new Intent(MainQuiz.this, QuizResults.class);
                        intent.putExtra("Room", roomCode);
                        intent.putExtra("Origin", "Host");
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void listenForAnswers(String qnoText) {
        db.collection("Rooms_Requests").document(roomCode)
                .collection("Questions").document(qnoText)
                .collection("Answers")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        ansList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Boolean seen = doc.getBoolean("SeenByHost");
                            if (seen == null || !seen) {
                                ansList.add(new Answer("", doc.getString("Email"), "", doc.getString("Answer"), roomCode, qnoText));
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
    }
}
