package com.example.djung.locally.View;

import android.content.Context;

/**
 * Created by Angy Chung on 2016-11-19.
 */

public class EnablePermissionsCard {
    private String mRationale;
    private String mButtonText;

    public EnablePermissionsCard(String rationale, String buttonText) {
        this.mRationale = rationale;
        this.mButtonText = buttonText;
    }

    public String getRationale() {
        return mRationale;
    }

    public String getmButtonText() {
        return mButtonText;
    }
}
