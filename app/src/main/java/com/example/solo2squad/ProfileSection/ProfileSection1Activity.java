package com.example.solo2squad.ProfileSection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solo2squad.DashboardActivity;
import com.example.solo2squad.DashboardMain;
import com.example.solo2squad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ProfileSection1Activity extends AppCompatActivity {

    private EditText etName, etCellphone, etStreet, etCity, etProvince, etPostalCode;
    private DatePicker datePicker;
    private TextView dateError;
    private Button btnSave;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesection1);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etName);
        etCellphone = findViewById(R.id.etCellphone);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etProvince = findViewById(R.id.etProvince);
        etPostalCode = findViewById(R.id.etPostalCode);
        datePicker = findViewById(R.id.datePicker);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.reg_progressBar);
        dateError = findViewById(R.id.dateError);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });
    }

    private void saveProfileData() {
        String name = etName.getText().toString().trim();
        String cellphone = etCellphone.getText().toString().trim();
        String street = etStreet.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String province = etProvince.getText().toString().trim();
        String postalCode = etPostalCode.getText().toString().trim();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2006, Calendar.DECEMBER, 31);



        if (name.isEmpty()) {
            etName.setError("Enter a valid Name : Cannot be empty");
            return;
        }
        if (!validateDate(calendar, maxDate)) {
            return; // Exit if date is not valid
        }
        if (cellphone.isEmpty()) {
            etCellphone.setError("Cellphone cannot be empty");
            return;
        }
        cellphone = etCellphone.getText().toString().trim();

        if (!validatePhoneNumber(cellphone)) {
            etCellphone.setError("Invalid phone number : Enter a 10 digit number !!");
            return;
        }
        if (street.isEmpty()) {
            etStreet.setError("Street cannot be empty");
            return;
        }
        if (city.isEmpty()) {
            etCity.setError("City cannot be empty");
            return;
        }
        if (province.isEmpty()) {
            etProvince.setError("Province cannot be empty");
            return;
        }
        if (postalCode.isEmpty()) {
            etPostalCode.setError("Postal Code cannot be empty");
            return;
        }


        // Add similar validations for other fields as needed

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);

            String uid = user.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            usersRef.child("name").setValue(name);
            usersRef.child("phoneNumber").setValue(cellphone);
            usersRef.child("address").child("street").setValue(street);
            usersRef.child("address").child("city").setValue(city);
            usersRef.child("address").child("province").setValue(province);
            usersRef.child("address").child("postalCode").setValue(postalCode);
            usersRef.child("dateOfBirth").setValue(day + "/" + (month + 1) + "/" + year);
            usersRef.child("profileSection").setValue(1);

            progressBar.setVisibility(View.GONE);

            // Redirect to ProfileSection2Activity
            startActivity(new Intent(ProfileSection1Activity.this, DashboardMain.class));
            finish();
        }
    }

    private boolean validateDate(Calendar calendar, Calendar maxDate) {
        if (calendar.after(Calendar.getInstance()) || calendar.after(maxDate)) {
            dateError.setVisibility(View.VISIBLE);
            dateError.setText("Please select a valid date (before 2006)");
            return false;
        } else {
            dateError.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        //String regex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*(\\d{3})\\s*\\)|((\\d{3})))\\s*(?:[.-]\\s*)?)?(\\d{3})\\s*(?:[.-]\\s*)?(\\d{4})$";
        String regex = "^\\d{10}$";
        return phoneNumber.matches(regex);
    }

}
