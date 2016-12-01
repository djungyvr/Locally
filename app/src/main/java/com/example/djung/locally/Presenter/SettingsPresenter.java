package com.example.djung.locally.Presenter;

import android.app.Activity;

import com.example.djung.locally.R;
import com.example.djung.locally.View.SettingsView;

/**
 * Created by Andy Lin on 2016-11-29.
 */

public class SettingsPresenter {
    private Activity activity;
    private SettingsView settingsView;

    public SettingsPresenter (Activity activity, SettingsView settingsView){
        this.activity = activity;
        this.settingsView = settingsView;
    }

    public void setActionBar(){
        settingsView.setActionBarTitle("Settings");
    }

    public void setNavDrawerSelectedItem(){
        settingsView.setNavDrawerSelectedItem(R.id.nav_manage);
    }
}
