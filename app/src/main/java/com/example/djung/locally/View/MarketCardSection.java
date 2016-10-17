package com.example.djung.locally.View;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardSection {

    private String sectionTitle;
    private ArrayList<MarketCard> marketCardArrayList;


    public MarketCardSection() {

    }

    public MarketCardSection(String sectionTitle, ArrayList<MarketCard> marketCardArrayList) {
        this.sectionTitle = sectionTitle;
        this.marketCardArrayList = marketCardArrayList;
    }

    public ArrayList<MarketCard> getMarketCardArrayList() {
        return marketCardArrayList;
    }

    public void setMarketCardArrayList(ArrayList<MarketCard> marketCardArrayList) {
        this.marketCardArrayList = marketCardArrayList;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }
}
