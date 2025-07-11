package com.example.kql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaderBoardAdapter extends ArrayAdapter {
    private Context context;
    private List<Participant> participantList;
    private LayoutInflater inflater;

    public LeaderBoardAdapter(Context context, List<Participant> participantList) {
        super(context, 0, participantList);
        this.context = context;
        this.participantList = participantList;
        this.inflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return participantList.size();
    }

    @Override
    public Object getItem(int position) {
        return participantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView nameText = null;
        TextView scoreText = null;
        TextView rankText = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.results_lis2t, parent, false);

            nameText = convertView.findViewById(R.id.nm);
            scoreText = convertView.findViewById(R.id.score);
            rankText = convertView.findViewById(R.id.rank);
            Participant participant = participantList.get(position);
            nameText.setText(participant.getName());
            scoreText.setText(String.valueOf(participant.getScore()));
            rankText.setText("#"+String.valueOf(participant.getRank())); // ✅ Set rank
        }



        return convertView;
    }
}
