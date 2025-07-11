package com.example.kql;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class AnsAdapter extends BaseAdapter {
FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Context context;
    private List<Answer> answerList;

    public AnsAdapter(Context context, List<Answer> answerList) {
        this.context = context;
        this.answerList = answerList;
    }

    @Override
    public int getCount() {
        return answerList.size();
    }

    @Override
    public Object getItem(int position) {
        return answerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView nm, email, role, ans;
        Button Correct, Otrt, Incorrect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ans_list, parent, false);
            holder = new ViewHolder();
            holder.nm = convertView.findViewById(R.id.nm);
            holder.email = convertView.findViewById(R.id.email);
            holder.role = convertView.findViewById(R.id.role);
            holder.ans = convertView.findViewById(R.id.ans);
            holder.Correct = convertView.findViewById(R.id.btnEdit);
            holder.Otrt = convertView.findViewById(R.id.delete);
            holder.Incorrect = convertView.findViewById(R.id.btnReport);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Answer item = answerList.get(position);
        holder.nm.setText(item.name);
        holder.email.setText(item.email);
        holder.role.setText(item.role);
        holder.ans.setText(item.ans);

        // Button Click Listeners (Customize actions as needed)
        holder.Correct.setOnClickListener(v -> {
            HashMap<String, Object> hashMap2 = new HashMap<>();
            firestore.collection("Rooms_Requests").document(item.rc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    String email = item.email.replace(".", "_");


                    if(snapshot.contains(email))
                    {
                        hashMap2.put(email, ""+(Integer.parseInt(snapshot.getString(email))+1));
                        firestore.collection("Rooms_Requests").document(item.rc).update(hashMap2);
                    }
                    else {
                        hashMap2.put(email, "1");
                        firestore.collection("Rooms_Requests").document(item.rc).update(hashMap2);
                    }
                }
            });
            HashMap<String, Object> hashMap;
            hashMap = new HashMap<>();
            hashMap.put("AnsweredBy", item.email);
            firestore.collection("Rooms_Requests").document(item.rc).collection("AnswersGivenBy").document(item.email).set(hashMap);
            firestore.collection("Rooms_Requests").document(item.rc).collection("Questions").document(item.qno).update(hashMap);
            HashMap<String, Object> hashMap1;
            hashMap1 = new HashMap<>();
            hashMap1.put("SeenByHost", true);
            hashMap1.put("Status", "Correct");
            firestore.collection("Rooms_Requests").document(item.rc).collection("Questions").document(item.qno).collection("Answers").document(item.email).update(hashMap1);
        });

        holder.Incorrect.setOnClickListener(v -> {

            HashMap<String, Object> hashMap;
            hashMap = new HashMap<>();
            hashMap.put("SeenByHost", true);
            hashMap.put("Status", "Incorrect");
            firestore.collection("Rooms_Requests").document(item.rc).collection("Questions").document(item.qno).collection("Answers").document(item.email).update(hashMap);
        });

        holder.Otrt.setOnClickListener(v -> {
            HashMap<String, Object> hashMap;
            hashMap = new HashMap<>();
            hashMap.put("Status", "Otrt");
            hashMap.put("SeenByHost", true);
            firestore.collection("Rooms_Requests").document(item.rc).collection("Questions").document(item.qno).collection("Answers").document(item.email).update(hashMap);

        });

        return convertView;
    }
}
