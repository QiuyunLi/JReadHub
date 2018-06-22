package com.jeez.guanpj.jreadhub.di.module;

import android.content.Context;

import com.jeez.guanpj.jreadhub.ReadhubApplication;
import com.jeez.guanpj.jreadhub.core.DataManager;
import com.jeez.guanpj.jreadhub.core.net.NetHelper;
import com.jeez.guanpj.jreadhub.core.net.NetHelperImpl;
import com.jeez.guanpj.jreadhub.core.preference.IPreferenceHelper;
import com.jeez.guanpj.jreadhub.core.preference.PreferenceHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = NetModule.class)
public class AppModule {
    private final ReadhubApplication mApplication;

    public AppModule(ReadhubApplication application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    NetHelper provideNetHelper(NetHelperImpl retrofitHelper) {
        return retrofitHelper;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(NetHelper netHelper) {
        return new DataManager(netHelper);
    }
}
