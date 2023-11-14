package com.example.solo2squad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragGameSelection extends Fragment {
    private ListView gamesListView;
    private gameselectionadapter adapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_game_selection, container, false);

        gamesListView = view.findViewById(R.id.gamesListView);
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        // Fetch data from Firebase and populate the ListView
        fetchDataFromFirebase();

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<event> eventList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming you have an "event" class with appropriate constructor
                    event event = snapshot.getValue(event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }

                // Create the custom adapter
                adapter = new gameselectionadapter(requireContext(), eventList);

                // Set the adapter to the ListView
                gamesListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
