package com.example.solo2squad.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solo2squad.DashboardActivity;
import com.example.solo2squad.ProfileSection.ProfileSection1Activity;
import com.example.solo2squad.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button  btnLogin;
    private TextView btnReset, btnSignup;

    FirebaseDatabase firebaseDatabase;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;

    private LinearLayout googleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        //Google sing in button variable declaration
        firebaseDatabase =FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are Creating your account");

        gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        Log.e("SIGNUP",String.valueOf(auth.getCurrentUser()));
//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
//            Log.e("Login", String.valueOf(auth.getCurrentUser()));
//            finish();
//        }

        // set the view now
        setContentView(R.layout.activity_login);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.login_email_txt);
        inputPassword = (EditText) findViewById(R.id.login_password_txt);
        progressBar = (ProgressBar) findViewById(R.id.reg_progressBar);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.forgot_password_txt);
        googleLoginButton =findViewById(R.id.googleLoginButton);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();



        //Google sign in button code
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }

        });



        //signup page redirection
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        //password reset button redirection
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });


        //Login code with firebase authentication
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseUser user = auth.getCurrentUser();
//                String email = inputEmail.getText().toString();
//                final String password = inputPassword.getText().toString();
//                user.reload();
//
//
//
//                if (user != null) {
//                    Log.e("Email verification", String.valueOf(user.isEmailVerified()));
//                    user.reload();
//                    if (user.isEmailVerified()) {
//                        // User is logged in and email is verified, proceed to main activity
//                        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                            if (!password.isEmpty()) {
//                                progressBar.setVisibility(View.VISIBLE);
//                                auth.signInWithEmailAndPassword(email, password)
//                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                            @Override
//                                            public void onSuccess(AuthResult authResult) {
//                                                progressBar.setVisibility(View.GONE);
//                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
//                                                finish();
//
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                progressBar.setVisibility(View.GONE);
//                                                Toast.makeText(LoginActivity.this, "Please Enter a valid Email & Password", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            } else {
//                                progressBar.setVisibility(View.GONE);
//                                inputPassword.setError("Password cannot be empty");
//                            }
//
//                        } else if (email.isEmpty()) {
//                            progressBar.setVisibility(View.GONE);
//                            inputEmail.setError("Email cannot be empty");
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            inputEmail.setError("Please enter a valid email");
//                        }
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
//                    } else {
//                        // User is logged in but email is not verified
//                        Toast.makeText(LoginActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
//
//                    }
//                } else {
//                    // User is not logged in, show login UI
//                    // If sign in fails, display a message to the user.
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(LoginActivity.this, "Authentication failed. Please enter a valid email and password.", Toast.LENGTH_SHORT).show();
//                }
//
//
//
//                Log.e("SIGNUP",String.valueOf(auth.getCurrentUser()));
//            }
//        });

//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = inputEmail.getText().toString();
//                final String password = inputPassword.getText().toString();
//
//                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    if (!password.isEmpty()) {
//                        progressBar.setVisibility(View.VISIBLE);
//                        auth.signInWithEmailAndPassword(email, password)
//                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                    @Override
//                                    public void onSuccess(AuthResult authResult) {
//                                        // Reload user and update UI
//                                        FirebaseUser user = auth.getCurrentUser();
//                                        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    // User reloaded successfully
//                                                    if (user.isEmailVerified()) {
//                                                        progressBar.setVisibility(View.GONE);
//                                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                                                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
//                                                        finish();
//                                                    } else {
//                                                        progressBar.setVisibility(View.GONE);
//                                                        Toast.makeText(LoginActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                } else {
//                                                    progressBar.setVisibility(View.GONE);
//                                                    Toast.makeText(LoginActivity.this, "Failed to reload user.", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        progressBar.setVisibility(View.GONE);
//                                        Toast.makeText(LoginActivity.this, "Please Enter a valid Email & Password", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                        inputPassword.setError("Password cannot be empty");
//                    }
//
//                } else if (email.isEmpty()) {
//                    progressBar.setVisibility(View.GONE);
//                    inputEmail.setError("Email cannot be empty");
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                    inputEmail.setError("Please enter a valid email");
//                }
//            }
//        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!password.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (user.isEmailVerified()) {
                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                int profileSection = dataSnapshot.child("profileSection").getValue(Integer.class);
                                                                if (profileSection == 0) {
                                                                    // Redirect to ProfileSection1Activity
                                                                    startActivity(new Intent(LoginActivity.this, ProfileSection1Activity.class));
                                                                } else if (profileSection == 1) {
                                                                    // Redirect to DashboardActivity
                                                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                                                } else {
                                                                    // Handle other cases if needed
                                                                }
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }


                                                        });
                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(LoginActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(LoginActivity.this, "Failed to reload user.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Please Enter a valid Email & Password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        inputPassword.setError("Password cannot be empty");
                    }
                } else if (email.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    inputEmail.setError("Email cannot be empty");
                } else {
                    progressBar.setVisibility(View.GONE);
                    inputEmail.setError("Please enter a valid email");
                }
            }
        });

    }


    int RC_SIGN_IN = 40;
    private void signIn() {
        Intent intent =mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==RC_SIGN_IN){
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account =task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credientials = GoogleAuthProvider.getCredential(idToken,null);

        auth.signInWithCredential(credientials)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();
                            String email = user.getEmail();
                            Uri photoUrl = user.getPhotoUrl();
                            String name= user.getDisplayName();

                            GoogleSignInUsers newUser;
                            Address userAddress = new Address("","", "", "");

                            if (photoUrl != null) {
                                //newUser = new GoogleSignInUsers(uid, email, "photoUrl"); // Empty profile image for now
                                newUser = new GoogleSignInUsers(uid,name,email,"",userAddress,"user",0,"photoUrl","");

                            } else {
                                //newUser = new GoogleSignInUsers(uid, name, "",""); // Empty profile image for now
                                newUser = new GoogleSignInUsers(uid,name,email,"",userAddress,"user",0,"","");
                            }

                            //GoogleSignInUsers users = new GoogleSignInUsers(user.getUid(),user.getDisplayName(),user.getPhotoUrl().toString());
//                            users.setUserId(user.getUid());
//                            users.setName(user.getDisplayName());
//                            users.setImage(user.getPhotoUrl().toString());

                            //firebaseDatabase.getReference().child("users").child(user.getUid()).setValue(users);

//                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
//                            usersRef.child(uid).setValue(newUser);

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                            usersRef.child(uid).child("userId").setValue(uid); // Add this line
                            usersRef.child(uid).setValue(newUser);


                            Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    //        signupRedirectText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
//            }
//        });
}
