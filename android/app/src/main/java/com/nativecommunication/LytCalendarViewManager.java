package com.nativecommunication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.nativecommunication.citypicker.CityPickerView;

public class LytCalendarViewManager extends SimpleViewManager<CityPickerView> {
    public static final String REACT_CLASS = "LytCalendarView";

    public static final String PROP_CITIES = "cities";
    public static final String PROP_SELECTED = "selected";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactProp(name=PROP_CITIES)
    public void setCities(CityPickerView cityPickerView, ReadableArray cities) {
        cityPickerView.setCities(cities);
    }

//    @ReactProp(name=PROP_SELECTED)
//    public void setSelected(CityPickerView cityPickerView, ReadableArray selected) {
//        cityPickerView.setSelected(selected);
//    }
//

    @Override
    protected CityPickerView createViewInstance(ThemedReactContext reactContext) {
        return new CityPickerView(reactContext);
    }
}
