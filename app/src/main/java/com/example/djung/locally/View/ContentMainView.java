package com.example.djung.locally.View;

import com.example.djung.locally.View.Adapters.ContentMainAdapter;
import com.example.djung.locally.View.Adapters.SuggestionAdapter;

/**
 * Created by Angy Chung on 2016-11-27.
 */

public interface ContentMainView {
    void setActionBarTitle(String title);
    void showContentMain(ContentMainAdapter adapter);
    void showSearchSuggestions(SuggestionAdapter adapter);
    void clearSearchFocus();

}
