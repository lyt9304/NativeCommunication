package com.nativecommunication.citypicker.model;

public class HotCity extends City {

    public HotCity(String name, String province, String code, Boolean selected) {
        super(name, province, "热门城市", code, selected);
    }
}