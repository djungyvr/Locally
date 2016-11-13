package com.example.djung.locally.View;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCard {

    private String marketName;

    private String marketAddress;

    private String marketHours;

    private String distance;

    private int imageResource;

    public MarketCard(String marketName, String marketAddress, String marketHours, String distance, int imageResource) {
        this.marketName = marketName;
        this.distance = distance;
        this.imageResource = imageResource;
        this.marketAddress = marketAddress;
        this.marketHours = marketHours;
    }

    public String getMarketAddress(){
        return marketAddress;
    }

    public String getMarketHours(){
        return marketHours;
    }

    public String getMarketName() {
        return marketName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getMarketDistance() {
        return distance;
    }
}
