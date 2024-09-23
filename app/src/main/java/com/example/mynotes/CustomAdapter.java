package com.example.mynotes;

import android.content.Context;
import android.graphics.Color;
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

        if (notes.get(position).getPriority() == 1) {
            convertView.setBackgroundColor(Color.RED);
        } else if(notes.get(position).getPriority() == 2) {
            convertView.setBackgroundColor(Color.BLUE);
        } else {
            convertView.setBackgroundColor(Color.YELLOW);
        }

        return convertView;
    }
}
