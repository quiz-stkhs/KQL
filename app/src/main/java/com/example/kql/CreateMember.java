package com.example.kql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateMember extends AppCompatActivity {
    EditText nm, email, mob, pass, cPass;
    Button reg;
    EditText imgUpload;

    ImageView img;

    TextView log;
    ProgressDialog pb;
    Spinner spin;
    FirebaseAuth fb = FirebaseAuth.getInstance();
    ActivityResultLauncher<Intent> cameraLauncher;
    FirebaseFirestore data = FirebaseFirestore.getInstance().collection("Member_Data").getFirestore();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_member);
        log = findViewById(R.id.log);
        nm = findViewById(R.id.edNm);
        pb = new ProgressDialog(CreateMember.this);
        pb.setTitle("Sign Up");
        pb.setMessage("Sign Up In Progress");
        email = findViewById(R.id.email);
        spin = findViewById(R.id.spnChooseRole);
        mob = findViewById(R.id.mob);
        pass = findViewById(R.id.pass);
        cPass = findViewById(R.id.cPass);
        reg = findViewById(R.id.reg);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(CreateMember.this, R.array.Choose_Role, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spin1);
        spin.setAdapter(arrayAdapter);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateMember.this,Home_President.class));
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.show();
                String name = nm.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String mobile = mob.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String confirmPassword = cPass.getText().toString().trim();
                String role = spin.getSelectedItem().toString();

                // Check for empty fields
                if (name.isEmpty() || userEmail.isEmpty() || mobile.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() ||role.equals("Choose Role")) {
                    // Show error
                    Toast.makeText(CreateMember.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    pb.cancel();
                }
                else if(password.equals(confirmPassword))
                {
                    fb.createUserWithEmailAndPassword(userEmail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("Name", name);
                            hashMap.put("Email", userEmail);
                            hashMap.put("Mobile", mobile);
                            hashMap.put("Password", password);
                            hashMap.put("Role", role);
                            data.collection("Member_Data").document(userEmail).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(CreateMember.this, MainActivity.class));
                                    finish();
                                    Toast.makeText(CreateMember.this, "Member Added Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateMember.this, ""+e, Toast.LENGTH_SHORT).show();
                                    pb.cancel();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pb.cancel();
                                    Toast.makeText(CreateMember.this, "Member Admission Failed "+ e, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pb.cancel();
                                }
                            });
                        }
                    });
                }
                else {
                    pb.cancel();
                    // Proceed with registration logic
                    // (e.g., validate email format, check if passwords match, etc.)
                    Toast.makeText(CreateMember.this, "Password do not Match", Toast.LENGTH_SHORT).show();




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