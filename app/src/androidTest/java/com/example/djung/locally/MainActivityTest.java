package com.example.djung.locally;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.djung.locally.HelperAction.RecyclerViewMatcher;
import com.example.djung.locally.HelperAction.ToolbarMatcher;
import com.example.djung.locally.View.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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

    /**
     * Tests the quick links thumbnails
     */
    @Test
    public void testQuickLinks() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_home));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        // check thumbnails have correct titles
        RecyclerViewMatcher rv = new RecyclerViewMatcher(R.id.recycler_view_quick_links_list);
        onView(rv.atPosition(0)).check(matches(hasDescendant(withText("All Markets"))));
        onView(rv.atPosition(1)).check(matches(hasDescendant(withText("Map"))));
        onView(rv.atPosition(2)).check(matches(hasDescendant(withText("In Season Produce"))));
        onView(rv.atPosition(3)).check(matches(hasDescendant(withText("Your Grocery List"))));

        // try opening each one
        int numTries = 20;

        for (int i=0; i<numTries; ++i) {
            int random = Math.abs((int) Calendar.getInstance().getTimeInMillis() % 4);

            switch(random) {
                case 0: // try Market List
                    onView(rv.atPosition(0)).perform(click());
                    ToolbarMatcher.matchToolbarTitle("Market List");
                    Espresso.pressBack();       // go back to Home
                    break;
                case 1: // Map - skip
                    break;
                case 2: // TODO: In season
                    break;
                case 3: // grocery list
                    onView(rv.atPosition(3)).perform(click());
                    ToolbarMatcher.matchToolbarTitle("Your Grocery List");
                   // Espresso.pressBack();   // clear search view focus
                    Espresso.pressBack();       // go back to Home
                    break;
            }

            ToolbarMatcher.matchToolbarTitle("Locally");
        }

    }
}

