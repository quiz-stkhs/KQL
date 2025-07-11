package com.example.kql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    TextView signup, fpass;
    Button login1;
    EditText mail, pass;
    ProgressDialog pb;
    public static final String Email = "Email";
    public static final int Version = 1;
    String role;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fb = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        login1 = findViewById(R.id.login);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        pb = new ProgressDialog(Login.this);
        pb.setTitle("Login");
        pb.setMessage("Login in Progress");
        role = new String();

        if(fb.getCurrentUser()!=null)
        {
            pb.show();
        }

        db.collection("App_Details").document("Version").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot2) {
                if(Integer.parseInt(snapshot2.getString("Version"))>Version)
                {

                    startActivity(new Intent(Login.this, Update.class));
                    finish();
                }
                else {
                    if (fb.getCurrentUser() != null) {
                        pb.show(); // Show loading dialog

                        db.collection("Member_Data")
                                .document(fb.getCurrentUser().getEmail())
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    pb.dismiss(); // Hide loading dialog
                                    role = snapshot.getString("Role");

                                    if (role.equals("President") || role.equals("Teacher")) {
                                        startActivity(new Intent(Login.this, Home_President.class));
                                    } else if (role.equals("Secretary") || role.equals("Admin")) {
                                        startActivity(new Intent(Login.this, Home_Secretary.class));
                                    } else {
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    }

                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    pb.dismiss();
                                    Toast.makeText(Login.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                                });
                    }

                    login1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pb.show();
                            if (mail.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                                pb.cancel();
                                Toast.makeText(Login.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
                            } else {
                                fb.signInWithEmailAndPassword(
                                                mail.getText().toString().trim(),
                                                pass.getText().toString().trim()
                                        ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                db.collection("Member_Data")
                                                        .document(mail.getText().toString())
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                                role = snapshot.getString("Role");
                                                                if (role.equals("President") || role.equals("Teacher")) {
                                                                    startActivity(new Intent(Login.this, Home_President.class));
                                                                } else if (role.equals("Secretary") || role.equals("Admin")) {
                                                                    startActivity(new Intent(Login.this, Home_Secretary.class));
                                                                } else {
                                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                                }
                                                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Login.this, "Failed " + e, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                pb.cancel();
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
        // Check if user is already logged in


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
