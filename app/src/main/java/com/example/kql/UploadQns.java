package com.example.kql;

import static android.os.Build.ID;


import static com.example.kql.CreateQuiz.RoomRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class UploadQns extends AppCompatActivity {
    Spinner spin;
    TextView qnm, qno;
    EditText ans;
    int n=1;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    EditText qn;
    Button nxt, finish;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_qns);
        qnm = findViewById(R.id.qnm);
        ans = findViewById(R.id.ans);
        qn = findViewById(R.id.qn);
        nxt = findViewById(R.id.nxt);
        finish = findViewById(R.id.finish);
        qno = findViewById(R.id.qno);
        sp = getSharedPreferences(RoomRequest, MODE_PRIVATE);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qno.getText().toString().equals("1") && (qn.getText().toString().isEmpty() || ans.getText().toString().isEmpty()))
                {
                    Toast.makeText(UploadQns.this, "Please Upload Atleast One Qn", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(qn.getText().toString().isEmpty())
                {
                    startActivity(new Intent(UploadQns.this, instructions.class));
                }
                else{
                    QnUpload();
                    startActivity(new Intent(UploadQns.this, instructions.class));
                }}
        });
        if(getIntent().getExtras()!=null)
        {
            n = Integer.parseInt(getIntent().getExtras().getString("No"));

        }
        else {
            n = 1;
        }
        qno.setText(""+n);

        //Setting Drop Down


        //Setting Quiz Name
//        Toast.makeText(this, ""+sp.getString("Code", "0"), Toast.LENGTH_SHORT).show();
        fs.collection(sp.getString("Destination", "")).document(sp.getString("Code", "0")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                qnm.setText(value.getString("Quiz Name"));
            }
        });
//    spin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(question.this, spin.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//        }
//    });
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qn.getText().toString().isEmpty() || ans.getText().toString().isEmpty())
                {
                    Toast.makeText(UploadQns.this, "Please Fill in All the Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    QnUpload();

                    Intent i = new Intent(UploadQns.this, Buffer1.class);
                    Bundle bundle = new Bundle();
                    int n1 = Integer.parseInt(qno.getText().toString())+1;
                    bundle.putString("No", ""+n1);
                    i.putExtras(bundle);


                    startActivity(i);
                }


            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Your Quiz Is Saved", Toast.LENGTH_SHORT).show();
        finish.performClick();
    }
    public void QnUpload()
    {
        if(qn.getText().toString().isEmpty() || ans.getText().toString().isEmpty())
        {
            Toast.makeText(UploadQns.this, "Please Fill in All the Details", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap <String, String> hashMap = new HashMap<>();
        hashMap.put("Qno", qno.getText().toString());
        hashMap.put("QN", qn.getText().toString());
        hashMap.put("Answer", ans.getText().toString());


//                Toast.makeText(question.this, qno.getText().toString(), Toast.LENGTH_SHORT).show();
        fs.collection(sp.getString("Destination", "")).document(sp.getString("Code", "0")).collection("Questions").document(qno.getText().toString()).set(hashMap);
    }
}