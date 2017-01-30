package com.sideprojects.work.mopifyandroid;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sideprojects.work.mopifyandroid.mopidy.MopidyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by work on 1/29/17.
 */

public class BrowseItemAdapter extends RecyclerView.Adapter<BrowseItemAdapter.ItemHolder>{

    public interface ItemClickListener {
        void onItemClicked(MopidyService.BrowseItem item);
    }
    private ItemClickListener mClickListener;

    private List<MopidyService.BrowseItem> mItems;

    public BrowseItemAdapter(){
        mItems = new ArrayList<>();
    }

    public void setItems(List<MopidyService.BrowseItem> items){
        mItems.clear();
        if(items != null && !items.isEmpty()) {
            mItems.addAll(items);
        }
    }

    public void clearItems(){
        mItems.clear();
    }

    public void setItemClickListener(ItemClickListener listener){
        mClickListener = listener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.row_item_browse, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if(position > 0 && position < mItems.size()){
            holder.bind(mItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitle;

        public ItemHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTitle.setOnClickListener(this);
        }

        public void bind(MopidyService.BrowseItem item){
            mTitle.setText(item.name);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null){
                if(getAdapterPosition() > 0 && getAdapterPosition() < mItems.size()){
                    mClickListener.onItemClicked(mItems.get(getAdapterPosition()));
                }
            }
        }
    }
}
