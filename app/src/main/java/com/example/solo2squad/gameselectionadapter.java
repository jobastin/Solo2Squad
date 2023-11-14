package com.example.solo2squad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class gameselectionadapter extends BaseAdapter {
    private Context context;
    private List<event> eventList;

    public gameselectionadapter(Context context, List<event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gameselectionlist, parent, false);
        }

        event event = eventList.get(position);

        TextView gameTypeTextView = convertView.findViewById(R.id.gameTypeTextView);
        TextView slotsTextView = convertView.findViewById(R.id.slotsTextView);
        Button actionButton = convertView.findViewById(R.id.actionButton);

        gameTypeTextView.setText("Game Type: " + event.getGameType());
        slotsTextView.setText("Slots: " + event.getSlots());

        // Handle button click here
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                // You can add specific actions for the button here
            }
        });

        return convertView;
    }
}
