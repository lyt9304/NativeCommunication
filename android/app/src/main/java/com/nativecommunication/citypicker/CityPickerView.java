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
import java.util.Collections;
import java.util.List;

public class CityPickerView extends LinearLayout implements InnerListener, SideIndexBar.OnIndexTouchedChangedListener {

    private View mContentView;
    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private boolean loaded;

    // constructor
    public CityPickerView(Context context) {
        super(context);
        this.loaded = false;
        this.setHotCities();
    }


    public void setHotCities() {
        Log.d("lyttest",  "set hot cities");
        mHotCities = new ArrayList<>();
        mHotCities.add(new HotCity("北京", "北京", "101010100", false));
        mHotCities.add(new HotCity("上海", "上海", "101020100", false));
        mHotCities.add(new HotCity("广州", "广东", "101280101", false));
        mHotCities.add(new HotCity("深圳", "广东", "101280601", false));
        mHotCities.add(new HotCity("天津", "天津", "101030100", false));
        mHotCities.add(new HotCity("杭州", "浙江", "101210101", false));
        mHotCities.add(new HotCity("南京", "江苏", "101190101", false));
        mHotCities.add(new HotCity("成都", "四川", "101270101", false));
        mHotCities.add(new HotCity("阿坝", "四川", "101200101", false));
    }

    public void setCities(ReadableArray cities) {
        if (this.loaded) {
            for(int i = 0; i < cities.size(); i++){
                ReadableMap city = cities.getMap(i);
                Boolean selected = city.getBoolean("selected");

                Boolean originalSelected = this.mAllCities.get(i + 1).getSelected();

                if (selected != originalSelected) {
                    mAdapter.updateSelected(i + 1, selected);
                }
            }

        } else {
            List newCities = new ArrayList<>();
            newCities.add(new HotCity("热门城市", "未知", "0", false));
            for(int i = 0; i < cities.size(); i++){
                ReadableMap city = cities.getMap(i);
                String name = city.getString("name");
                String pinyin = city.getString("pinyin");
                Boolean selected = city.getBoolean("selected");
                newCities.add(new City(name, name, pinyin, "101010100", selected));
            }
            this.mAllCities = newCities;
            drawView();
            this.loaded = true;
        }
    }

    public View drawView() {
        mContentView = inflate(getContext(), R.layout.cp_dialog_city_picker, this);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getContext(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()), 1);
        mAdapter = new CityListAdapter(getContext(), mAllCities, mHotCities);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                //确保定位城市能正常刷新
//                if (newState == RecyclerView.SCROLL_STATE_IDLE){
//                    mAdapter.refreshLocationItem();
//                }
//            }
//        });

//        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
//        mOverlayTextView = mContentView.findViewById(R.id.cp_overlay);
//
        mIndexBar = (SideIndexBar) mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);
//
//        mSearchBox = mContentView.findViewById(R.id.cp_search_box);
//        mSearchBox.addTextChangedListener(this);
//
//        mCancelBtn = mContentView.findViewById(R.id.cp_cancel);
//        mClearAllBtn = mContentView.findViewById(R.id.cp_clear_all);
//        mCancelBtn.setOnClickListener(this);
//        mClearAllBtn.setOnClickListener(this);

        return mContentView;
    }

    @Override
    public void dismiss(int position, City data) {
        Log.d("lyttest", "dismiss:" + position + ", " + data.toString());
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