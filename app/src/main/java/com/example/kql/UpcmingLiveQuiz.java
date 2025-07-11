package com.example.kql;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UpcmingLiveQuiz extends AppCompatActivity {
    ListView listView;
    QuizRequestAdapter quizRequestAdapter;
    List<QuizRequest> quizRequestList;
    ProgressDialog pb;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upcoming_live_quiz);
        listView = findViewById(R.id.listt);
        quizRequestList = new ArrayList<>();
        quizRequestAdapter = new QuizRequestAdapter(UpcmingLiveQuiz.this, quizRequestList);
        listView.setAdapter(quizRequestAdapter);

        firestore.collection("Rooms_Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                quizRequestList.clear();
                long currentTime = System.currentTimeMillis();

                for (DocumentSnapshot doc : value.getDocuments()) {
                    String status = doc.getString("Status");
                    String quizDate = doc.getString("Date");
                    String quizTime = doc.getString("Time");
                    String quizName = doc.getString("Quiz Name");
                    String owner = doc.getString("Owner");
                    String ownerName = doc.getString("Owner Name");
                    String roomCode = doc.getString("Room Code");



                    if (getIntent().getExtras().getString("Start").equals("False")) {
                        if ("Approved".equals(status)) {
                            String datetime = quizDate.replaceAll(" ", "") + " " + quizTime;
                            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy HH:mm", Locale.getDefault());

                            try {
                                Date quizDateTime = sdf.parse(datetime);
                                if (quizDateTime == null || quizDateTime.getTime() <= currentTime) {
                                    continue; // Skip quizzes in the past
                                }
                            } catch (ParseException e) {
                                continue; // Skip if date is invalid
                            }
                            quizRequestList.add(new QuizRequest(quizName, quizDate, owner, ownerName, quizTime, roomCode, status));
                        }
                    } else {
                        if (owner.equals(firebaseAuth.getCurrentUser().getEmail())) {
                            quizRequestList.add(new QuizRequest(quizName, quizDate, owner, ownerName, quizTime, roomCode, status));
                        }
                    }
                }

                if (quizRequestList.isEmpty()) {
                    Toast.makeText(UpcmingLiveQuiz.this, "No Upcoming Quizzes Found", Toast.LENGTH_SHORT).show();
                }

                quizRequestAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog(position);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void dialog(int position) {
        Dialog dialog = new Dialog(UpcmingLiveQuiz.this);
        dialog.setContentView(R.layout.dialog2);
        dialog.setCancelable(false);

        EditText date = dialog.findViewById(R.id.date);
        pb = new ProgressDialog(this);
        pb.setTitle("Please Wait");
        pb.setMessage("Approval in Progress");
        EditText Time = dialog.findViewById(R.id.time);
        EditText nm = dialog.findViewById(R.id.nm);
        EditText Qnm = dialog.findViewById(R.id.Qnm);
        EditText status = dialog.findViewById(R.id.status);
        date.setText(quizRequestList.get(position).date);
        Time.setText(quizRequestList.get(position).time);
        nm.setText(quizRequestList.get(position).name);
        Qnm.setText(quizRequestList.get(position).quizName);
        LinearLayout vanish6 = dialog.findViewById(R.id.vanish6);
        vanish6.setVisibility(GONE);
        TextView Approve = dialog.findViewById(R.id.btnApprove);
        TextView Edit = dialog.findViewById(R.id.btnEdit);
        status.setVisibility(GONE);

        if (!getIntent().getExtras().getString("Start").equals("False")) {
            Approve.setText("Start Quiz");
            status.setVisibility(VISIBLE);
            vanish6.setVisibility(VISIBLE);
            status.setText(quizRequestList.get(position).status);
        }

        if (quizRequestList.get(position).mail.equals(firebaseAuth.getCurrentUser().getEmail())) {
            Approve.setText("Start Quiz");
            status.setVisibility(VISIBLE);
            vanish6.setVisibility(VISIBLE);
            status.setText(quizRequestList.get(position).status);
        }

        Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] dateStr = {quizRequestList.get(position).date.trim()};
                final int[] loop = {0};

                if (Approve.getText().toString().equals("Start Quiz")) {
                    String timeStr = quizRequestList.get(position).time.trim();
                    firestore.collection("Rooms_Requests").document(quizRequestList.get(position).roomcode).get().addOnSuccessListener(snapshot -> {
                        if (!snapshot.getString("Status").equals("Approved")) {
                            Toast.makeText(UpcmingLiveQuiz.this, "Quiz is Not Approved Yet!", Toast.LENGTH_SHORT).show();
                            loop[0] = 1;
                        }
                    }).addOnSuccessListener(snapshot -> {
                        if (loop[0] == 1) return;

                        dateStr[0] = dateStr[0].replaceAll(" ", "");
                        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy HH:mm", Locale.getDefault());

                        try {
                            Date quizDateTime = sdf.parse(dateStr[0] + " " + timeStr);
                            long currentTimeMillis = System.currentTimeMillis();
                            long quizTimeMillis = quizDateTime.getTime();
                            long diff = currentTimeMillis - quizTimeMillis;

                            if (diff >= -5 * 60 * 1000 && diff <= 5 * 60 * 1000) {
                                HashMap<String, Object> hashMap1 = new HashMap<>();
                                hashMap1.put("isStarted", true);
                                firestore.collection("Rooms_Requests").document(quizRequestList.get(position).roomcode).update(hashMap1);
                                Toast.makeText(UpcmingLiveQuiz.this, "Starting Quiz...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpcmingLiveQuiz.this, ParticipantList.class);
                                intent.putExtra("Room", quizRequestList.get(position).roomcode);
                                startActivity(intent);
                            } else {
                                Toast.makeText(UpcmingLiveQuiz.this, "Quiz can be started at the given time only.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(UpcmingLiveQuiz.this, "Error parsing date/time", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    pb.show();
                    dialog2(position);
                    dialog.dismiss();
                }
            }
        });

        Edit.setOnClickListener(v -> dialog.dismiss());

        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void dialog2(int position) {
        Dialog dialog = new Dialog(UpcmingLiveQuiz.this);
        dialog.setContentView(R.layout.dialog5);
        dialog.setCancelable(false);

        pb = new ProgressDialog(this);
        pb.setTitle("Please Wait");
        pb.setMessage("Application in Progress");

        EditText Qnm = dialog.findViewById(R.id.Qnm);
        TextView Approve = dialog.findViewById(R.id.btnApprove);
        TextView Edit = dialog.findViewById(R.id.btnEdit);

        Approve.setOnClickListener(v -> {
            if (Qnm.getText().toString().isEmpty()) {
                Toast.makeText(UpcmingLiveQuiz.this, "Please Fill In the Reason for Leave", Toast.LENGTH_SHORT).show();
            } else {
                pb.show();
                ApproveQuiz(position, Qnm.getText().toString());
                dialog.dismiss();
                pb.dismiss();
            }
        });

        Edit.setOnClickListener(v -> dialog.dismiss());

        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void ApproveQuiz(int position, String reason) {
        if (getIntent().getExtras().getString("Start").equals("False")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Reason", reason);
            hashMap.put("Status", "Leave Requested");
            hashMap.put("Date", quizRequestList.get(position).date);
            hashMap.put("Time", quizRequestList.get(position).time);
            hashMap.put("Quiz_Name", quizRequestList.get(position).quizName);
            hashMap.put("Host", quizRequestList.get(position).name);

            firestore.collection("Leave_Requests")
                    .document(firebaseAuth.getCurrentUser().getEmail())
                    .collection(quizRequestList.get(position).roomcode)
                    .document(quizRequestList.get(position).roomcode)
                    .set(hashMap)
                    .addOnSuccessListener(unused -> Toast.makeText(UpcmingLiveQuiz.this, "Applied for Leave Successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(UpcmingLiveQuiz.this, "Failed " + e, Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> pb.dismiss());
        }
    }
}
