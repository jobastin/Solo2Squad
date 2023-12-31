package com.example.solo2squad.ProfileSection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solo2squad.DashboardActivity;
import com.example.solo2squad.EventPayment;
import com.example.solo2squad.R;
import com.example.solo2squad.StripeConfig;
import com.example.solo2squad.event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    int priceFirebase;


    private FirebaseAuth auth;

    private String pKey = StripeConfig.PUBLISHABLE_KEY;
    private String sKey = StripeConfig.S_KEY;
    PaymentSheet paymentSheet;
    OkHttpClient client = new OkHttpClient();
    private String storedClientSecret;
    private String customerId;
    private String ephemeralKey;



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

        auth = FirebaseAuth.getInstance();

        createStripeCustomer();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

        databaseReference.child(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the event object based on the unique ID
                    event eventDetails = dataSnapshot.getValue(event.class);

                    priceFirebase= (int) eventDetails.getPricePerSlot();

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

        paymentSheet=new PaymentSheet(this,paymentSheetResult -> {
            handlePaymentSheetResult(paymentSheetResult);
        });

        // Handle Confirm Booking button click
        confirmBookingButton.setOnClickListener(view -> {
            String numberOfSlots = numberOfSlotsEditText.getText().toString();
            int numberOfSlots1 = Integer.parseInt(numberOfSlots);
            priceFirebase = priceFirebase * numberOfSlots1;


            PaymentFlow();
        });

        PaymentConfiguration.init(this, pKey);
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
        int amount = priceFirebase;
        RequestBody requestBody = new FormBody.Builder()
                .add("customer", customerId)
                .add("amount", String.valueOf(amount)+"00")
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

                    // Run on the UI thread before presenting the payment sheet
//                    runOnUiThread(() -> {
//                        paymentSheet.presentWithPaymentIntent(responseBody);
//                        //PaymentFlow();
//                    });
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
            Log.e("Cancelled","handle payment");
            Toast.makeText(this, "Payment Cancelled !!! Please try again", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment Failed !!! Please try again", Toast.LENGTH_SHORT).show();
            Log.e("Failed","handle payment");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            Toast.makeText(this, "Booking Confirmed", Toast.LENGTH_SHORT).show();

            Log.e("Complete","handle payment");
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }
    }


