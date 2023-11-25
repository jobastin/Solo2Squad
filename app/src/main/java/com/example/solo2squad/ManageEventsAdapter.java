package com.example.solo2squad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.solo2squad.ProfileSection.gameselectiondetails;

import java.util.List;

public class ManageEventsAdapter extends BaseAdapter{

    private Context context;
    private List<event> eventList;
    private List<String> eventKeys;
    private FragmentManager fragmentManager;

    public ManageEventsAdapter(Context context, List<event> eventList,List<String> eventKeys, FragmentManager fragmentManager) {
        this.context = context;
        this.eventList = eventList;
        this.eventKeys = eventKeys;
        this.fragmentManager = fragmentManager;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.manageeventslist, parent, false);
        }

        event event = eventList.get(position);

        TextView gameTypeTextView = convertView.findViewById(R.id.gameTypeTextView);
        TextView slotsTextView = convertView.findViewById(R.id.slotsTextView);
        Button editButton = convertView.findViewById(R.id.editbutton);
        Button deleteButton = convertView.findViewById(R.id.deletebutton);

        gameTypeTextView.setText("Game Type: " + event.getSportsType());
        slotsTextView.setText("Slots: " + event.getSlotsAvailable());

        // Handle button click here
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickedEventKey = eventKeys.get(position);

                Intent intent = new Intent(context, editEvents.class);
                intent.putExtra("eventKey", clickedEventKey);
                context.startActivity(intent);

            }
        });


        deleteButton.setOnClickListener(v -> {
            confirmDelete dialogFragment = new confirmDelete();
            dialogFragment.setEventKey(eventKeys.get(position));
            dialogFragment.show(fragmentManager, "ConfirmDeleteDialog");
        });

        return convertView;
    }
}
