package com.example.kql;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;



public class MemberList extends AppCompatActivity {

    ListView memberListView;
    FirebaseFirestore fb = FirebaseFirestore.getInstance();
    MemberAdapter adapter;
    List<Member> memberList = new ArrayList<>();
    TextView update;
    CheckBox cbPresident, cbSecretary, cbAdmin, cbTeacher, cbMember, cbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list); // make sure this matches your XML filename

        memberListView = findViewById(R.id.member_list);
        update = findViewById(R.id.update);
        cbPresident = findViewById(R.id.cb_president);
        cbSecretary = findViewById(R.id.cb_secretary);
        cbAdmin = findViewById(R.id.cb_admin);
        cbTeacher = findViewById(R.id.cb_teacher);
        cbMember = findViewById(R.id.cb_member);
        cbAll = findViewById(R.id.all);

        adapter = new MemberAdapter(this, memberList);
        memberListView.setAdapter(adapter);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberList.clear();
                fb.collection("Member_Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            if(cbAll.isChecked())
                            {
                                memberList.add(new Member(documentSnapshot.getString("Name"), documentSnapshot.getString("Email"), documentSnapshot.getString("Role")));
                            }
                            else {
                                if(cbPresident.isChecked())
                                {
                                    if(documentSnapshot.getString("Role").equals("President"))
                                    {
                                        memberList.add(new Member(documentSnapshot.getString("Name"), documentSnapshot.getString("Email"), documentSnapshot.getString("Role")));

                                    }
                                }
                                if(cbMember.isChecked())
                                {
                                    if(documentSnapshot.getString("Role").equals("Member"))
                                    {
                                        memberList.add(new Member(documentSnapshot.getString("Name"), documentSnapshot.getString("Email"), documentSnapshot.getString("Role")));

                                    }
                                }
                                if(cbAdmin.isChecked())
                                {
                                    if(documentSnapshot.getString("Role").equals("Admin"))
                                    {
                                        memberList.add(new Member(documentSnapshot.getString("Name"), documentSnapshot.getString("Email"), documentSnapshot.getString("Role")));

                                    }
                                }
                                if(cbTeacher.isChecked())
                                {
                                    if(documentSnapshot.getString("Role").equals("Teacher"))
                                    {
                                        memberList.add(new Member(documentSnapshot.getString("Name"), documentSnapshot.getString("Email"), documentSnapshot.getString("Role")));

                                    }
                                }
                                if(cbSecretary.isChecked())
                                {
                                    if(documentSnapshot.getString("Role").equals("Secretary"))
                                    {
                                        memberList.add(new Member(documentSnapshot.getString("Name"), documentSnapshot.getString("Email"), documentSnapshot.getString("Role")));

                                    }
                                }
                            }


                        }
                        adapter.notifyDataSetChanged();

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
