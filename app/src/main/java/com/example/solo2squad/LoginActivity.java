package com.example.solo2squad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

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



        //Get Firebase auth instance
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
//            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
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



//            private void signIn() {
//                Intent intent =mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(intent,RC_SIGN_IN);
//            }





           // private void firebaseAuth(String idToken) {

//                AuthCredential credientials = GoogleAuthProvider.getCredential(idToken,null);
//
//                auth.signInWithCredential(credientials)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                if(task.isSuccessful()){
//                                    FirebaseUser user = auth.getCurrentUser();
//
//                                    GoogleSignInUsers users = new GoogleSignInUsers();
//                                    users.setUserId(user.getUid());
//                                    users.setName(user.getDisplayName());
//                                    users.setProfile(user.getPhotoUrl().toString());
//
//                                    firebaseDatabase.getReference().child("GoogleSignInUsers").child(user.getUid()).setValue(users);
//                                    Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
//                                    startActivity(intent);
//                                }
//                                else{
//                                    Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        });
            //}


//        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//            LoginActivity.super.onActivityResult(requestCode, resultCode, data);
//            if(requestCode ==RC_SIGN_IN){
//                Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
//                try {
//                    GoogleSignInAccount account =task.getResult(ApiException.class);
//                    firebaseAuth(account.getIdToken());
//                } catch (ApiException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }

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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

//                if (TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                progressBar.setVisibility(View.VISIBLE);
//
//                //authenticate user
//                auth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                // If sign in fails, display a message to the user. If sign in succeeds
//                                // the auth state listener will be notified and logic to handle the
//                                // signed in user can be handled in the listener.
//                                progressBar.setVisibility(View.GONE);
//                                if (!task.isSuccessful()) {
//                                    // there was an error
//                                    if (password.length() < 6) {
//                                        inputPassword.setError(getString(R.string.minimum_password));
//                                    } else {
//                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//                                    }
//                                } else {
//                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }
//                        });

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!password.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class ));
                                        finish();

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
                Log.e("SIGNUP",String.valueOf(auth.getCurrentUser()));
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

                            GoogleSignInUsers users = new GoogleSignInUsers();
                            users.setUserId(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setProfile(user.getPhotoUrl().toString());

                            firebaseDatabase.getReference().child("GoogleSignInUsers").child(user.getUid()).setValue(users);
                            Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
                            startActivity(intent);
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
