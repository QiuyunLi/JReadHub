package com.jeez.guanpj.jreadhub.mvpframe.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.jeez.guanpj.jreadhub.app.ReadhubApplicationLike;
import com.jeez.guanpj.jreadhub.base.fragment.AbsBaseFragment;
import com.jeez.guanpj.jreadhub.di.component.DaggerFragmentComponent;
import com.jeez.guanpj.jreadhub.di.component.FragmentComponent;
import com.jeez.guanpj.jreadhub.di.module.FragmentModule;
import com.jeez.guanpj.jreadhub.mvpframe.presenter.BasePresenter;
import com.jeez.guanpj.jreadhub.mvpframe.view.IBaseMvpView;

import javax.inject.Inject;

/**
 * Created by Jie on 2016-11-2.
 */

public abstract class AbsBaseMvpFragment<P extends BasePresenter> extends AbsBaseFragment implements IBaseMvpView {

    @Inject
    public P mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performInject();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != mPresenter) {
            mPresenter.onAttach(this);
        }
    }

    public FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(ReadhubApplicationLike.getAppComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    protected abstract void performInject();

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.onDetach();
        }
        super.onDestroyView();
    }
}
