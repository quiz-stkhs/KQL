package com.example.kql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth fb = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView txtRole, txtMail, txtPhone, txtNm, mainNm;
    NavigationView navigationView;
    String role, nm;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_president);
        role = new String();
        nm = new String();
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        txtMail = header.findViewById(R.id.email);
        txtPhone = header.findViewById(R.id.phone);
        txtRole = header.findViewById(R.id.role);
        txtNm = header.findViewById(R.id.name);
        mainNm = findViewById(R.id.name);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar); // set toolbar as ActionBar

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        db.collection("Member_Data").document(fb.getCurrentUser().getEmail().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                role = snapshot.getString("Role");
                txtRole.setText(role);
                txtMail.setText(snapshot.getString("Email"));
                txtPhone.setText(snapshot.getString("Mobile"));
                txtNm.setText(snapshot.getString("Name"));
                mainNm.setText(snapshot.getString("Name"));

            }
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            // Handle different menu items here
            if(id == R.id.logout)
            {
                fb.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }else if (id == R.id.nav_host_live_quiz) {
                startActivity(new Intent(MainActivity.this, LiveQuiz.class));

            }
            else if(id == R.id.nav_leaderboard)
            {
                startActivity(new Intent(MainActivity.this, LeaderBoard.class));
            }
            else if(id ==R.id.nav_check_quiz)
            {
                Bundle bundle = new Bundle();
                bundle.putString("Start", "False");
                startActivity(new Intent(MainActivity.this, UpcmingLiveQuiz.class).putExtras(bundle));
            }
            drawer.closeDrawers();
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}