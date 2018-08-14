package com.nativecommunication;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.nativecommunication.citypicker.CityPickerView;

public class LytCalendarViewManager extends SimpleViewManager<CityPickerView> {
    public static final String REACT_CLASS = "LytCalendarView";

    public static final String PROP_CITIES = "cities";
    public static final String PROP_HOT_CITIES = "hotCities";
    public static final String PROP_SELECTED = "selected";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name=PROP_CITIES)
    public void setCities(CityPickerView cityPickerView, ReadableArray cities) {
        cityPickerView.setCities(cities);
    }

    @ReactProp(name=PROP_HOT_CITIES)
    public void setHotCities(CityPickerView cityPickerView, ReadableArray hotCities) {
        cityPickerView.setHotCities(hotCities);
    }

    @ReactProp(name=PROP_SELECTED)
    public void setSelected(CityPickerView cityPickerView, ReadableArray selected) {
        cityPickerView.setSelected(selected);
    }

    @Override
    protected CityPickerView createViewInstance(ThemedReactContext reactContext) {
        return new CityPickerView(reactContext);
    }
}
