package com.example.solo2squad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragManageEvents extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    private ListView manageeventListView;
    private ManageEventsAdapter adapter;
    private DatabaseReference databaseReference;

    private String currentUserID = currentUser.getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_manage_events, container, false);

        manageeventListView = view.findViewById(R.id.manageevents);
        databaseReference = FirebaseDatabase.getInstance().getReference("Hosted_events");

        // Fetch data from Firebase and populate the ListView
        fetchDataFromFirebase();

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<event> eventList = new ArrayList<>();
                List<String> eventKeys = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming you have an "event" class with appropriate constructor
                    String key = snapshot.getKey();
                    event event = snapshot.getValue(event.class);
                    if (event != null && event.getUserID().equals(currentUserID)) {
                        eventList.add(event);
                        eventKeys.add(key);
                    }
                }

                // Create the custom adapter
                adapter = new ManageEventsAdapter(requireContext(), eventList, eventKeys,requireActivity().getSupportFragmentManager());

                // Set the adapter to the ListView
                manageeventListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
