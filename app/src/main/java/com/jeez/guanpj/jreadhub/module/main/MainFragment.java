package com.jeez.guanpj.jreadhub.module.main;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jeez.guanpj.jreadhub.R;
import com.jeez.guanpj.jreadhub.base.fragment.AbsBaseFragment;
import com.jeez.guanpj.jreadhub.event.FabClickEvent;
import com.jeez.guanpj.jreadhub.event.ToolbarNavigationClickEvent;
import com.jeez.guanpj.jreadhub.event.ToolbarSearchClickEvent;
import com.jeez.guanpj.jreadhub.module.adpter.FragmentAdapter;
import com.jeez.guanpj.jreadhub.module.common.CommonListFragment;
import com.jeez.guanpj.jreadhub.module.topic.TopicFragment;
import com.jeez.guanpj.jreadhub.mvpframe.rx.RxBus;
import com.jeez.guanpj.jreadhub.util.Constants;
import com.jeez.guanpj.jreadhub.util.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportFragment;

public class MainFragment extends AbsBaseFragment implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    public void initView() {
        mToolbar.inflateMenu(R.menu.menu_home);
        mToolbar.setTitle(getText(R.string.menu_home));
    }

    @Override
    public void initDataAndEvent() {
        List<String> pageTitles = new ArrayList<>();
        pageTitles.add("热门话题");
        pageTitles.add("科技动态");
        pageTitles.add("开发者资讯");
        pageTitles.add("区块链快讯");

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TopicFragment.newInstance());
        fragments.add(CommonListFragment.newInstance(Constants.TYPE_NEWS));
        fragments.add(CommonListFragment.newInstance(Constants.TYPE_TECHNEWS));
        fragments.add(CommonListFragment.newInstance(Constants.TYPE_BLOCKCHAIN));

        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager(), pageTitles);
        adapter.setFragments(fragments);
        mViewPager.setAdapter(adapter);

        for (int i = 0; i < pageTitles.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(pageTitles.get(i)));
        }
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mToolbar.setNavigationOnClickListener(view -> RxBus.getInstance().post(new ToolbarNavigationClickEvent()));
        mToolbar.setOnMenuItemClickListener(this);
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        RxBus.getInstance().post(new FabClickEvent(mViewPager.getCurrentItem()));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                RxBus.getInstance().post(new ToolbarSearchClickEvent());
                showShortToast("Coming soon...");
                break;
            case R.id.action_share:
                NavigationUtil.shareToApp(getActivity(), "https://readhub.cn\n互联网聚合阅读平台");
                break;
            default:
                break;
        }
        return true;
    }

    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }
}
