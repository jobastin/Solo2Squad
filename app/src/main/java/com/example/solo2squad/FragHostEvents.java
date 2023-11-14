package com.example.solo2squad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragHostEvents extends Fragment {
    private Spinner gameTypeSpinner;
    private EditText locationEditText, dateAndTimeEditText, slotsEditText, priceEditText, descriptionEditText;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_host_event, container, false);
        gameTypeSpinner = view.findViewById(R.id.gameTypeSpinner);
        locationEditText = view.findViewById(R.id.locationEditText);
        dateAndTimeEditText = view.findViewById(R.id.dateAndTimeEditText);
        slotsEditText = view.findViewById(R.id.slotsEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        Button submitButton = view.findViewById(R.id.submitButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("sports_type");

        // Fetch data from Firebase and populate the spinner
        fetchDataFromFirebase();

        // Handle the submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String gameType = gameTypeSpinner.getSelectedItem().toString();
                String location = locationEditText.getText().toString();
                String dateAndTime = dateAndTimeEditText.getText().toString();
                int slots = Integer.parseInt(slotsEditText.getText().toString());
                double pricePerPerson = Double.parseDouble(priceEditText.getText().toString());
                String description = descriptionEditText.getText().toString();

                // Create an Event object
                event event = new event(gameType, location, dateAndTime, slots, pricePerPerson, description);

                // Update the Firebase database
                updateFirebaseDatabase(event);

                showToast("Event added successfully");
                clearInputFields();

            }
        });

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> sportsNames = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String sportsName = snapshot.child("sports_name").getValue(String.class);
                    if (sportsName != null) {
                        sportsNames.add(sportsName);
                    }
                }
                // Populate spinner with retrieved data
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sportsNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                gameTypeSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void updateFirebaseDatabase(event event) {
        // Push the event to the "events" node in the database
        databaseReference.push().setValue(event);
        // You can also use databaseReference.child("specific_child").setValue(event) if needed
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearInputFields() {
        locationEditText.setText("");
        dateAndTimeEditText.setText("");
        slotsEditText.setText("");
        priceEditText.setText("");
        descriptionEditText.setText("");
    }
}
