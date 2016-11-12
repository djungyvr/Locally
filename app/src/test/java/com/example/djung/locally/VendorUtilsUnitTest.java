package com.example.djung.locally;

import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.Utils.VendorUtils;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the functions in VendorUtils
 *
 * Created by David Jung on 10/11/16.
 */

public class VendorUtilsUnitTest {

    @Test
    public void testFilterPlaceholderText() {
        List<String> itemsBeforeFilter = new ArrayList<>();
        itemsBeforeFilter.add("PLACEHOLDER");
        itemsBeforeFilter.add("Apples");
        itemsBeforeFilter.add("Pears");
        List<String> itemsAfterFilter = new ArrayList<>();
        itemsAfterFilter.add("Apples");
        itemsAfterFilter.add("Pears");

        // Test for filter
        assertThat(itemsAfterFilter, Is.<List<String>>is(VendorUtils.filterPlaceholderText(itemsBeforeFilter)));

        // Test for filter of empty list
        assertThat(new ArrayList<String>(), Is.<List<String>>is(VendorUtils.filterPlaceholderText(new ArrayList<String>())));

        itemsBeforeFilter = new ArrayList<>();
        itemsBeforeFilter.add("PLACEHOLDER");

        // Test for filter of empty list
        assertThat(new ArrayList<String>(), Is.<List<String>>is(VendorUtils.filterPlaceholderText(itemsBeforeFilter)));
    }
}
