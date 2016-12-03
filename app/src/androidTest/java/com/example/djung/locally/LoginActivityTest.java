package com.example.djung.locally;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.widget.EditText;

import com.example.djung.locally.HelperAction.CustomViewAction;
import com.example.djung.locally.View.LoginActivity;
import com.example.djung.locally.View.VendorActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests the login activity
 *
 * PLEASE READ BEFORE TESTING :
 * Before running these tests ensure any users are signed out of the app
 *
 * Created by David Jung on 09/11/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginActivityTest {
    private final String TAG = "LoginTest";
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void invalidUsernameTest() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_username)).perform(typeText(""),
                closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());

        // Check if message displays
        onView(withId(R.id.text_view_username_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void invalidPasswordTest() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_username)).perform(typeText("username"),
                closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());

        // Check if message displays
        onView(withId(R.id.text_view_password_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void failedLoginBadPasswordTest() {
        // Type username.
        onView(withId(R.id.edit_text_username)).perform(typeText("test2"),
                closeSoftKeyboard());
        // Type password.
        onView(withId(R.id.edit_text_password)).perform(typeText("Test123"),
                closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());

        onView(withText(containsString("Sign-in failed"))).check(matches(withText(containsString("Sign-in failed"))));
    }

    @Test
    public void failedLoginBadUsernameTest() {
        // Type username.
        onView(withId(R.id.edit_text_username)).perform(typeText("ThisUserDoesNotExist"),
                closeSoftKeyboard());
        // Type password.
        onView(withId(R.id.edit_text_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());

        onView(withText(containsString("Sign-in failed"))).check(matches(withText(containsString("Sign-in failed"))));
    }

    // Should be called after failed logins
    // Should sign out after each test
    @Test
    public void successfulLoginTest() {
        // Type username.
        onView(withId(R.id.edit_text_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type password.
        onView(withId(R.id.edit_text_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());


        try {
            onView(withText(containsString("Sign-in successful!"))).check(matches(withText(containsString("Sign-in successful!"))));
            onView(withId(R.id.nav_signout)).perform(click());
        } catch (NoMatchingViewException e) {
            try {
                // Too many login attempts
                onView(withText(containsString("Sign-in failed"))).check(matches(withText(containsString("Sign-in failed"))));
            } catch(NoMatchingViewException e1) {
                onView(withId(R.id.fab_save_vendor_list)).check(matches(isDisplayed()));
            }
        }
    }

    private void login() {
        // Type username.
        onView(withId(R.id.edit_text_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type password.
        onView(withId(R.id.edit_text_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());
    }

    // Should be called after failed logins
    // Should sign out after each test
    @Test
    public void testAddItem() {

        login();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Add crab apples
        onView(isAssignableFrom(EditText.class)).perform(typeText("crab appl"),closeSoftKeyboard());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(containsString("crab appl"))).perform(click());
    }
}
