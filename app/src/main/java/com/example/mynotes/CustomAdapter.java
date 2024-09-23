package com.example.mynotes;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Note> {
    private Context context;
    private ArrayList<Note> notes;

    public CustomAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(notes.get(position).getTitle());
        textView.setGravity(Gravity.CENTER);
        if (notes.get(position).getPriority() == 0) {
            convertView.setBackgroundColor(Color.argb(100, 255, 102, 102));
        } else if(notes.get(position).getPriority() == 1) {
            convertView.setBackgroundColor(Color.argb(100, 102, 153, 255));
        } else {
            convertView.setBackgroundColor(Color.argb(100, 255, 255, 153));
        }

        return convertView;
    }
}
