package com.example.solo2squad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class editEvents extends AppCompatActivity {

    private Spinner spinnerSportsCategory;
    private Spinner spinnerSportsType;

    private FirebaseAuth auth;
    private DatabaseReference sportsCategoryRef;
    private DatabaseReference sportsTypeRef,userEventsRef;

    private EditText editTextLocation, editTextDescription, editTextSlotsAvailable, editTextPricePerSlot;
    private CheckBox checkBoxFreeBooking;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button buttonHostEvent;
    private TextView textViewTimeError;

    private boolean ischecked = true;
    private String uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uniqueId = getIntent().getStringExtra("eventKey");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_events);




        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && uniqueId != null) {
            userEventsRef = FirebaseDatabase.getInstance().getReference().child("user_events").child(currentUser.getUid());
            spinnerSportsCategory =findViewById(R.id.spinnerSportsCategory);
            spinnerSportsType = findViewById(R.id.spinnerSportsType);


            editTextLocation = findViewById(R.id.editTextLocation);
            editTextDescription = findViewById(R.id.editTextDescription);
            editTextSlotsAvailable = findViewById(R.id.editTextSlotsAvailable);
            editTextPricePerSlot = findViewById(R.id.editTextPricePerSlot);
            checkBoxFreeBooking = findViewById(R.id.checkBoxFreeBooking);
            datePicker = findViewById(R.id.datePicker);
            timePicker = findViewById(R.id.timePicker);
            buttonHostEvent = findViewById(R.id.btnSubmit);
            textViewTimeError = findViewById(R.id.textViewTimeError);
            sportsCategoryRef = FirebaseDatabase.getInstance().getReference().child("sports_categories");
            sportsTypeRef = FirebaseDatabase.getInstance().getReference().child("sports_types");
            fetchSportsCategories();

            // Retrieve event details based on the unique ID
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events").child(uniqueId);
            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        event existingEvent = dataSnapshot.getValue(event.class);
                        if (existingEvent != null) {
                            // Populate the fields with existing data
                            populateFields(existingEvent);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });


            checkBoxFreeBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFreeBookingCheckBox();
                }
            });
            buttonHostEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateEvent();
                }
            });
        }

    }

    private void fetchSportsCategories() {
        sportsCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> sportsCategories = new ArrayList<>();

                    sportsCategories.add("Select Category");


                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryID = categorySnapshot.getKey();
                        String categoryName = categorySnapshot.child("name").getValue(String.class);

                        if (categoryID != null && categoryName != null) {
                            sportsCategories.add(categoryID + ": " + categoryName);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(editEvents.this, android.R.layout.simple_spinner_item, sportsCategories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSportsCategory.setAdapter(adapter);

                    spinnerSportsCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String selectedCategory = sportsCategories.get(position);
                            // Extract the category ID from the selected string
                            String[] parts = selectedCategory.split(": ");
                            if (parts.length == 2) {
                                String categoryID = parts[0];
                                fetchSportsTypes(categoryID);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Do nothing here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void fetchSportsTypes(String selectedCategoryID) {
        sportsTypeRef.orderByChild("category_id").equalTo(Integer.parseInt(selectedCategoryID))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<String> sportsTypes = new ArrayList<>();
// Add a default type
                            sportsTypes.add("Select Type");
                            for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                                String typeName = typeSnapshot.child("name").getValue(String.class);
                                if (typeName != null) {
                                    sportsTypes.add(typeName);
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(editEvents.this, android.R.layout.simple_spinner_item, sportsTypes);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSportsType.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }




    private void populateFields(event existingEvent) {
        // Set retrieved data to the respective views
        editTextLocation.setText(existingEvent.getLocation());
        editTextDescription.setText(existingEvent.getDescription());
        editTextSlotsAvailable.setText(String.valueOf(existingEvent.getSlotsAvailable()));
        editTextPricePerSlot.setText(String.valueOf(existingEvent.getPricePerSlot()));
        // Set other fields similarly...

        // Handle CheckBox and Price Per Slot based on retrieved data
        if (existingEvent.isFreeBooking()) {
            checkBoxFreeBooking.setChecked(true);
            editTextPricePerSlot.setVisibility(View.GONE);
        } else {
            checkBoxFreeBooking.setChecked(false);
            editTextPricePerSlot.setVisibility(View.VISIBLE);
            editTextPricePerSlot.setText(String.valueOf(existingEvent.getPricePerSlot()));
        }

        // Set Date and Time Pickers based on timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(existingEvent.getTimestamp()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        datePicker.updateDate(year, month, day);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);
    }
    private void handleFreeBookingCheckBox() {

        if (checkBoxFreeBooking.isChecked()) {
            editTextPricePerSlot.setText("");
            editTextPricePerSlot.setVisibility(View.GONE);
        } else {
            editTextPricePerSlot.setVisibility(View.VISIBLE);
        }
    }


    private boolean validateFields() {
        boolean isValid = true;

        if (!isPricePerSlotValid()) {
            isValid=false;
        }
        //for location
        String location = editTextLocation.getText().toString().trim();
        if (location.isEmpty()) {
            editTextLocation.setError("Location is required");
            isValid = false;
        } else {
            editTextLocation.setError(null);
        }

        //checking slots
        String slotsAvailableStr = editTextSlotsAvailable.getText().toString().trim();
        if (slotsAvailableStr.isEmpty()) {
            editTextSlotsAvailable.setError("Slots Available is required");
            isValid = false;
        } else {
            try {
                int slotsAvailable = Integer.parseInt(slotsAvailableStr);
                if (slotsAvailable <= 0) {
                    editTextSlotsAvailable.setError("Slots Available should be greater than 0");
                    isValid = false;
                } else {
                    editTextSlotsAvailable.setError(null);
                }
            } catch (NumberFormatException e) {
                editTextSlotsAvailable.setError("Invalid number");
                isValid = false;
            }
        }

        String selectedCategory = spinnerSportsCategory.getSelectedItem().toString();
        if ("Select Category".equals(selectedCategory)) {
            // Show error next to the spinner
            ((TextView) spinnerSportsCategory.getSelectedView()).setError("Please select a sports category");
            isValid = false;
        } else {
            // Clear the error if a valid selection is made
            ((TextView) spinnerSportsCategory.getSelectedView()).setError(null);

            // Validate Sports Type Spinner only if a valid category is selected
            String[] parts = selectedCategory.split(": ");
            if (parts.length == 2) {
                String categoryID = parts[0];
                isValid = isValid && validateSportsType(categoryID);
            }
        }

        return isValid;
    }

    private void updateEvent() {

        try {
            if (validateFields() && isFutureTimeSelected()) {
                // Retrieve the updated values from the views
                String location = editTextLocation.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                int slotsAvailable = Integer.parseInt(editTextSlotsAvailable.getText().toString().trim());
                boolean freeBooking = checkBoxFreeBooking.isChecked();
                double pricePerSlot = freeBooking ? 0.0 : Double.parseDouble(editTextPricePerSlot.getText().toString().trim());

                // Get the selected date and time from Date and Time Pickers
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute);

                long timestamp = calendar.getTimeInMillis();
                int activeStatus = 1; // Assuming 1 for active status
                String paymentStatus = getPaymentStatus();

                EventPayment payment = new EventPayment("","",0.0,0,0);
                // Create an event object with updated details
                event updatedEvent = new event(auth.getCurrentUser().getUid(), spinnerSportsCategory.getSelectedItem().toString(),
                        spinnerSportsType.getSelectedItem().toString(), location, description, String.valueOf(timestamp),0,
                        slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus, paymentStatus,payment);



                // Reference to the Firebase database
                DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

                // Update the existing event in the database with the updated details
                hostedEventsRef.child(uniqueId).setValue(updatedEvent);

                // Show a success message
                Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();

                redirectToDashboardWithFragmentSelected(2);
            }
        }catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions here
        }
    }

    private void redirectToDashboardWithFragmentSelected(int fragmentIndex) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("FRAGMENT_INDEX", fragmentIndex);
        startActivity(intent);
        finish(); // Optionally, finish the current activity to prevent going back to it
    }



    private boolean isPricePerSlotValid() {

        if (checkBoxFreeBooking.isChecked()) {
            // If free booking is checked, no need to validate price per slot
            editTextPricePerSlot.setError(null); // Clear the error
            return true;
        }
        String pricePerSlotStr = editTextPricePerSlot.getText().toString().trim();

        if (pricePerSlotStr.isEmpty()) {
            // Price per slot is empty
            editTextPricePerSlot.setError("Price per slot is required");
            return false;
        }

        try {
            double pricePerSlot = Double.parseDouble(pricePerSlotStr);

            if (pricePerSlot < 0) {
                // Price per slot is negative
                editTextPricePerSlot.setError("Price per slot must be non-negative");
                return false;
            }

            // Price per slot is valid
            editTextPricePerSlot.setError(null); // Clear the error
            return true;
        } catch (NumberFormatException e) {
            // Invalid double value
            editTextPricePerSlot.setError("Invalid price per slot");
            return false;
        }
    }


    private boolean isFutureTimeSelected() {
        // Get the selected date and time
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Create a Calendar instance for the selected date and time
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, day, hour, minute);

        // Create a Calendar instance for the current date and time
        Calendar currentCalendar = Calendar.getInstance();

        // Compare the selected time with the current time
        if (selectedCalendar.after(currentCalendar)) {
            // The selected time is in the future
            textViewTimeError.setVisibility(View.GONE);
            return true;
        } else {
            // The selected time is not in the future
            textViewTimeError.setText("Please select a future time");
            textViewTimeError.setVisibility(View.VISIBLE);
            return false;
        }
    }
    private boolean validateSportsType(String categoryID) {
        ischecked = true;

        // Fetch sports types based on the selected category
        sportsTypeRef.orderByChild("category_id").equalTo(Integer.parseInt(categoryID))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Show error next to the sports type spinner
                            ((TextView) spinnerSportsType.getSelectedView()).setError("No sports types available for the selected category");
                            ischecked = false;
                        } else {
                            // Clear the error if valid sports types are available
                            ((TextView) spinnerSportsType.getSelectedView()).setError(null);

                            // Check if "Select Type" is selected in sports type
                            String selectedSportsType = spinnerSportsType.getSelectedItem().toString();
                            if ("Select Type".equals(selectedSportsType)) {
                                // Show error next to the sports type spinner
                                ((TextView) spinnerSportsType.getSelectedView()).setError("Please select a sports type");
                                ischecked = false;
                            } else {
                                // Clear the error if a valid sports type is selected
                                ((TextView) spinnerSportsType.getSelectedView()).setError(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });

        return ischecked;
    }

    private String getPaymentStatus() {
        // Assuming a simple logic for demonstration
        return checkBoxFreeBooking.isChecked() ? "Free" : "Pending"; // Replace with your logic
    }

    private void clearFields() {
        // Clear Sports Category Spinner
        spinnerSportsCategory.setSelection(0);

        // Clear Sports Type Spinner
        spinnerSportsType.setAdapter(null);

        // Clear Location EditText
        editTextLocation.setText("");

        // Clear Time Picker
        // Assuming you have a method to set the current time, replace setCurrentTime() with your method
        setCurrentTime();

        // Clear Date Picker
        // Assuming you have a method to set the current date, replace setCurrentDate() with your method
        setCurrentDate();

        // Clear Description EditText
        editTextDescription.setText("");

        // Clear Slots Available EditText
        editTextSlotsAvailable.setText("");

        // Uncheck Free Booking CheckBox and show Price Per Slot EditText
        checkBoxFreeBooking.setChecked(false);
        editTextPricePerSlot.setVisibility(View.VISIBLE);
        editTextPricePerSlot.setText(""); // Clear the text

        // Clear any error messages
        textViewTimeError.setVisibility(View.GONE);
    }

    //reset to current time
    private void setCurrentTime() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Set the current time in the TimePicker
        timePicker.setHour(currentHour);
        timePicker.setMinute(currentMinute);
    }


    //reset to current date
    private void setCurrentDate() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the current date in the DatePicker
        datePicker.updateDate(currentYear, currentMonth, currentDay);
    }



}
