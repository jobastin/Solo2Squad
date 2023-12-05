package com.example.solo2squad.Authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solo2squad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputPasswordC;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private TextView loginTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //btnSignIn = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_reg);
        inputEmail = (EditText) findViewById(R.id.reg_email_txt);
        inputPassword = (EditText) findViewById(R.id.reg_password_txt);
        inputPasswordC= (EditText) findViewById(R.id.reg_repassowrd_txt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginTxt = findViewById(R.id.login_password_txt);

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String Cpassword = inputPasswordC.getText().toString().trim();
                Log.e("SIGNUP",email);

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$";
                if (!password.matches(passwordPattern)) {
                    Toast.makeText(getApplicationContext(), "Password Should have at least Minimum 6 \n characters, " +
                            "at least one letter, one \n number and one special character.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(Cpassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    String uid = user.getUid();
                                    String email = user.getEmail();
                                    Uri photoUrl = user.getPhotoUrl();
                                    String name= user.getDisplayName();

                                    // Send email verification
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> emailTask) {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Please try Again !!", Toast.LENGTH_SHORT).show();
                                                Log.e("Email Verification", "Failed to send verification email", emailTask.getException());
                                            }
                                        }
                                    });

                                    GoogleSignInUsers newUser;
                                    Address userAddress = new Address("","", "", "");

                                    if (photoUrl != null) {
                                        //newUser = new GoogleSignInUsers(uid, email, "photoUrl"); // Empty profile image for now
                                        newUser = new GoogleSignInUsers(uid,name,email,"",userAddress,"user",0,"photoUrl","");

                                    } else {
                                        //newUser = new GoogleSignInUsers(uid, name, "",""); // Empty profile image for now
                                        newUser = new GoogleSignInUsers(uid,name,email,"",userAddress,"user",0,"","");
                                    }
                                    // Create a User object
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    usersRef.child(uid).setValue(newUser);

                                    //Toast.makeText(SignupActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(SignupActivity.this, "Authentication success." + task.getException(),
//                                            Toast.LENGTH_SHORT).show();
//                                    //Adding data from Firebase database to Amazon RDS
//                                    FirebaseUser user = auth.getCurrentUser();
//                                    String uid = user.getUid();
//                                    String email = user.getEmail();
//                                    Log.e("user",String.valueOf(auth.getCurrentUser()));
//                                    Log.e("uid",uid);
//                                    Log.e("uid",email);
//
//
//
//                                    // Insert data into RDS database
////                                    Connection connection = DatabaseConnection.getConnection();
////                                    try {
////                                        String sql = "INSERT INTO Solo2Squad.tbl_login(uid_firebase, email, `role`, first_time_login, status) VALUES('uid', 'email', 'user', 0, 1);";
////
////                                        PreparedStatement statement = connection.prepareStatement(sql);
////                                        statement.setString(1, uid);
////                                        statement.setString(2, email);
////                                        statement.executeUpdate();
////                                        statement.close();
////                                        connection.close();
////                                    } catch (SQLException e) {
////                                        e.printStackTrace();
////                                    }
//
//
//
//                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                                    finish();
//                                }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}