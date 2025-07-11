package com.example.kql;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class CreateQuiz extends AppCompatActivity {
ImageView imgCal;
CalendarView cal;
    Button nxtquiz;
    TextView Onm;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    EditText nm, code, time;
    public static final String RoomRequest = "MyPrefs";
    int day1, month1, year1;
EditText edCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quiz);
        imgCal = findViewById(R.id.imgCal);
        cal = findViewById(R.id.cal);
        edCal = findViewById(R.id.edCal);
        nxtquiz = findViewById(R.id.nxtquiz);
        nm = findViewById(R.id.nm);
        code = findViewById(R.id.code);
        Onm = findViewById(R.id.Onm);
        time = findViewById(R.id.time);
        cal.setVisibility(GONE);
        fs.collection("Member_Data").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Onm.setText(snapshot.getString("Name"));
                imgCal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cal.setVisibility(VISIBLE);
                        imgCal.setVisibility(GONE);

                    }
                });
                cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                        // Get today's date parts
                        Calendar today = Calendar.getInstance();
                        int todayYear = today.get(Calendar.YEAR);
                        int todayMonth = today.get(Calendar.MONTH); // Note: zero-based
                        int todayDay = today.get(Calendar.DAY_OF_MONTH);

                        // Compare selected date with today
                        if (year > todayYear ||
                                (year == todayYear && month > todayMonth) ||
                                (year == todayYear && month == todayMonth && dayOfMonth > todayDay)) {

                            edCal.setText(dayOfMonth + " / " + (month + 1) + " / " + year);
                            day1 = dayOfMonth;
                            month1 = month + 1;
                            year1 = year;
                            cal.setVisibility(GONE);
                            imgCal.setVisibility(VISIBLE);

                        } else {
                            Toast.makeText(view.getContext(), "Please select a future date", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                nxtquiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(nm.getText().toString().isEmpty()||code.getText().toString().isEmpty() || edCal.getText().toString().isEmpty()||time.getText().toString().isEmpty())
                        {
                            Toast.makeText(CreateQuiz.this, "Please fill complete credentials", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            fs.collection("Rooms_Requests").document(code.getText().toString().trim()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            Toast.makeText(CreateQuiz.this, "Room key already exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Room doesn't exist — create it
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("Owner", firebaseAuth.getCurrentUser().getEmail());
                                            hashMap.put("Quiz Name", nm.getText().toString().trim());
                                            hashMap.put("Date", edCal.getText().toString());
                                            hashMap.put("Time", time.getText().toString());
                                            hashMap.put("Status", "Offline");
                                            hashMap.put("isStarted", false);
                                            hashMap.put("Active", false);
                                            hashMap.put("isFinished", false);
                                            hashMap.put("Owner Name", Onm.getText().toString());
                                            hashMap.put("Room Code", code.getText().toString().trim());
                                            String destination;

                                                destination = "Rooms_Requests";


                                            fs.collection(destination).document(code.getText().toString().trim()).set(hashMap)
                                                    .addOnSuccessListener(unused -> {
                                                        Intent i = new Intent(CreateQuiz.this, UploadQns.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("No", "1");

                                                        SharedPreferences sp = getSharedPreferences(RoomRequest, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor spe = sp.edit();
                                                        spe.putString("Role", snapshot.getString("Role"));
                                                        spe.putString("Destination", destination);
                                                        spe.putString("Code",code.getText().toString().trim() );
                                                        spe.apply();

                                                        i.putExtras(bundle);
                                                        startActivity(i);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(CreateQuiz.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CreateQuiz.this, "Error checking room code", Toast.LENGTH_SHORT).show();
                                    });
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