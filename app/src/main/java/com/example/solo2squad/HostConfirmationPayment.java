package com.example.solo2squad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HostConfirmationPayment extends AppCompatActivity {

    private TextView textViewUserID;
    private TextView textViewSportsCategory;
    private TextView textViewSportsType;
    private TextView textViewLocation;
    private TextView textViewDescription;
    private TextView textViewTotalSlots;
    private TextView textViewSlotsAvailable;
    private TextView textViewFreeBooking;
    private TextView textViewPricePerSlot;
    private TextView textViewTimestamp;
    private TextView textViewActiveStatus;
    private TextView textViewPaymentStatus;

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
        setContentView(R.layout.activity_host_confirmation_payment);

        // Initialize your TextViews
        textViewUserID = findViewById(R.id.textViewUserID);
        textViewSportsCategory = findViewById(R.id.textViewSportsCategory);
        textViewSportsType = findViewById(R.id.textViewSportsType);
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewTotalSlots = findViewById(R.id.textViewTotalSlots);
        textViewSlotsAvailable = findViewById(R.id.textViewSlotsAvailable);
        textViewFreeBooking = findViewById(R.id.textViewFreeBooking);
        textViewPricePerSlot = findViewById(R.id.textViewPricePerSlot);
        textViewTimestamp = findViewById(R.id.textViewTimestamp);
        textViewActiveStatus = findViewById(R.id.textViewActiveStatus);
        textViewPaymentStatus = findViewById(R.id.textViewPaymentStatus);

        // Retrieve data from Intent
        Intent intent = getIntent();
        //String eventKey = intent.getStringExtra("eventKey");

        auth = FirebaseAuth.getInstance();

        createStripeCustomer();



        // Retrieve more data as needed
        String userID = intent.getStringExtra("userID");
        String sportsCategory = intent.getStringExtra("sportsCategory");
        String sportsType = intent.getStringExtra("sportsType");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        int totalSlots = intent.getIntExtra("totalSlots", 0);
        int slotsAvailable = intent.getIntExtra("slotsAvailable", 0);
        boolean freeBooking = intent.getBooleanExtra("freeBooking", false);
        double pricePerSlot = intent.getDoubleExtra("pricePerSlot", 0.0);
        long timestamp = intent.getLongExtra("timestamp", 0);
        int activeStatus = intent.getIntExtra("activeStatus", 0);
        String paymentStatus = intent.getStringExtra("paymentStatus");


        Log.e("HostConfirmationPayment", "User ID: " + userID);
        Log.e("HostConfirmationPayment", "Sports Category: " + sportsCategory);
        Log.e("HostConfirmationPayment", "Sports Type: " + sportsType);
        Log.e("HostConfirmationPayment", "Location: " + location);
        Log.e("HostConfirmationPayment", "Description: " + description);
        Log.e("HostConfirmationPayment", "Total Slots: " + totalSlots);
        Log.e("HostConfirmationPayment", "Slots Available: " + slotsAvailable);
        Log.e("HostConfirmationPayment", "Free Booking: " + freeBooking);
        Log.e("HostConfirmationPayment", "Price Per Slot: " + pricePerSlot);
        Log.e("HostConfirmationPayment", "Timestamp: " + timestamp);
        Log.e("HostConfirmationPayment", "Active Status: " + activeStatus);
        Log.e("HostConfirmationPayment", "Payment Status: " + paymentStatus);

        // Set values to TextViews
        textViewUserID.setText("User ID: " + userID);
        textViewSportsCategory.setText("Sports Category: " + sportsCategory);
        textViewSportsType.setText("Sports Type: " + sportsType);
        textViewLocation.setText("Location: " + location);
        textViewDescription.setText("Description: " + description);
        textViewTotalSlots.setText("Total Slots: " + totalSlots);
        textViewSlotsAvailable.setText("Slots Available: " + slotsAvailable);
        textViewFreeBooking.setText("Free Booking: " + (freeBooking ? "Yes" : "No"));
        textViewPricePerSlot.setText("Price Per Slot: " + pricePerSlot);
        textViewTimestamp.setText("Timestamp: " + timestamp);
        textViewActiveStatus.setText("Active Status: " + activeStatus);
        textViewPaymentStatus.setText("Payment Status: " + paymentStatus);

        // Use the retrieved data as needed
        // For example, set text in TextViews
        TextView textViewUserID = findViewById(R.id.textViewUserID);
        textViewUserID.setText("User ID: " + userID);

        TextView textViewSportsCategory = findViewById(R.id.textViewSportsCategory);
        textViewSportsCategory.setText("Sports Category: " + sportsCategory);

        // Continue for other data...

        // Example for displaying timestamp in a human-readable format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(timestamp));
        TextView textViewTimestamp = findViewById(R.id.textViewTimestamp);
        textViewTimestamp.setText("Timestamp: " + formattedDate);

        paymentSheet=new PaymentSheet(this,paymentSheetResult -> {
            handlePaymentSheetResult(paymentSheetResult);
        });


        Button paymentButton = findViewById(R.id.paymentButton);

        // Set an onClickListener for the button
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();

            }
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
        int amount = 1000;
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

            // Retrieve more data as needed

            Intent intent = getIntent();
            String userID = intent.getStringExtra("userID");
            String sportsCategory = intent.getStringExtra("sportsCategory");
            String sportsType = intent.getStringExtra("sportsType");
            String location = intent.getStringExtra("location");
            String description = intent.getStringExtra("description");
            int totalSlots = intent.getIntExtra("totalSlots", 0);
            int slotsAvailable = intent.getIntExtra("slotsAvailable", 0);
            boolean freeBooking = intent.getBooleanExtra("freeBooking", false);
            double pricePerSlot = intent.getDoubleExtra("pricePerSlot", 0.0);
            long timestamp = intent.getLongExtra("timestamp", 0);
            int activeStatus = intent.getIntExtra("activeStatus", 0);
            String paymentStatus = intent.getStringExtra("paymentStatus");
            long currentTimestamp = System.currentTimeMillis();
            EventPayment payment = new EventPayment(customerId, ephemeralKey,pricePerSlot, currentTimestamp, 1);
            event newEvent = new event(userID, sportsCategory, sportsType, location, description, String.valueOf(timestamp), totalSlots,
                    slotsAvailable, freeBooking, pricePerSlot, timestamp, activeStatus,"Done", payment);


            DatabaseReference hostedEventsRef = FirebaseDatabase.getInstance().getReference().child("Hosted_events");

            String eventKey = hostedEventsRef.push().getKey();
            hostedEventsRef.child(eventKey).setValue(newEvent);


            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();

            Log.e("Complete","handle payment");
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

}

