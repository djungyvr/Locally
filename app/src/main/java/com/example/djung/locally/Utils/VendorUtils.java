package com.example.djung.locally.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.djung.locally.AWS.AwsConfiguration;

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

    /**
     * Construct the URL of the vendor image
     * @param vendorName name of the vendor
     * @param marketName name of the market
     * @return url to the vendor image
     */
    public static String getS3Url(String marketName, String vendorName) {
        marketName = marketName.replace(' ','_');
        vendorName = vendorName.replace(' ','_');

        return  AwsConfiguration.AMAZON_S3_VENDOR_IMAGE + marketName + '-' + vendorName + ".jpg";
    }

    /**
     * Construct the unique filename of the vendor image as .jpg
     * @param vendorName name of the vendor
     * @param marketName name of the market
     * @return filename of the vendor image
     */
    public static String getS3FileNameJpeg(String marketName, String vendorName) {
        marketName = marketName.replace(' ','_');
        vendorName = vendorName.replace(' ','_');

        return  marketName + '-' + vendorName + ".jpg";
    }
}
