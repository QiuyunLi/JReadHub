package com.jeez.guanpj.jreadhub.module.topic.detail;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeez.guanpj.jreadhub.R;
import com.jeez.guanpj.jreadhub.bean.EntityEventTopicBean;
import com.jeez.guanpj.jreadhub.bean.TopicBean;
import com.jeez.guanpj.jreadhub.bean.TopicNewsBean;
import com.jeez.guanpj.jreadhub.event.OpenWebSiteEvent;
import com.jeez.guanpj.jreadhub.module.adpter.TopicTimelineAdapter;
import com.jeez.guanpj.jreadhub.module.topic.detail.relate.RelevantTopicWindow;
import com.jeez.guanpj.jreadhub.mvpframe.rx.RxBus;
import com.jeez.guanpj.jreadhub.mvpframe.view.lce.fragment.AbsBaseMvpLceSwipeBackFragment;
import com.jeez.guanpj.jreadhub.util.Constants;
import com.jeez.guanpj.jreadhub.widget.RelativePopupWindow;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.util.ArrayList;

import butterknife.BindView;

public class TopicDetailFragment extends AbsBaseMvpLceSwipeBackFragment<TopicBean, TopicDetailPresenter> implements TopicDetailContract.View, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.txt_topic_detail_title)
    TextView mTxtTopicTitle;
    @BindView(R.id.txt_topic_time)
    TextView mTxtTopicTime;
    @BindView(R.id.txt_topic_description)
    TextView mTxtTopicDescription;
    @BindView(R.id.ll_web_title_container)
    LinearLayout mTitleContainer;
    @BindView(R.id.ll_topic_trace_container)
    LinearLayout mTimelineContainer;
    @BindView(R.id.recycler_topic_trace)
    RecyclerView mRecyclerTimeline;
    @BindView(R.id.ll_relative_topic_container)
    LinearLayout mRelativeTopicContainer;
    @BindView(R.id.tfl_relative_topic)
    TagFlowLayout mRelativeTopic;
    @BindView(R.id.content_view)
    NestedScrollView mScrollView;
    @BindView(R.id.txt_toolbar_header)
    TextView mToolbarHeader;

    private String mTopicTitle;
    private String mTopicId;
    private TopicBean mTopicBean;
    private TopicTimelineAdapter mTimelineAdapter;
    private boolean mIsStar;
    private boolean mShowTips = false;

    public static TopicDetailFragment newInstance(String topicId, String title) {
        TopicDetailFragment fragment = new TopicDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_TOPIC_ID, topicId);
        bundle.putString(Constants.BUNDLE_TOPIC_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopicId = getArguments().getString(Constants.BUNDLE_TOPIC_ID);
        mTopicTitle = getArguments().getString(Constants.BUNDLE_TOPIC_TITLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData(false);
    }

    @Override
    public void loadData(boolean isPullToRefresh) {
        mPresenter.getTopicDetail(mTopicId, isPullToRefresh);
        mPresenter.checkStar(mTopicId);
    }

    @Override
    protected void performInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_topic_detail;
    }

    @Override
    public void initView() {
        mToolbar.setNavigationIcon(R.drawable.ic_nav_back);
        mToolbar.inflateMenu(R.menu.menu_topic_detail);
        mToolbar.setTitle(getText(R.string.menu_topic_detail));
        mToolbarHeader.setText(mTopicTitle);
        mToolbarHeader.setVisibility(View.GONE);
    }

    @Override
    public void initDataAndEvent() {
        mToolbar.setNavigationOnClickListener(v -> pop());
        mToolbar.setOnMenuItemClickListener(this);

        mTimelineAdapter = new TopicTimelineAdapter(getContext());
        mRecyclerTimeline.setAdapter(mTimelineAdapter);
        mRecyclerTimeline.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerTimeline.setNestedScrollingEnabled(false);

        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > mTxtTopicTime.getBottom()) {
                mToolbarHeader.setVisibility(View.VISIBLE);
                mToolbar.setTitle("");
            } else {
                mToolbarHeader.setVisibility(View.GONE);
                mToolbar.setTitle(getText(R.string.menu_topic_detail));
            }
        });
    }

    @Override
    public void bindData(TopicBean topicBean, boolean isPullToRefresh) {
        this.mTopicBean = topicBean;
        mTxtTopicTitle.setText(mTopicBean.getTitle().trim());

        if (null != mTopicBean.getFormattedPublishDate()) {
            mTxtTopicTime.setText(mTopicBean.getFormattedPublishDate().toLocalDate().toString() + "  " +
                    mTopicBean.getFormattedPublishDate().toLocalTime().toString().substring(0, 8));
        } else {
            mTxtTopicTime.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mTopicBean.getSummary().trim())) {
            mTxtTopicDescription.setVisibility(View.GONE);
        } else {
            mTxtTopicDescription.setText(mTopicBean.getSummary().trim());
            mTxtTopicDescription.setVisibility(View.VISIBLE);
        }

        mTitleContainer.removeAllViews();
        if (mTopicBean.getNewsArray() == null || mTopicBean.getNewsArray().isEmpty()) {
            mTitleContainer.setVisibility(View.GONE);
        } else {
            mTitleContainer.setVisibility(View.VISIBLE);
            for (final TopicNewsBean topic : mTopicBean.getNewsArray()) {
                TextView textView = new TextView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                textView.setPadding(10, 16, 10, 16);
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_topic_detail_ring, 0, 0, 0);
                textView.setCompoundDrawablePadding(15);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                /*textView.setTextColor(ContextCompat.getColor(getContext(),
                        ResourceUtil.getResource(getActivity(), R.attr.readhubTextColorPrimary)));*/
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_topic_detail_news_title));
                //textView.setBackgroundResource(R.drawable.selector_btn_background);
                if (TextUtils.isEmpty(topic.getSiteName())) {
                    textView.setText(topic.getTitle());
                } else {
                    SpannableString spannableTitle = SpannableString.valueOf(getContext().getString(R.string.title___site_name, topic.getTitle(), topic.getSiteName()));
                    spannableTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text_topic_detail_news_author)),
                            topic.getTitle().length(),
                            topic.getTitle().length() + topic.getSiteName().length() + 2,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableTitle);
                }
                textView.setOnClickListener(v -> {
                    String url = null;
                    if (!TextUtils.isEmpty(topic.getMobileUrl())) {
                        url = topic.getMobileUrl();
                    } else {
                        url = topic.getUrl();
                    }
                    if (!TextUtils.isEmpty(url)) {
                        RxBus.getInstance().post(new OpenWebSiteEvent(url, topic.getTitle()));
                    }
                });
                mTitleContainer.addView(textView);
            }
        }

        if (null != mTopicBean.getTimeline() && null != mTopicBean.getTimeline().getTopics() && 0 < mTopicBean.getTimeline().getTopics().size()) {
            mTimelineAdapter.addItems(mTopicBean.getTimeline().getTopics());
            mTimelineContainer.setVisibility(View.VISIBLE);
        } else {
            mTimelineContainer.setVisibility(View.GONE);
        }

        if (!mTopicBean.getEntityEventTopics().isEmpty()) {
            mRelativeTopicContainer.setVisibility(View.VISIBLE);
            ArrayList<EntityEventTopicBean> entityEventTopics = mTopicBean.getEntityEventTopics();
            mRelativeTopic.setAdapter(new TagAdapter<EntityEventTopicBean>(entityEventTopics) {
                @Override
                public View getView(FlowLayout parent, int position, EntityEventTopicBean entityEventTopicBean) {
                    TextView item = (TextView) getLayoutInflater().inflate(R.layout.item_relevant_topic, mRelativeTopic, false);
                    item.setText(entityEventTopicBean.getEntityName() + entityEventTopicBean.getEventTypeLabel());
                    return item;
                }
            });
            mRelativeTopic.setOnTagClickListener((view, position, parent) -> {
                String topicId = String.valueOf(entityEventTopics.get(position).getEntityId());
                long order = mTopicBean.getOrder();

                RelevantTopicWindow window = new RelevantTopicWindow(getActivity(), topicId, order);
                window.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER, true);
                setBackgroundAlpha(0.7f);
                window.setOnDismissListener(() -> {
                    setBackgroundAlpha(1f);
                    if (((TagView) view).isChecked()) {
                        ((TagView) view).setChecked(false);
                    }
                });
                return true;
            });
        } else {
            mRelativeTopicContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckStarResult(boolean isTopicExist) {
        if (isTopicExist) {
            mToolbar.getMenu().findItem(R.id.action_collect).setIcon(R.drawable.ic_tool_bar_star_fill);
            if (mShowTips) {
                if (mIsStar) {
                    showShortToast(getString(R.string.tips_unstar_faild));
                } else {
                    showShortToast(getString(R.string.tips_star_success));
                }
            }
            mIsStar = true;
        } else {
            mToolbar.getMenu().findItem(R.id.action_collect).setIcon(R.drawable.ic_tool_bar_star_border);
            if (mShowTips) {
                if (mIsStar) {
                    showShortToast(getString(R.string.tips_unstar_success));
                } else {
                    showShortToast(getString(R.string.tips_star_faild));
                }
            }
            mIsStar = false;
        }
        mShowTips = true;
    }

    private void setBackgroundAlpha(float v) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = v;
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_collect:
                if (mIsStar) {
                    mPresenter.removeStar(mTopicBean);
                } else {
                    mPresenter.addStar(mTopicBean);
                }
                break;
            default:
                break;
        }
        return true;
    }
}
