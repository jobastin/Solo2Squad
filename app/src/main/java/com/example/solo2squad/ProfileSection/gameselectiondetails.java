package com.example.solo2squad.ProfileSection;

import android.os.Bundle;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solo2squad.R;
import com.example.solo2squad.event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class gameselectiondetails extends AppCompatActivity {

    private TextView sportsTypeTextView;
    private TextView sportsCategoryTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView slotsAvailableTextView;
    private TextView pricePerSlotTextView;
    private EditText numberOfSlotsEditText;
    private Button confirmBookingButton;

    private DatabaseReference databaseReference;
    private String uniqueId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamedetails);

        uniqueId = getIntent().getStringExtra("eventKey");

        // Initialize views
        sportsTypeTextView = findViewById(R.id.sportsTypeTextView);
        sportsCategoryTextView = findViewById(R.id.sportsCategoryTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        slotsAvailableTextView = findViewById(R.id.slotsAvailableTextView);
        pricePerSlotTextView = findViewById(R.id.pricePerSlotTextView);
        numberOfSlotsEditText = findViewById(R.id.numberOfSlotsEditText);
        confirmBookingButton = findViewById(R.id.confirmBookingButton);



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

        databaseReference.child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the event object based on the unique ID
                    event eventDetails = dataSnapshot.getValue(event.class);

                    if (eventDetails != null) {
                        // Set values from eventDetails object to TextViews
                        sportsTypeTextView.setText(eventDetails.getSportsType());
                        sportsCategoryTextView.setText(eventDetails.getSportsCategory());
                        descriptionTextView.setText(eventDetails.getDescription());
                        locationTextView.setText(eventDetails.getLocation());
                        slotsAvailableTextView.setText("Slots Available: " + eventDetails.getSlotsAvailable());
                        pricePerSlotTextView.setText("Price per Slot: $" + eventDetails.getPricePerSlot());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        // Handle Confirm Booking button click
        confirmBookingButton.setOnClickListener(view -> {
            String numberOfSlots = numberOfSlotsEditText.getText().toString();
            // Perform booking based on the number of slots entered
            // Add your booking logic here using the value in numberOfSlots
        });
            }
    }


