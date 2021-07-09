package com.todobom.queenscanner.Model;

public class CountryItem {
    private String mCountryName;
    private int mFlagImage;

    public CountryItem(String countryName, int flagImage) {
        mCountryName = countryName;
        mFlagImage = flagImage;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public int getFlagImage() {
        return mFlagImage;
    }
}