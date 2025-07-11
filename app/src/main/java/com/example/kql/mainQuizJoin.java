package com.example.kql;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.HashMap;

public class mainQuizJoin extends AppCompatActivity {
    LinearLayout main;
    TextView qnm, qno;
    EditText ans;
    TextView qn;
    Button nxt;
    TextView qnm1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fb = FirebaseAuth.getInstance();
    String roomCode, activeQn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_quiz_join);

        main = findViewById(R.id.main);
        qnm = findViewById(R.id.qnm);
        qno = findViewById(R.id.qno);
        qn = findViewById(R.id.qn);
        ans = findViewById(R.id.ans);
        nxt = findViewById(R.id.nxt);
        qnm1 = findViewById(R.id.qnm1);
        roomCode = getIntent().getExtras().getString("Room");
// Fetch and set the quiz name from Firestore
        db.collection("Rooms_Requests").document(getIntent().getStringExtra("Room"))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String quizName = documentSnapshot.getString("Quiz Name"); // Pay attention to the exact key name
                        if (quizName != null && !quizName.isEmpty()) {
                            qnm1.setText(quizName);
                        } else {
                            qnm1.setText("Quiz");
                        }
                    } else {
                        qnm1.setText("Quiz");
                    }
                })
                .addOnFailureListener(e -> {
                    qnm1.setText("Quiz");
                    Toast.makeText(mainQuizJoin.this, "Failed to load quiz name", Toast.LENGTH_SHORT).show();
                });

        db.collection("Rooms_Requests").document(roomCode).addSnapshotListener((roomSnap, error) -> {
            if (roomSnap != null && roomSnap.exists()) {
                activeQn = roomSnap.getString("ActiveQn");

                if (Boolean.TRUE.equals(roomSnap.getBoolean("isFinished"))) {
                    Intent intent = new Intent(mainQuizJoin.this, QuizResults.class);
                    intent.putExtra("Room", roomCode);
                    startActivity(intent);
                    finish();
                }

                if (activeQn != null) {
                    db.collection("Rooms_Requests").document(roomCode)
                            .collection("Questions").document(activeQn).get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    qn.setText(doc.getString("QN"));
                                    qno.setText(doc.getString("Qno"));
                                }
                            });

                    db.collection("Rooms_Requests").document(roomCode)
                            .collection("Questions").document(activeQn)
                            .addSnapshotListener((qSnap, qErr) -> {
                                if (qSnap != null && qSnap.exists() && qSnap.contains("AnsweredBy")) {
                                    Dialog2(qSnap.getString("Answer"), qSnap.getString("AnsweredBy"));
                                }
                            });

                    db.collection("Rooms_Requests").document(roomCode)
                            .collection("Questions").document(activeQn)
                            .collection("Answers").document(fb.getCurrentUser().getEmail())
                            .addSnapshotListener((aSnap, aErr) -> {
                                if (aSnap != null && aSnap.exists() &&
                                        Boolean.TRUE.equals(aSnap.getBoolean("SeenByHost")) &&
                                        aSnap.contains("Status")) {
                                    Dialog(aSnap.getString("Answer"), aSnap.getString("Status"));
                                }
                            });
                }
            }
        });

        nxt.setOnClickListener(v -> {
            String answer = ans.getText().toString().trim();
            if (answer.isEmpty()) {
                Toast.makeText(mainQuizJoin.this, "Please fill an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Email", fb.getCurrentUser().getEmail());
            hashMap.put("Answer", answer);
            hashMap.put("SeenByHost", false);

            db.collection("Rooms_Requests").document(roomCode)
                    .collection("Questions").document(activeQn)
                    .collection("Answers").document(fb.getCurrentUser().getEmail())
                    .set(hashMap);
        });

        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Dialog(String ans1, String res1) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog3);
        EditText ans = dialog.findViewById(R.id.ans);
        EditText res = dialog.findViewById(R.id.res);
        ans.setText(ans1);
        res.setText(res1);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.close).setOnClickListener(v -> dialog.dismiss());
        if (!isFinishing()) dialog.show();
    }

    public void Dialog2(String ans1, String answerer) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog4);
        EditText ans = dialog.findViewById(R.id.ans);
        EditText res = dialog.findViewById(R.id.res);
        ans.setText(ans1);
        res.setText(answerer);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.close).setOnClickListener(v -> dialog.dismiss());
        if (!isFinishing()) dialog.show();
    }
}


