package com.jeez.guanpj.jreadhub.mvpframe.view.lce;

import android.view.View;

import com.jeez.guanpj.jreadhub.R;
import com.jeez.guanpj.jreadhub.mvpframe.view.lce.animator.DefaultLceAnimator;
import com.jeez.guanpj.jreadhub.mvpframe.view.lce.animator.ILceAnimator;

public class MvpLceViewDelegate implements IBaseMvpLceView {

    private View loadingView;
    private View contentView;
    private View errorView;

    private ILceAnimator lceAnimator;

    public void initLceView(View view) {
        if (loadingView == null) {
            loadingView = view.findViewById(R.id.loading_view);
        }
        if (contentView == null) {
            contentView = view.findViewById(R.id.content_view);
        }
        if (errorView == null) {
            errorView = view.findViewById(R.id.error_view);
        }
        if (loadingView == null) {
            throw new NullPointerException("loadingView is not null!");
        }
        if (contentView == null) {
            throw new NullPointerException("contentView is not null!");
        }
        if (errorView == null) {
            throw new NullPointerException("errorView is not null!");
        }
    }

    public void setOnErrorViewClickListener(View.OnClickListener onClickListener) {
        if (this.errorView != null) {
            this.errorView.setOnClickListener(onClickListener);
        }
    }

    private ILceAnimator getLceAnimator() {
        if (lceAnimator == null) {
            lceAnimator = DefaultLceAnimator.getInstance();
        }
        return lceAnimator;
    }

    public void setLceAnimator(ILceAnimator lceAnimator) {
        this.lceAnimator = lceAnimator;
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        if (!pullToRefresh) {
            getLceAnimator().showLoading(loadingView, contentView, errorView);
        }
    }

    @Override
    public void showContent() {
        getLceAnimator().showContent(loadingView, contentView, errorView);
    }

    @Override
    public void showError() {
        getLceAnimator().showErrorView(loadingView, contentView, errorView);
    }

    @Override
    public void bindData(Object data) {

    }
}