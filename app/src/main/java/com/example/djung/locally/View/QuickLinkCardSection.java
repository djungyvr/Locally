package com.example.djung.locally.View;

import java.util.ArrayList;

/**
 * Created by Angy Chung on 2016-11-12.
 */

public class QuickLinkCardSection {
    private ArrayList<QuickLinkCard> mQuickLinkCards;

    public QuickLinkCardSection(ArrayList<QuickLinkCard> quickLinkCards) {
        mQuickLinkCards = quickLinkCards;
    }

    public ArrayList<QuickLinkCard> getThumbnailList() {
        return mQuickLinkCards;
    }

    public void setmThumbnailList(ArrayList<QuickLinkCard> quickLinkCards) {
        mQuickLinkCards = quickLinkCards;
    }

}
