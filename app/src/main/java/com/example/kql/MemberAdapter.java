package com.example.kql;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends ArrayAdapter {
    Context context;
    List<Member> originalList;

    public MemberAdapter(Context context, List<Member> list) {
        super(context, 0, list);
        this.context = context;
        this.originalList = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_list_item, parent, false);

        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView role = view.findViewById(R.id.role);

        Member item = originalList.get(position);
        name.setText(item.getName());
        email.setText(item.getEmail());
        role.setText(item.getRole());

        int roleColor;
        switch (item.getRole().toLowerCase()) {
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
        }

        role.setBackgroundTintList(ColorStateList.valueOf(roleColor));

        return view;
    }




}
