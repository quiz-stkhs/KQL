



package com.example.kql;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.core.content.ContextCompat;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class JoinQuiz extends AppCompatActivity {
    Button qzrmjoin, srchroom;
    TextView codeview;
    EditText rcode;
    ProgressDialog pb;

    public static final String INSTR = "inst";
    public static final String TIME = "quiz_time";

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    SharedPreferences spEmail;
    String userEmail;
    boolean isQuizAvailable = false;
    String searchedRoomCode = "";
    Boolean isStarted = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_quiz);

        pb = new ProgressDialog(this);
        pb.setTitle("Room Code");
        pb.setMessage("Processing...");

        qzrmjoin = findViewById(R.id.qzrmjoin);
        codeview = findViewById(R.id.codeview);
        srchroom = findViewById(R.id.srchroom);
        rcode = findViewById(R.id.rcode);

        spEmail = getSharedPreferences("Email", Context.MODE_PRIVATE);
        userEmail = spEmail.getString("email", null);
        qzrmjoin.setVisibility(View.GONE);

        qzrmjoin.setOnClickListener(v -> {
            String room = rcode.getText().toString().trim();

            if (room.isEmpty()) {
                Toast.makeText(this, "Please enter a Room Code first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isQuizAvailable || !room.equals(searchedRoomCode)) {
                Toast.makeText(this, "Please search and validate the Room Code before joining", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!isStarted)
            {
                Toast.makeText(this, "Quiz is not Started Yet", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("Room", room);
            startActivity(new Intent(JoinQuiz.this, instructions2.class).putExtras(bundle));
        });

        srchroom.setOnClickListener(v -> fetchQuizDetails());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void fetchQuizDetails() {
        String room = rcode.getText().toString().trim();
        isQuizAvailable = false;
        searchedRoomCode = "";
        pb.show();

        if (room.isEmpty()) {
            Toast.makeText(this, "Empty Room Code", Toast.LENGTH_SHORT).show();
            pb.dismiss();
            return;
        }

        fs.collection("Rooms_Requests").document(room).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && "Approved".equals(documentSnapshot.getString("Status"))) {
                String quizName = documentSnapshot.getString("Quiz Name");
                String owner = documentSnapshot.getString("Owner");
                isStarted = documentSnapshot.getBoolean("isStarted");
                String instructions = documentSnapshot.getString("Instructions");

                codeview.setText("\n" + owner + "\n\nQuiz Name: " + quizName);
                codeview.setTextColor(getResources().getColor(R.color.blue));

                SharedPreferences sp = getSharedPreferences(INSTR, Context.MODE_PRIVATE);
                SharedPreferences.Editor spe = sp.edit();
                spe.putString("Code", room);
                spe.putString("inst", instructions);
                spe.apply();

                qzrmjoin.setVisibility(View.VISIBLE);

                isQuizAvailable = true;
                searchedRoomCode = room;
                pb.dismiss();
            } else {
                codeview.setText("!! Quiz Room not found or not online !!!");
                codeview.setTextColor(getResources().getColor(R.color.red));
                pb.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error while fetching room!", Toast.LENGTH_SHORT).show();
            pb.dismiss();
        });
    }

    public void checkAlreadyAttempted(String roomCode) {
        pb.show();

        fs.collection("Rooms").document(roomCode)
                .collection("Answers").document(userEmail)
                .get()
                .addOnSuccessListener(doc -> {
                    pb.dismiss();
                    if (doc.exists()) {
                        startQuiz(roomCode);
                    } else {
                        startQuiz(roomCode);
                    }
                })
                .addOnFailureListener(e -> {
                    pb.dismiss();
                    Toast.makeText(this, "Failed to check attempt status!", Toast.LENGTH_SHORT).show();
                });
    }

    private void startQuiz(String roomCode) {
//        SharedPreferences.Editor timer = getSharedPreferences(TIME, MODE_PRIVATE).edit();
//        timer.putString("start", String.valueOf(System.currentTimeMillis()));
//        timer.apply();
//
//        Intent intent = new Intent(this, Participant_Instr.class);
//        startActivity(intent);
    }

//    private void showReviewDialog(String roomCode) {
//        new AlertDialog.Builder(this)
//                .setTitle("Already Attempted")
//                .setMessage("You have already attempted this quiz.\nDo you want to review your answers?")
//                .setPositiveButton("Review Answers", (dialog, which) -> {
//                    Intent reviewIntent = new Intent(this, ReviewAnswers.class);
//                    reviewIntent.putExtra("RoomCode", roomCode);
//                    reviewIntent.putExtra("email", userEmail);
//                    startActivity(reviewIntent);
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
}
