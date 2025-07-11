package com.example.kql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LiveQuiz extends AppCompatActivity {
    Button crtquiz,joinquiz,myquiz;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LiveQuiz.this, Home_President.class));
        finish(); // Optional: prevents returning here via back
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_live_quiz);
        crtquiz = findViewById(R.id.crtquiz);
        joinquiz = findViewById(R.id.joinquiz);
        myquiz = findViewById(R.id.myquiz);
        myquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Start", "Start");
                startActivity(new Intent(LiveQuiz.this, UpcmingLiveQuiz.class).putExtras(bundle));
            }
        });
        joinquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LiveQuiz.this, JoinQuiz.class));
            }
        });
        crtquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LiveQuiz.this,CreateQuiz.class));
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}