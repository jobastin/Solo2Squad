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
import com.example.solo2squad.DashboardMain;
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
    GoogleSignInClient gsc;

    private LinearLayout googleLoginButton;

    //for unti testing
    // Setter method to inject FirebaseAuth instance
    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void setGsc(GoogleSignInClient gsc) {
        this.gsc = gsc;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Add your code to perform actions when the activity is restarting

        // For example, sign out the user if you are using Firebase Authentication
        signOut();
    }
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
                FirebaseAuth.getInstance().signOut();
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
                                                                Log.e("Login Google", String.valueOf(profileSection));
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


    private void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                //startActivity(new Intent(SecondActivity.this, MainActivity.class));
            }
        });
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credentials = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credentials)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                                if (isNewUser) {



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



                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    usersRef.child(uid).child("userId").setValue(uid); // Add this line
                                    usersRef.child(uid).setValue(newUser);


                                    // Check profileSection value
                                    int profileSection = newUser.getProfileSection();

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

                                } else {
                                    // The user is an existing user
                                    // Check the profileSection variable in the database
                                    checkProfileSection(user.getUid());
                                }

                                // Continue with the rest of your code
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkProfileSection(String uid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User exists in the database
                    int profileSection = snapshot.child("profileSection").getValue(Integer.class);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
