package com.example.djung.locally.View;

import com.example.djung.locally.Model.Market;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardSection {

    private String sectionTitle;
    private ArrayList<Market> marketList;


    public MarketCardSection() {

    }

    public MarketCardSection(String sectionTitle, ArrayList<Market> marketList) {
        this.sectionTitle = sectionTitle;
        this.marketList = marketList;
    }

    public ArrayList<Market> getMarketList() {
        return marketList;
    }

    public void setMarketList(ArrayList<Market> marketCardList) {
        this.marketList = marketCardList;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }
}
