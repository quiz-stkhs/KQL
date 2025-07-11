package com.example.kql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ParticipantAdapter extends BaseAdapter {

    private Context context;
    private List<Participant> participantList;
    private LayoutInflater inflater;

    public ParticipantAdapter(Context context, List<Participant> participantList) {
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

    static class ViewHolder {
        TextView nameText;
        TextView scoreText;
        TextView rankText; // ✅ Added for rank
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.results_list, parent, false);
            holder = new ViewHolder();

            holder.nameText = convertView.findViewById(R.id.nm);
            holder.scoreText = convertView.findViewById(R.id.score);
            holder.rankText = convertView.findViewById(R.id.rank); // ✅ Initialize rank
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Participant participant = participantList.get(position);
        holder.nameText.setText(participant.getName());
        holder.scoreText.setText(String.valueOf(participant.getScore()));
        holder.rankText.setText(String.valueOf(participant.getRank())); // ✅ Set rank

        return convertView;
    }
}
