package com.example.kql;


import static com.example.kql.CreateQuiz.RoomRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class instructions extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please Enter Instructions For Your quiz", Toast.LENGTH_SHORT).show();
    }
    Button publishquiz,savequiz;
    SharedPreferences sp;
    EditText instr;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions);
        sp = getSharedPreferences(RoomRequest, MODE_PRIVATE);
        savequiz = findViewById(R.id.savequiz);
        instr = findViewById(R.id.instr);


        publishquiz = findViewById(R.id.publishquiz);
        savequiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Instructions", instr.getText().toString());
                hashMap.put("Status","Offline");
                Toast.makeText(instructions.this, ""+sp, Toast.LENGTH_SHORT).show();
                fs.collection("Rooms").document(sp.getString("Code", "0")).update(hashMap);
                String role = sp.getString("Role", "Member");
                if(role.equals("President")||role.equals("Teacher"))
                {
                    startActivity(new Intent(instructions.this, Home_President.class));

                }
                else if(role.equals("Secretary")||role.equals("Admin"))
                {
                    startActivity(new Intent(instructions.this, Home_Secretary.class));
                }
                else {
                    startActivity(new Intent(instructions.this, MainActivity.class));
                }
                Toast.makeText(instructions.this, "Quiz Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        publishquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Instructions", instr.getText().toString());
                hashMap.put("Status","Approval Requested");
                fs.collection("Rooms_Requests").document(sp.getString("Code", "0")).update(hashMap);
                String role = sp.getString("Role", "Member");
                if(role.equals("President")||role.equals("Teacher"))
                {
                    startActivity(new Intent(instructions.this, Home_President.class));

                }
                else if(role.equals("Secretary")||role.equals("Admin"))
                {
                    startActivity(new Intent(instructions.this, Home_Secretary.class));
                }
                else {
                    startActivity(new Intent(instructions.this, MainActivity.class));
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}