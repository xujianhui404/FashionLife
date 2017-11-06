package com.myfashionlife.ui.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.myfashionlife.R;
import com.myfashionlife.app.APPConstant;
import com.myfashionlife.base.component.BaseFragment;
import com.myfashionlife.common.ActivityId;
import com.myfashionlife.common.IntentKeys;
import com.myfashionlife.manager.StartManager;
import com.myfashionlife.ui.home.adapter.WXNewsNewAdapter;
import com.myfashionlife.ui.home.data.WXNewsBean;
import com.myfashionlife.ui.home.impl.WXNewsContract;
import com.myfashionlife.ui.home.impl.WXNewsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lovexujh on 2017/10/15
 */

public class WXNewsFragment extends BaseFragment<WXNewsPresenter> implements WXNewsContract.View, BaseRefreshListener, AdapterView.OnItemClickListener {

    private String mCid = "1";//查询新闻的cid
    private List<WXNewsBean.ResultBean.ListBean> mDatas;
    private PullToRefreshLayout mPullToRefresh;
    private int mPager;
    private boolean mCanLoadMore = true;
    private RecyclerView mRecyclerView;
    private WXNewsNewAdapter mRecyclerViewAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatas = new ArrayList<>();
        mPullToRefresh = (PullToRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerViewAdapter = new WXNewsNewAdapter(getContext(), mDatas);
        mRecyclerViewAdapter.setData(mDatas);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                staggeredGridLayoutManager.invalidateSpanAssignments();
            }
        });

        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCid = bundle.getString(APPConstant.WX_NEWS_CID, "1");
            mPager = 1;
            mPresenter.queryWXNews(mCid, mPager);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    protected WXNewsPresenter attachPresenter() {
        return new WXNewsPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wx_news_layout;
    }


    @Override
    public void onError(String msg) {
        mCanLoadMore = false;
    }

    @Override
    public void onRefresh(WXNewsBean.ResultBean result) {
        if (result.getList() == null) {
            return;
        }
        mCanLoadMore = result.getList().size() >= APPConstant.WXNews.SIZE_20;
        mPullToRefresh.setCanLoadMore(mCanLoadMore);
        mPullToRefresh.finishRefresh();
        mPullToRefresh.finishLoadMore();
        if (result.getCurPage() == 1) {
            mDatas.clear();
        }
        mDatas.addAll(result.getList());
        mRecyclerViewAdapter.setData(mDatas);
        mRecyclerViewAdapter.notifyItemInserted(mDatas.size());
    }

    @Override
    public void refresh() {//下拉刷新时
        mPager = 1;
        mPresenter.queryWXNews(mCid, mPager);
    }

    @Override
    public void loadMore() {//上拉加载时
        if (mCanLoadMore) {
            mPresenter.queryWXNews(mCid, ++mPager);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WXNewsBean.ResultBean.ListBean listBean = mDatas.get(position);
        Intent intent = new Intent();
        intent.putExtra(IntentKeys.URL, listBean.getSourceUrl());
        StartManager.startActivity(ActivityId.WEB_VIEW_ACTIVITY, getContext(), intent);
    }
}
