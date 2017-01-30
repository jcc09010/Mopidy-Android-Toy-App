package com.sideprojects.work.mopifyandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.sideprojects.work.mopifyandroid.mopidy.MopidyService;

import java.util.Stack;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by work on 1/28/17.
 */

public class BrowseFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private BrowseItemAdapter mItemAdapter;
    private ProgressBar mLoadingBar;
    private Button mButtonRootMenu;
    private Button mButtonBack;

    private Stack<MopidyService.BrowseItem> mNavigationStack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationStack = new Stack<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_browse, container, false);
        mButtonRootMenu = (Button) root.findViewById(R.id.button_root_menu);
        mButtonBack = (Button) root.findViewById(R.id.button_back);
        mLoadingBar = (ProgressBar) root.findViewById(R.id.loading_bar);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mItemAdapter = new BrowseItemAdapter();
        mRecyclerView.setAdapter(mItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonRootMenu.setOnClickListener(buttonView -> {
            Log.e("DEBUG", "Querying Root Menu");
            mNavigationStack.clear();
            MopidyService.browse(null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(()->showProgress(true))
                    .doOnNext(response->showProgress(false))
                    .subscribe(response -> {
                        mNavigationStack.add(null);
                        mItemAdapter.setItems(response.results);
                        mItemAdapter.notifyDataSetChanged();
                    }, Throwable::printStackTrace);
        });

        mItemAdapter.setItemClickListener(item ->{
            Log.e("DEBUG", "Querying URI : " + item.uri);
            MopidyService.browse(item)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(()->showProgress(true))
                    .doOnNext(response->showProgress(false))
                    .subscribe(response -> {
                        mNavigationStack.add(item);
                        mItemAdapter.setItems(response.results);
                        mItemAdapter.notifyDataSetChanged();
                    }, Throwable::printStackTrace);
        });

        mButtonBack.setOnClickListener(buttonView -> {
            if(!mNavigationStack.isEmpty()){
                mNavigationStack.pop();
            }
            MopidyService.BrowseItem item =
                    !mNavigationStack.isEmpty() ? mNavigationStack.pop() : null;
            Log.e("DEBUG", "Querying URI : " + (item != null ? item.uri : null));
            MopidyService.browse(item)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(()->showProgress(true))
                    .doOnNext(response->showProgress(false))
                    .subscribe(response ->{
                        mItemAdapter.setItems(response.results);
                        mItemAdapter.notifyDataSetChanged();
                    }, Throwable::printStackTrace);
        });
    }

    private void showProgress(boolean show){
        if(show){
            mLoadingBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
