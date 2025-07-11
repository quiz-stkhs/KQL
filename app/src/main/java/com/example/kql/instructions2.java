package com.example.kql;


import static android.view.View.GONE;
import static com.example.kql.CreateQuiz.RoomRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class instructions2 extends AppCompatActivity {


    Button publishquiz,savequiz;
    SharedPreferences sp;
    EditText instr;
    TextView qnm2;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions);
        sp = getSharedPreferences(RoomRequest, MODE_PRIVATE);
        savequiz = findViewById(R.id.savequiz);
        instr = findViewById(R.id.instr);
        publishquiz = findViewById(R.id.publishquiz);
        publishquiz.setVisibility(GONE);
        savequiz.setVisibility(GONE);
        instr.setEnabled(false);
         qnm2 = findViewById(R.id.qnm1);
        // Fetch and set the quiz name from Firestore
        fs.collection("Rooms_Requests").document(getIntent().getExtras().getString("Room"))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String quizName = documentSnapshot.getString("Quiz Name") +" (" + documentSnapshot.getString("Room Code") + ")"; // Pay attention to the exact key name
                        if (quizName != null && !quizName.isEmpty()) {
                            qnm2.setText(quizName);
                        } else {
                            qnm2.setText("Quiz");
                        }
                    } else {
                        qnm2.setText("Quiz");
                    }
                })
                .addOnFailureListener(e -> {
                    qnm2.setText("Quiz");
                    Toast.makeText(instructions2.this, "Failed to load quiz name", Toast.LENGTH_SHORT).show();
                });

        fs.collection("Rooms_Requests").document(getIntent().getExtras().getString("Room")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                instr.setText(snapshot.getString("Instructions"));
            }
        });
        fs.collection("Member_Data").document(firebaseAuth.getCurrentUser().getEmail().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                HashMap<String, Object> hashMap;
                hashMap = new HashMap<>();
                hashMap.put("Email", firebaseAuth.getCurrentUser().getEmail());
                hashMap.put("Name", snapshot.getString("Name"));
                hashMap.put("Role", snapshot.getString("Role"));
                fs.collection("Rooms_Requests").document(getIntent().getExtras().getString("Room")).collection("Attendance").document(firebaseAuth.getCurrentUser().getEmail()).set(hashMap);
                fs.collection("Rooms_Requests").document(getIntent().getExtras().getString("Room")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.getBoolean("Active"))
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString("Room", getIntent().getExtras().getString("Room"));
                            startActivity(new Intent(instructions2.this, mainQuizJoin.class).putExtras(bundle));
                            finish();

                        }

                    }
                });

            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}