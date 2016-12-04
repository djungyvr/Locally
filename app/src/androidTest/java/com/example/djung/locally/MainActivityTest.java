package com.example.djung.locally;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.djung.locally.View.Activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.contains;

/**
 * Runs tests related to the Main Activity
 *
 * Created by David Jung on 09/11/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    private final int NAV_HOME = 0;
    private final int NAV_MARKET_LIST = 1;
    private final int NAV_GROCERY_LIST = 2;
    private final int NAV_MAP = 3;
    private final int NAV_MANAGE = 4;

    /**
     * Randomly opens drawer items except for the map
     */
    @Test
    public void navigationDrawerTesting() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        int numTries = 20;

        for(int i = 0; i < numTries; i++) {
            // Open Drawer to click on navigation.
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

            int random = Math.abs((int) Calendar.getInstance().getTimeInMillis() % 5);

            switch(random) {
                case NAV_HOME:
                    // Check home screen open
                    onView(withId(R.id.nav_view))
                            .perform(NavigationViewActions.navigateTo(R.id.nav_home));
                    break;
                case NAV_MARKET_LIST:
                    // Check market screen screen open
                    onView(withId(R.id.nav_view))
                            .perform(NavigationViewActions.navigateTo(R.id.nav_market_list));
                    break;
                case NAV_GROCERY_LIST:
                    // Check grocery list screen screen open
                    onView(withId(R.id.nav_view))
                            .perform(NavigationViewActions.navigateTo(R.id.nav_grocery_list));
                    break;
                case NAV_MAP:
                    // Don't do this one since the dialog pops up
//                    // Check grocery list screen screen open
//                    onView(withId(R.id.nav_view))
//                            .perform(NavigationViewActions.navigateTo(R.id.nav_map));
                    break;
                case NAV_MANAGE:
                    // Check grocery list screen screen open
                    onView(withId(R.id.nav_view))
                            .perform(NavigationViewActions.navigateTo(R.id.nav_manage));
                    break;
            }

            onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        }
    }
}
