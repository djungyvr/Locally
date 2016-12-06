package com.example.djung.locally;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.djung.locally.View.Activities.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.djung.locally.HelperAction.NestedScrollToAction.betterScrollTo;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsString;

/**
 * Tests the registration process
 *
 * Created by David Jung on 25/11/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterTest {
    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void invalidUsernameTest() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText(""),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());

        // Check if message displays
        onView(withId(R.id.text_view_reg_username_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void invalidPasswordTest() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText(""),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());

        // Check if message displays
        onView(withId(R.id.text_view_reg_password_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void failedRegistrationInvalidPassword() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("Test1234"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_email)).perform(typeText("test@test.com"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_vendor_name)).perform(typeText("vendorName"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_phone)).perform(typeText("1111111111"),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());

        // Check if message displays
        //onView(withId(R.id.text_view_reg_username_message)).check(matches(withText(containsString("failed"))));
        onView(withText(containsString("Sign up failed"))).check(matches(withText(containsString("failed"))));
    }

    @Test
    public void failedRegistrationNoEmail() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("NoEmailUser"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_vendor_name)).perform(typeText("vendorName"),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());

        // Check if message displays
        onView(withId(R.id.text_view_reg_email_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void failedRegistrationNoVendorName() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("NoEmailUser"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());

        // Check if message displays
        onView(withId(R.id.text_view_reg_vendor_name_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void failedRegistrationNoPhoneNumber() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("NoEmailUser"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_email)).perform(typeText("test@test.com"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_vendor_name)).perform(typeText("vendorName"),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());
        // Check if message displays
        onView(withId(R.id.text_view_reg_phone_message)).check(matches(withText(containsString("empty"))));
    }

    @Test
    public void failedRegistrationExistingUser() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("test"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_vendor_name)).perform(typeText("vendorName"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_email)).perform(typeText("test@test.com"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_phone)).perform(typeText("1111111111"),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());
        // Check if message displays
        onView(withText(containsString("Sign up failed"))).check(matches(withText(containsString("failed"))));
    }

    @Test
    public void failedRegistrationExistingVendorName() {
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_username)).perform(typeText("repeatVendorName2"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_password)).perform(typeText("Test1234!"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_vendor_name)).perform(typeText("Vendor Name"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_email)).perform(typeText("test@test.com"),
                closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.edit_text_reg_phone)).perform(typeText("1111111111"),
                closeSoftKeyboard());
        onView(withId(R.id.button_reg_signup)).perform(betterScrollTo(),click());
        // Check if message displays
        onView(withText(containsString("Sign up failed"))).check(matches(withText(containsString("failed"))));
    }
}
