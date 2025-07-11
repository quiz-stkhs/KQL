package com.example.kql;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CheckHostRequests extends AppCompatActivity {
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
        setContentView(R.layout.activity_check_host_requests);
        listView = findViewById(R.id.listt);
        quizRequestList = new ArrayList<>();
        quizRequestAdapter = new QuizRequestAdapter(CheckHostRequests.this, quizRequestList);
        listView.setAdapter(quizRequestAdapter);
        firestore.collection("Rooms_Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                quizRequestList.clear();

                for (int i = 0; i < value.size(); i++) {
                    if(value.getDocuments().get(i).getString("Status").equals("Approval Requested"))
                    {
                        quizRequestList.add(new QuizRequest(value.getDocuments().get(i).getString("Quiz Name"), value.getDocuments().get(i).getString("Date"), value.getDocuments().get(i).getString("Owner"), value.getDocuments().get(i).getString("Owner Name"), value.getDocuments().get(i).getString("Time"), value.getDocuments().get(i).getString("Room Code"), value.getDocuments().get(i).getString("Status")));

                    }
                }
                if(quizRequestList.isEmpty())
                {
                    Toast.makeText(CheckHostRequests.this, "No Active Quiz Requests", Toast.LENGTH_SHORT).show();
                }
                quizRequestAdapter.notifyDataSetChanged();
            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CheckHostRequests.this, ""+position, Toast.LENGTH_SHORT).show();
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
        Dialog dialog = new Dialog(CheckHostRequests.this);
        dialog.setContentView(R.layout.dialog1);
        EditText date = dialog.findViewById(R.id.date);
        pb = new ProgressDialog(this);
        pb.setTitle("Please Wait");
        pb.setMessage("Approval in Progress");
        EditText Time = dialog.findViewById(R.id.time);
        EditText nm = dialog.findViewById(R.id.nm);
        EditText Qnm = dialog.findViewById(R.id.Qnm);
        date.setText(quizRequestList.get(position).date);
        Time.setText(quizRequestList.get(position).time);
        nm.setText(quizRequestList.get(position).name);
        Qnm.setText(quizRequestList.get(position).quizName);
        TextView Approve = dialog.findViewById(R.id.btnApprove);
        TextView Edit = dialog.findViewById(R.id.btnEdit);

        Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.show();
                ApproveQuiz(position);
                dialog.dismiss();
            }
        });
        if(!isFinishing())
        {
            dialog.show();
        }

    }

    private void ApproveQuiz(int position) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status","Approved");
        Toast.makeText(this, ""+quizRequestList.get(position).roomcode, Toast.LENGTH_SHORT).show();
        firestore.collection("Rooms_Requests").document(quizRequestList.get(position).roomcode).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckHostRequests.this, "Quiz Request Approved Successfully", Toast.LENGTH_SHORT).show();
                pb.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckHostRequests.this, "Failed", Toast.LENGTH_SHORT).show();
                pb.dismiss();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pb.dismiss();
            }
        });

    }
}