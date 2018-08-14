package com.nativecommunication.citypicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.nativecommunication.R;
import com.nativecommunication.citypicker.adapter.decoration.GridItemDecoration;
import com.nativecommunication.citypicker.model.City;
import com.nativecommunication.citypicker.model.HotCity;

import org.w3c.dom.Text;

import java.util.List;

import static java.security.AccessController.getContext;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_CURRENT = 10;
    private static final int VIEW_TYPE_HOT     = 11;

    private Context mContext;
    private List<City> mData;
    private List<HotCity> mHotData;
    private InnerListener mInnerListener;
    private LinearLayoutManager mLayoutManager;
    private boolean stateChanged;
    private RecyclerView mRecyclerView;
    private GridListAdapter mHotCitiesAdapter;

    public CityListAdapter(Context context, List<City> data, List<HotCity> hotData) {
        this.mData = data;
        this.mContext = context;
        this.mHotData = hotData;
    }

    public void setLayoutManager(LinearLayoutManager manager){
        this.mLayoutManager = manager;
    }

    public void updateData(List<City> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public void updateSelected(int index, boolean selected) {
        City data = this.mData.get(index);
        data.setSelected(selected);
        notifyItemChanged(index);

        for (int i = 0; i < this.mHotData.size(); i++) {
            HotCity hData = this.mHotData.get(i);
            Log.d("lyttest", "hData1: " + i + ": " + data.getName() +": " + hData.getName());

            if (TextUtils.equals(data.getName(), hData.getName())) {
                Log.d("lyttest", "hData2: " + i + ": " + selected +": " + hData.getName());
                hData.setSelected(selected);
                mHotCitiesAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public void refreshLocationItem(){
        //如果定位城市的item可见则进行刷新
        if (stateChanged && mLayoutManager.findFirstVisibleItemPosition() == 0) {
            stateChanged = false;
            notifyItemChanged(0);
        }
    }

    /**
     * 滚动RecyclerView到索引位置
     * @param index
     */
    public void scrollToSection(String index){
        Log.d("lyttest", "scrollToSection: " + index);
        if (mData == null || mData.isEmpty()) return;
        if (TextUtils.isEmpty(index)) return;
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(index.substring(0, 1), mData.get(i).getSection().substring(0, 1))){
                if (mLayoutManager != null){
                    Log.d("lyttest", "scroll to index: " + i + "," + mData.get(i).getName());
                    mLayoutManager.scrollToPositionWithOffset(i, 0);
//                    mRecyclerView.scrollToPosition(i);
                    return;
                }
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case VIEW_TYPE_CURRENT:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_location_layout, parent, false);
                return new LocationViewHolder(view);
            case VIEW_TYPE_HOT:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_hot_layout, parent, false);
                Log.d("lyttest", "VIEW_TYPE_HOT inflater");
                return new HotViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_default_layout, parent, false);
                return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
//        Log.d("lytest", "onBindViewHolder: " + position);
        if (holder == null) return;
        if (holder instanceof DefaultViewHolder){
            Log.d("lytest", "onBindViewHolder(default): " + position);

            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;

            ((DefaultViewHolder)holder).name.setText(data.getName());

            if (data.getSelected()) {
                ((DefaultViewHolder)holder).name.setTextColor(Color.parseColor("#EE5045"));
            } else {
                ((DefaultViewHolder)holder).name.setTextColor(Color.parseColor("#666666"));
            }

            ((DefaultViewHolder) holder).name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("lyttest", "clicked");
                    if (mInnerListener != null){
                        mInnerListener.dismiss(pos, data);
                    }
                }
            });
        }

        //热门城市
        if (holder instanceof HotViewHolder){
            Log.d("lytest", "onBindViewHolder(hot): " + position);
//            int size = mHotData.size();
//            for (int i = 0; i < size; i++) {
//                HotCity data = mHotData.get(i);
////                TextView view = new TextView(mContext);
////                view.setText(data.getName());
//                View v = LayoutInflater.from(mContext).inflate(R.layout.cp_grid_item_layout, null);
//                ((TextView) v.findViewById(R.id.cp_gird_item_name)).setText(data.getName());
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                params.weight = 1.0f;
//                params.gravity = Gravity.TOP;
//
//                v.setLayoutParams(params);
//                ((HotViewHolder) holder).container.addView(v);
//            }

            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;
            GridListAdapter mAdapter = new GridListAdapter(mContext, mHotData);
            mAdapter.setInnerListener(mInnerListener);
            this.mHotCitiesAdapter = mAdapter;
            ((HotViewHolder) holder).mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("lyttest",  position + ": " + mData.get(position).getSection().substring(0, 1));
        if (position == 0 && TextUtils.equals("定", mData.get(position).getSection().substring(0, 1)))
            return VIEW_TYPE_CURRENT;
        if (position == 0 && TextUtils.equals("热", mData.get(position).getSection().substring(0, 1)))
            return VIEW_TYPE_HOT;
        return super.getItemViewType(position);
    }

    public void setInnerListener(InnerListener listener){
        this.mInnerListener = listener;
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder{
        BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DefaultViewHolder extends BaseViewHolder{
        TextView name;

        DefaultViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.cp_list_item_name);
        }
    }

    public static class HotViewHolder extends BaseViewHolder {
//        GridLayout container;
        RecyclerView mRecyclerView;

        HotViewHolder(View itemView) {
            super(itemView);
//            container = (GridLayout) itemView.findViewById(R.id.cp_hot_list);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.cp_hot_list);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(),
                    GridListAdapter.SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
            int space = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
            mRecyclerView.addItemDecoration(new GridItemDecoration(GridListAdapter.SPAN_COUNT,
                    space));
            Log.d("lyttest", itemView.toString());
        }
    }

    public static class LocationViewHolder extends BaseViewHolder {
        FrameLayout container;
        TextView current;

        LocationViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView.findViewById(R.id.cp_list_item_location_layout);
            current = (TextView) itemView.findViewById(R.id.cp_list_item_location);
        }
    }
}
