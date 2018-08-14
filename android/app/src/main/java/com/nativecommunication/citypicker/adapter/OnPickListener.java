package com.nativecommunication.citypicker.adapter;


import com.nativecommunication.citypicker.model.City;

public interface OnPickListener {
    void onPick(int position, City data);
    void onLocate();
}
