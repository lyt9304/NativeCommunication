package com.nativecommunication.citypicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nativecommunication.R;
import com.nativecommunication.citypicker.adapter.decoration.GridItemDecoration;
import com.nativecommunication.citypicker.model.City;
import com.nativecommunication.citypicker.model.HotCity;

import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_HOT     = 11;

    private Context mContext;
    private List<City> mData;
    private List<HotCity> mHotData;
    private List<String> mSelected;
    private InnerListener mInnerListener;
    private LinearLayoutManager mLayoutManager;
    private GridListAdapter mHotCitiesAdapter;

    public CityListAdapter(Context context, List<City> data, List<HotCity> hotData, List<String> selected) {
        this.mData = data;
        this.mContext = context;
        this.mHotData = hotData;
        this.mSelected = selected;
    }

    public void setLayoutManager(LinearLayoutManager manager){
        this.mLayoutManager = manager;
    }

    public void updateSelected(List<String> selected) {
        this.mSelected = selected;
        notifyDataSetChanged();
    }

    public void scrollToSection(String index){
        if (mData == null || mData.isEmpty()) return;
        if (TextUtils.isEmpty(index)) return;
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(index.substring(0, 1), mData.get(i).getSection().substring(0, 1))){
                if (mLayoutManager != null){
                    mLayoutManager.scrollToPositionWithOffset(i, 0);
                    return;
                }
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case VIEW_TYPE_HOT:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_hot_layout, parent, false);
                return new HotViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_default_layout, parent, false);
                return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder == null) return;
        if (holder instanceof DefaultViewHolder){
            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;

            ((DefaultViewHolder)holder).name.setText(data.getName());

            if (mSelected.contains(data.getName())) {
                ((DefaultViewHolder)holder).name.setTextColor(Color.parseColor("#EE5045"));
            } else {
                ((DefaultViewHolder)holder).name.setTextColor(Color.parseColor("#666666"));
            }

            ((DefaultViewHolder) holder).name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInnerListener != null){
                        mInnerListener.dismiss(pos, data);
                    }
                }
            });
        }

        if (holder instanceof HotViewHolder){
            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;
            GridListAdapter mAdapter = new GridListAdapter(mContext, mHotData, mSelected);
            mAdapter.setInnerListener(mInnerListener);
            mHotCitiesAdapter = mAdapter;
            ((HotViewHolder) holder).mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
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
        RecyclerView mRecyclerView;

        HotViewHolder(View itemView) {
            super(itemView);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.cp_hot_list);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(),
                    GridListAdapter.SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
            int space = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
            mRecyclerView.addItemDecoration(new GridItemDecoration(GridListAdapter.SPAN_COUNT,
                    space));
        }
    }
}
