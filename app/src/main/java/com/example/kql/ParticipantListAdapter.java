
package com.example.kql;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ParticipantListAdapter extends BaseAdapter {
    Context context;
    List<ParticipantModel> list;

    public ParticipantListAdapter(Context context, List<ParticipantModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.participant_item, parent, false);

        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView role = view.findViewById(R.id.role);

        ParticipantModel item = list.get(position);
        name.setText(item.name);
        email.setText(item.email);
        role.setText(item.role);

        int roleColor;
        switch (item.role.toLowerCase()) {
            case "president":
                roleColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "secretary":
                roleColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                break;
            case "teacher":
                roleColor = context.getResources().getColor(android.R.color.holo_purple);
                break;
            case "member":
                roleColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "admin":
                roleColor = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                roleColor = context.getResources().getColor(android.R.color.darker_gray);
                break;
        }

        role.setBackgroundTintList(ColorStateList.valueOf(roleColor));

        return view;
    }
}
