package com.example.solo2squad;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.solo2squad.Authentication.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class LoginActivityTest {

    private LoginActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(LoginActivity.class).create().start().resume().get();
    }

    @Test
    public void testButtonClick() {
        // Mock dependencies
        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseUser mockUser = mock(FirebaseUser.class);
        DatabaseReference mockUserRef = mock(DatabaseReference.class);
        Task<Void> mockTask = mock(Task.class);

        // Set up UI components
        EditText inputEmail = activity.findViewById(R.id.login_email_txt);
        EditText inputPassword = activity.findViewById(R.id.login_password_txt);
        ProgressBar progressBar = activity.findViewById(R.id.progressBar);
        Button btnLogin = activity.findViewById(R.id.btn_login);

        // Set input values
        inputEmail.setText("test@example.com");
        inputPassword.setText("test123");

        // Trigger button click
        btnLogin.performClick();

        // TODO: Add assertions based on your expected behavior
        // For example, you might assert that the progress bar is visible after the button click.
        assertEquals(View.VISIBLE, progressBar.getVisibility());
    }
}
