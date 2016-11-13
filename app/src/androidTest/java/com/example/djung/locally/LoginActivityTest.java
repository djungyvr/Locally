package com.example.djung.locally;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

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
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests the login activity
 *
 * Created by David Jung on 09/11/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginActivityTest {
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
        Espresso.registerIdlingResources(mActivityRule.getActivity().getIdlingResource());

        // Type username.
        onView(withId(R.id.edit_text_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type password.
        onView(withId(R.id.edit_text_password)).perform(typeText("Test123"),
                closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());

        onView(withText(containsString("Sign-in failed"))).check(matches(withText(containsString("Sign-in failed"))));
    }

    @Test
    public void failedLoginBadUsernameTest() {
        Espresso.registerIdlingResources(mActivityRule.getActivity().getIdlingResource());

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
        Espresso.registerIdlingResources(mActivityRule.getActivity().getIdlingResource());

        // Type username.
        onView(withId(R.id.edit_text_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type password.
        onView(withId(R.id.edit_text_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());


        try {
            onView(withText(containsString("Sign-in successful!"))).check(matches(withText(containsString("Sign-in successful!"))));
        } catch(NoMatchingViewException e) {
            onView(withId(R.id.fab_save_vendor_list)).check(matches(isDisplayed()));
        }

    }
}
