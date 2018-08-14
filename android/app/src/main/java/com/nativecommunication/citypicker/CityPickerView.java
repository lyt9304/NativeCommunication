package com.nativecommunication.citypicker;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.nativecommunication.R;
import com.nativecommunication.citypicker.adapter.CityListAdapter;
import com.nativecommunication.citypicker.adapter.InnerListener;
import com.nativecommunication.citypicker.adapter.decoration.DividerItemDecoration;
import com.nativecommunication.citypicker.adapter.decoration.SectionItemDecoration;
import com.nativecommunication.citypicker.model.City;
import com.nativecommunication.citypicker.model.HotCity;
import com.nativecommunication.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

public class CityPickerView extends LinearLayout implements InnerListener, SideIndexBar.OnIndexTouchedChangedListener {

    private View mContentView;
    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private List<String> mSelected;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private boolean loaded;

    public CityPickerView(Context context) {
        super(context);
        this.loaded = false;
    }


    public void setHotCities(ReadableArray hotCities) {
        mHotCities = new ArrayList<>();
        for(int i = 0; i < hotCities.size(); i++){
            String name = hotCities.getString(i);
            mHotCities.add(new HotCity(name));
        }
    }

    public void setSelected(ReadableArray selected) {
        mSelected = new ArrayList<String>();
        for (int i = 0; i < selected.size(); i++) {
            mSelected.add(selected.getString(i));
        }
        if (loaded) {
            mAdapter.updateSelected(mSelected);
        }
    }

    public void setCities(ReadableArray cities) {
        mAllCities = new ArrayList<>();
        mAllCities.add(new HotCity("热门城市"));
        for(int i = 0; i < cities.size(); i++){
            ReadableMap city = cities.getMap(i);
            String name = city.getString("name");
            String pinyin = city.getString("pinyin");
            mAllCities.add(new City(name, pinyin));
        }
        drawView();
        this.loaded = true;
    }

    public View drawView() {
        mContentView = inflate(getContext(), R.layout.cp_dialog_city_picker, this);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getContext(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()), 1);
        mAdapter = new CityListAdapter(getContext(), mAllCities, mHotCities, mSelected);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mOverlayTextView = (TextView) mContentView.findViewById(R.id.cp_overlay);

        mIndexBar = (SideIndexBar) mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);

        return mContentView;
    }

    @Override
    public void dismiss(int position, City data) {
        WritableMap event = Arguments.createMap();
        event.putString("city", data.getName());
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event);
    }

    @Override
    public void onIndexChanged(String index, int position) {
        mAdapter.scrollToSection(index);
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(measureAndLayout);
    }
}