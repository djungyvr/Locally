package com.example.djung.locally.View;

/**
 * Created by Angy Chung on 2016-11-11.
 */

public class QuickLinkCard {
    private Integer mImageId;
    private String mHeading;
    private String mSubheading;

    public QuickLinkCard(Integer imageId, String heading, String subheading) {
        mImageId = imageId;
        mHeading = heading;
        mSubheading = subheading;
    }

    public Integer getImageId(){
        return mImageId;
    }

    public String getHeading(){
        return mHeading;
    }

    public String getSubheading(){
        return mSubheading;
    }

}
