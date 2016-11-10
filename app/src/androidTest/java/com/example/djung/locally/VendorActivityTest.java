package com.example.djung.locally;

import android.content.Intent;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.djung.locally.View.LoginActivity;
import com.example.djung.locally.View.VendorActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;

/**
 * Runs tests related to Vendor Activity
 *
 * Created by David Jung on 09/11/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class VendorActivityTest {
    @Rule
    public ActivityTestRule<VendorActivity> mActivityRule =
            new ActivityTestRule<>(VendorActivity.class);
}
