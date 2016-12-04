package com.example.djung.locally;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.djung.locally.View.Activities.VendorActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.contains;

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
