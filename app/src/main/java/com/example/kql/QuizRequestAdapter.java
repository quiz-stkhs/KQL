package com.example.kql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class QuizRequestAdapter extends ArrayAdapter<QuizRequest> {
    private Context context;
    private List<QuizRequest> quizList;

    public QuizRequestAdapter(Context context, List<QuizRequest> quizList) {
        super(context, 0, quizList);
        this.context = context;
        this.quizList = quizList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuizRequest quiz = quizList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.quizrequestslist, parent, false);
        }

        // Bind views
        TextView tvName = convertView.findViewById(R.id.Pnm);
        TextView tvMail = convertView.findViewById(R.id.Pmail);
        TextView tvQuizName = convertView.findViewById(R.id.nm);
        TextView tvRoomCode = convertView.findViewById(R.id.roomCode);
        TextView tvDate = convertView.findViewById(R.id.date);
        TextView tvTime = convertView.findViewById(R.id.time);

        // Set values
        tvName.setText(quiz.name);
        tvMail.setText(quiz.mail);
        tvQuizName.setText(quiz.quizName);
        tvRoomCode.setText(quiz.roomcode);
        tvDate.setText(quiz.date);
        tvTime.setText(quiz.time);

        return convertView;
    }
}
