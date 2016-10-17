package com.example.djung.locally.View;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardViewModel {

    private String marketName;



    public MarketCardViewModel(String marketName) {
        this.marketName = marketName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
}
