package com.example.djung.locally.View.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.djung.locally.Presenter.SettingsPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Activities.AboutUsActivity;
import com.example.djung.locally.View.Activities.MainActivity;
import com.example.djung.locally.View.Interfaces.SettingsView;

/**
 * Created by Andy Lin on 2016-11-29.
 */

public class SettingsFragment extends android.support.v4.app.Fragment implements SettingsView {
    private SettingsPresenter settingsPresenter;
    private MainActivity mainActivity;
    private LinearLayout contactUsSection;
    private LinearLayout aboutUsSection;

    @Override
    public void setActionBarTitle(String title) {
        mainActivity.setActionBarTitle(title);
        mainActivity.setAppBarElevation(4);
    }

    @Override
    public void setNavDrawerSelectedItem(int resID) {
        mainActivity.setNavigationDrawerCheckedItem(resID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_main, container, false);
        settingsPresenter = new SettingsPresenter(getActivity(), this);
        this.mainActivity = (MainActivity) getActivity();
        this.contactUsSection = (LinearLayout) view.findViewById(R.id.settings_contact_us_button);
        this.aboutUsSection = (LinearLayout) view.findViewById(R.id.settings_about_button);

        contactUsSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Currently no contact us implementation!", Toast.LENGTH_SHORT).show();
            }
        });

        aboutUsSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAboutUsFragment();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        settingsPresenter.setActionBar();
        settingsPresenter.setNavDrawerSelectedItem();
    }

    public void launchAboutUsFragment(){
        Intent aboutUsIntent = new Intent(getActivity(), AboutUsActivity.class);
        startActivity(aboutUsIntent);
    }
}
