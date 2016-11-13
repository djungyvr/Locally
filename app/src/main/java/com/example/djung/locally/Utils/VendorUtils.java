package com.example.djung.locally.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for vendors
 *
 * Created by David Jung on 10/11/16.
 */

public class VendorUtils {

    /**
     * Filters out the PLACEHOLDER text required by AWS, should be used whenever displaying the vendor
     * item list
     * @param vendorItems list of vendor items
     * @return the vendorItems without the "PLACEHOLDER" element
     */
    public static ArrayList<String> filterPlaceholderText(List<String> vendorItems) {
        ArrayList<String> filteredItems = new ArrayList<>(vendorItems);
        filteredItems.remove("PLACEHOLDER");
        return filteredItems;
    }
}
