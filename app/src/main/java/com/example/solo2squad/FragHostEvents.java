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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.solo2squad.Authentication.LoginActivity;
import com.example.solo2squad.ProfileSection.ProfileSection1Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FragHostEvents extends Fragment {

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

    private String pKey = StripeConfig.PUBLISHABLE_KEY;
    private String sKey = StripeConfig.S_KEY;
    PaymentSheet paymentSheet;
    OkHttpClient client = new OkHttpClient();

    private String storedClientSecret;
    private String customerId;
    private String ephemeralKey;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Stripe with your publishable key
        PaymentConfiguration.init(requireContext(), pKey);

        // Create a new instance of the Stripe class
        ///stripe = new Stripe(requireContext());
        paymentSheet=new PaymentSheet(this,paymentSheetResult -> {
            handlePaymentSheetResult(paymentSheetResult);
            addEventToFirebase();
            clearFields();

        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_host_event, container, false);




        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userEventsRef = FirebaseDatabase.getInstance().getReference().child("user_events").child(currentUser.getUid());
            spinnerSportsCategory = view.findViewById(R.id.spinnerSportsCategory);
            spinnerSportsType = view.findViewById(R.id.spinnerSportsType);


            editTextLocation = view.findViewById(R.id.editTextLocation);
            editTextDescription = view.findViewById(R.id.editTextDescription);
            editTextSlotsAvailable = view.findViewById(R.id.editTextSlotsAvailable);
            editTextPricePerSlot = view.findViewById(R.id.editTextPricePerSlot);
            checkBoxFreeBooking = view.findViewById(R.id.checkBoxFreeBooking);
            datePicker = view.findViewById(R.id.datePicker);
            timePicker = view.findViewById(R.id.timePicker);
            buttonHostEvent = view.findViewById(R.id.btnSubmit);
            textViewTimeError = view.findViewById(R.id.textViewTimeError);
            sportsCategoryRef = FirebaseDatabase.getInstance().getReference().child("sports_categories");
            sportsTypeRef = FirebaseDatabase.getInstance().getReference().child("sports_types");
            fetchSportsCategories();

            checkBoxFreeBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFreeBookingCheckBox();
                }
            });
            buttonHostEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hostEvent();
                }
            });
        }

        return view;
    }

    //fetching sports category to the spinner
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sportsCategories);
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

    //fetching sports types to the spinner
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

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sportsTypes);
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
    //handling the free bookings
    private void handleFreeBookingCheckBox() {

        if (checkBoxFreeBooking.isChecked()) {
            editTextPricePerSlot.setText("");
            editTextPricePerSlot.setVisibility(View.GONE);
        } else {
            editTextPricePerSlot.setVisibility(View.VISIBLE);
        }
    }

    //adding data to DB..payment gateway initial call
    private void hostEvent() {
        if (validateFields() && isFutureTimeSelected()) {
            if(checkBoxFreeBooking.isChecked()){
                Log.e("is1Checked","is1checked");
                addEventToFirebase();
                clearFields();
                //startActivity(new Intent(requireContext(), FragManageEvents.class));
            }
            else {
                Log.e("isnot1Checked","isnot1checked");
                createStripeCustomer();

            }
        }

    }

    //creating Stripe customer
    private void createStripeCustomer() {
        // Assuming you have some form of authentication, like Firebase Authentication
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail(); // Use email as a unique identifier for the customer

            // Build the request body with the customer's email
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .build();

            // Build the request with the customer creation endpoint
            Request request = new Request.Builder()
                    .url("https://api.stripe.com/v1/customers")
                    .header("Authorization", "Bearer " + sKey)
                    .post(requestBody)
                    .build();

            // Use the OkHttpClient to make the network call
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("Stripe", "Customer created: " + responseBody);

                        customerId = extractCustomerId(responseBody);

                        // Use the retrieved customer ID to create ephemeral key and payment intent
                        createEphemeralKey(customerId);
                        createPaymentIntent(customerId);
                    } else {
                        Log.e("Stripe", "Error creating customer: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("Stripe", "Error creating customer: " + e.getMessage());
                }
            });
        } else {
            Log.e("Stripe", "Current user is null");
        }
    }

    private String extractCustomerId(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.optString("id");
        } catch (JSONException e) {
            Log.e("Stripe", "Error extracting customer ID: " + e.getMessage());
            return null;
        }
    }


    //creating the ephemeral key for customer
    private void createEphemeralKey(String customerId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("customer", customerId)
                .build();

        Request request = new Request.Builder()
                .url("https://api.stripe.com/v1/ephemeral_keys")
                .header("Authorization", "Bearer " + sKey)
                .header("Stripe-Version", "2023-10-16")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Extract and store the ephemeral key
                    ephemeralKey = extractEphemeralKey(responseBody);
                    Log.d("Stripe", "Ephemeral key created: " + responseBody);
                } else {
                    Log.e("Stripe", "Error creating ephemeral key: " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Stripe", "Error creating ephemeral key: " + e.getMessage());
            }
        });
    }

    // Method to extract the ephemeral key from the response body
    private String extractEphemeralKey(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.optString("secret");
        } catch (JSONException e) {
            Log.e("Stripe", "Error extracting ephemeral key: " + e.getMessage());
            return null;
        }
    }


    //creating payment and adding the amount
    private void createPaymentIntent(String customerId) {
//        boolean freeBooking = checkBoxFreeBooking.isChecked();
//        double amount = freeBooking ? 0.0 : Double.parseDouble(editTextPricePerSlot.getText().toString().trim());
//        amount=(int)amount*100;
        int amount = 89120;
        RequestBody requestBody = new FormBody.Builder()
                .add("customer", customerId)
                .add("amount", String.valueOf(amount))
                .add("currency", "cad")  // Replace with your desired currency
                .add("automatic_payment_methods[enabled]", "true")
                .build();

        Request request = new Request.Builder()
                .url("https://api.stripe.com/v1/payment_intents")
                .header("Authorization", "Bearer " + sKey)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("Stripe", "Payment intent created: " + responseBody);
                    storedClientSecret = extractClientSecret(responseBody);



                    // Open the PaymentSheet for payment confirmation
                    paymentSheet.presentWithPaymentIntent(responseBody);
                    PaymentFlow();
                } else {
                    Log.e("Stripe", "Error creating payment intent: " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Stripe", "Error creating payment intent: " + e.getMessage());
            }
        });
    }

    //extracting client secreat key for payment flow
    private String extractClientSecret(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.optString("client_secret");
        } catch (JSONException e) {
            Log.e("Stripe", "Error extracting client secret: " + e.getMessage());
            return null;
        }
    }

    private void PaymentFlow() {


        paymentSheet.presentWithPaymentIntent(
                storedClientSecret,new PaymentSheet.Configuration("Solo2Squad",
                        new PaymentSheet.CustomerConfiguration(
                                customerId,ephemeralKey
                        ))
        );
    }

    //handling the payment result : success or failure
    private void handlePaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            //paymentCheck=false;
            Log.e("Cancelled","handle payment");
            //Toast.makeText(requireContext(), "Payment Cancelled !!! Please try again", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            //paymentCheck=false;
            //Toast.makeText(requireContext(), "Payment Failed !!! Please try again", Toast.LENGTH_SHORT).show();
            Log.e("Failed","handle payment");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {


           // Toast.makeText(requireContext(), "Payment Success", Toast.LENGTH_SHORT).show();

            Log.e("Complete","handle payment");
            startActivity(new Intent(requireContext(), FragManageEvents.class));
        }
    }



    //field validations
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
    private void addEventToFirebase() {
        // Get the selected values from the UI
        String userID = auth.getCurrentUser().getUid(); // Assuming you have the auth instance
        String sportsCategory = spinnerSportsCategory.getSelectedItem().toString();
        String sportsType = spinnerSportsType.getSelectedItem().toString();
        String location = editTextLocation.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int totalSlots = Integer.parseInt(editTextSlotsAvailable.getText().toString().trim());
        int slotsAvailable = Integer.parseInt(editTextSlotsAvailable.getText().toString().trim());
        boolean freeBooking = checkBoxFreeBooking.isChecked();
        double pricePerSlot = freeBooking ? 0.0 : Double.parseDouble(editTextPricePerSlot.getText().toString().trim());

        // Get the selected date and time
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

        // Create an event object
        //event newEvent = new event(userID, sportsCategory, sportsType, location, description, String.valueOf(timestamp),totalSlots,
        //slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus,paymentStatus,payment);

        // Reference to the Firebase database
        //DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

        // Push the new event to the database
        //String eventKey = hostedEventsRef.push().getKey();
        //hostedEventsRef.child(eventKey).setValue(newEvent);
        //EventPayment payment = new EventPayment();
        //DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events").child(eventKey).child("Payments");
        long currentTimestamp = System.currentTimeMillis();
        if (checkBoxFreeBooking.isChecked()) {

            Log.e("isChecked","ischecked");

            EventPayment payment = new EventPayment("","",0.0,currentTimestamp,0);
            event newEvent = new event(userID, sportsCategory, sportsType, location, description, String.valueOf(timestamp),totalSlots,
                    slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus,paymentStatus,payment);


            DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

            String eventKey = hostedEventsRef.push().getKey();
            hostedEventsRef.child(eventKey).setValue(newEvent);
        }
        else {
            EventPayment payment = new EventPayment(customerId, ephemeralKey, pricePerSlot, currentTimestamp, 1);
            event newEvent = new event(userID, sportsCategory, sportsType, location, description, String.valueOf(timestamp), totalSlots,
                    slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus,paymentStatus, payment);


            DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

            String eventKey = hostedEventsRef.push().getKey();
            hostedEventsRef.child(eventKey).setValue(newEvent);

            Log.e("isnotChecked", "isnotchecked");

//            if (paymentCheck){
//
//                EventPayment payment = new EventPayment(customerId, ephemeralKey, pricePerSlot, currentTimestamp, 1);
//            event newEvent = new event(userID, sportsCategory, sportsType, location, description, String.valueOf(timestamp), totalSlots,
//                    slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus, "Done", payment);
//
//
//            DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");
//
//            String eventKey = hostedEventsRef.push().getKey();
//            hostedEventsRef.child(eventKey).setValue(newEvent);
//
//            Log.e("isnotChecked", "isnotchecked");
//        }
//            else {
//                EventPayment payment = new EventPayment(customerId, ephemeralKey, pricePerSlot, currentTimestamp, 0);
//                event newEvent = new event(userID, sportsCategory, sportsType, location, description, String.valueOf(timestamp), totalSlots,
//                        slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus, "Failed", payment);
//
//
//                DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");
//
//                String eventKey = hostedEventsRef.push().getKey();
//                hostedEventsRef.child(eventKey).setValue(newEvent);
//
//            }

        }

//        // Navigate based on booking type
//        if (freeBooking) {
//
//            clearFields();
//            // Navigate to activity_manage events (assuming this is the target activity for free bookings)
//            // Intent manageEventsIntent = new Intent(requireContext(), ManageEventsActivity.class);
//            // startActivity(manageEventsIntent);
//        } else {
//            clearFields();
//            // Navigate to payment gateway (replace PaymentGatewayActivity with your actual payment gateway activity)
//            // Intent paymentGatewayIntent = new Intent(requireContext(), PaymentGatewayActivity.class);
//            // startActivity(paymentGatewayIntent);
//        }

        // Show a success message
        Toast.makeText(requireContext(), "Event hosted successfully", Toast.LENGTH_SHORT).show();
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
