package com.example.djung.locally.View;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCard {

    private String marketName;

    private String distance;

    private int imageResource;

    public MarketCard(String marketName, String distance, int imageResource) {
        this.marketName = marketName;
        this.imageResource = imageResource;
        this.distance = distance;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getImageResource() {
        return imageResource;
    }

}
