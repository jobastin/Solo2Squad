package com.example.solo2squad.Authentication;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.example.solo2squad.Authentication.LoginActivity;
import com.example.solo2squad.Authentication.MainActivity;
import com.example.solo2squad.DashboardActivity;
import com.example.solo2squad.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testCheckIfUserLoggedIn() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity.class);

        // Assuming getStartedbtn is the id of the button in your MainActivity
        Espresso.onView(ViewMatchers.withId(R.id.getStartedbtn)).perform(ViewActions.click());

        // Check if LoginActivity is launched
        Espresso.onView(ViewMatchers.withId(R.id.login_activity_root_layout)) // replace with the root layout id of LoginActivity
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


    @Test
    public void testNavigateToLoginActivity() {
        // Assuming getStartedbtn is the id of the TextView in your MainActivity
        Espresso.onView(ViewMatchers.withId(R.id.getStartedbtn)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testSplashScreen() {
        // Assuming getStartedbtn is the id of the button in your splash screen
        Espresso.onView(ViewMatchers.withId(R.id.getStartedbtn)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));

    }

    @Test
    public void testLoginActivity() {
        // Assuming login_email_txt, login_password_txt, and btn_login are the ids of the relevant UI elements
        Espresso.onView(ViewMatchers.withId(R.id.login_email_txt)).perform(ViewActions.typeText("adhinbabu1998@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.login_password_txt)).perform(ViewActions.typeText("123456"));
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click());

        // You can add assertions here based on the expected behavior of your app after login.
        // For example, you can check if the DashboardActivity is launched.
        Intents.intended(IntentMatchers.hasComponent(DashboardActivity.class.getName()));
    }
}