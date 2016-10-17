package com.example.djung.locally.View;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardViewModelSection {

    private String sectionTitle;
    private ArrayList<MarketCardViewModel> marketCardViewModelArrayList;


    public MarketCardViewModelSection() {

    }

    public MarketCardViewModelSection(String sectionTitle, ArrayList<MarketCardViewModel> marketCardViewModelArrayList) {
        this.sectionTitle = sectionTitle;
        this.marketCardViewModelArrayList = marketCardViewModelArrayList;
    }

    public ArrayList<MarketCardViewModel> getMarketCardViewModelArrayList() {
        return marketCardViewModelArrayList;
    }

    public void setMarketCardViewModelArrayList(ArrayList<MarketCardViewModel> marketCardViewModelArrayList) {
        this.marketCardViewModelArrayList = marketCardViewModelArrayList;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }
}
