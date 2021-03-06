package com.bfourclass.smartbooking.external_api.instance.numbeo_data;

public class CostOfLivingStatistics {
    /*
    * Every attribute's value is an estimation of the price, with values in local currency.
     */
    private String monthlyPersonCost;

    // Prices at market
    private String domesticBeerPrice;
    private String waterPrice;
    private String cigarettesPrice;

    // Transportation prices
    private String busTicketPrice;
    private String taxiKmPrice;
    private String gasolinePrice;

    public CostOfLivingStatistics() {
        monthlyPersonCost = null;
        domesticBeerPrice = null;
        waterPrice = null;
        cigarettesPrice = null;
        busTicketPrice = null;
        taxiKmPrice = null;
        gasolinePrice = null;
    }

    public String getMonthlyPersonCost() {
        return monthlyPersonCost;
    }

    public void setMonthlyPersonCost(String monthlyPersonCost) {
        this.monthlyPersonCost = monthlyPersonCost;
    }

    public String getDomesticBeerPrice() {
        return domesticBeerPrice;
    }

    public void setDomesticBeerPrice(String domesticBeerPrice) {
        this.domesticBeerPrice = domesticBeerPrice;
    }

    public String getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(String waterPrice) {
        this.waterPrice = waterPrice;
    }

    public String getCigarettesPrice() {
        return cigarettesPrice;
    }

    public void setCigarettesPrice(String cigarettesPrice) {
        this.cigarettesPrice = cigarettesPrice;
    }

    public String getBusTicketPrice() {
        return busTicketPrice;
    }

    public void setBusTicketPrice(String busTicketPrice) {
        this.busTicketPrice = busTicketPrice;
    }

    public String getTaxiKmPrice() {
        return taxiKmPrice;
    }

    public void setTaxiKmPrice(String taxiKmPrice) {
        this.taxiKmPrice = taxiKmPrice;
    }

    public String getGasolinePrice() {
        return gasolinePrice;
    }

    public void setGasolinePrice(String gasolinePrice) {
        this.gasolinePrice = gasolinePrice;
    }
}
