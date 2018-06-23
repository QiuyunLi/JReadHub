package com.jeez.guanpj.jreadhub.ui.topic;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jeez.guanpj.jreadhub.R;
import com.jeez.guanpj.jreadhub.bean.DataListBean;
import com.jeez.guanpj.jreadhub.bean.TopicBean;
import com.jeez.guanpj.jreadhub.mvpframe.view.fragment.AbsBaseMvpFragment;
import com.jeez.guanpj.jreadhub.ui.adpter.AnimTopicListAdapter;
import com.jeez.guanpj.jreadhub.widget.LoadMoreFooter;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class TopicFragment extends AbsBaseMvpFragment<TopicPresenter> implements TopicContract.View, SwipeRefreshLayout.OnRefreshListener, LoadMoreFooter.OnLoadMoreListener {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view)
    HeaderAndFooterRecyclerView mRecyclerView;
    @BindView(R.id.txt_new)
    TextView mTxtNew;

    private LoadMoreFooter mLoadMoreFooter;
    private AnimTopicListAdapter mAdapter;

    public static TopicFragment newInstance() {
        Bundle args = new Bundle();
        TopicFragment fragment = new TopicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_common;
    }

    @Override
    protected void performInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.addItemDecoration(new GapItemDecoration(getActivity()));

        mLoadMoreFooter = new LoadMoreFooter(getContext(), mRecyclerView);
        mAdapter = new AnimTopicListAdapter();
        mAdapter.isFirstOnly(false);
        mAdapter.setNotDoAnimationCount(3);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initDataAndEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        mLoadMoreFooter.setOnLoadMoreListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        onRefresh();
        // 30 秒轮询获取新话题数量
        mPresenter.addSubscribe(Observable.interval(30, TimeUnit.SECONDS)
                .subscribe(aLong -> mPresenter.getNewTopicCount(mAdapter.getItem(0).getOrder())));
    }

    @Override
    public void onRefresh() {
        mPresenter.doRefresh();
    }

    @Override
    public void onLoadMore() {
        mPresenter.doLoadMore(mAdapter.getItem(mAdapter.getItemCount() - 1).getOrder());
    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestEnd(DataListBean<TopicBean> data, boolean isPull2Refresh) {
        List<TopicBean> dataList = data.getData();
        if (null != dataList && !dataList.isEmpty()) {
            mAdapter.addData(dataList);
            mLoadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
        } else {
            mLoadMoreFooter.setState(LoadMoreFooter.STATE_FINISHED);
        }

        if (isPull2Refresh) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRequestError(boolean isPull2Refresh) {
        if (isPull2Refresh) {
            mRefreshLayout.setRefreshing(false);
        } else {
            mLoadMoreFooter.setState(LoadMoreFooter.STATE_FAILED);
        }
    }

    @Override
    public void onFabClick() {
        mRecyclerView.scrollToPosition(0);
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void showNewTopicCount(int newTopicCount) {
        mTxtNew.setVisibility(View.VISIBLE);
        mTxtNew.setText(getContext().getString(R.string.new_topic_tips, newTopicCount));
    }

    @OnClick(R.id.txt_new)
    void onClick() {
        mRecyclerView.scrollToPosition(0);
        mTxtNew.setVisibility(View.GONE);
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }
}
