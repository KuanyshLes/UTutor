/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.support.robigroup.ututor.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;


import com.support.robigroup.ututor.di.ActivityContext;
import com.support.robigroup.ututor.di.PerActivity;
import com.support.robigroup.ututor.ui.chat.ChatMvpPresenter;
import com.support.robigroup.ututor.ui.chat.ChatMvpView;
import com.support.robigroup.ututor.ui.chat.ChatPresenter;
import com.support.robigroup.ututor.ui.chat.RateMvpPresenter;
import com.support.robigroup.ututor.ui.chat.RateMvpView;
import com.support.robigroup.ututor.ui.chat.eval.RatePresenter;
import com.support.robigroup.ututor.ui.history.HistoryMvpPresenter;
import com.support.robigroup.ututor.ui.history.HistoryMvpView;
import com.support.robigroup.ututor.ui.history.HistoryPresenter;
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpView;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityMvpPresenter;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityMvpView;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityPresenter;
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentView;
import com.support.robigroup.ututor.ui.login.RegistrationFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.RegistrationFragmentView;
import com.support.robigroup.ututor.ui.login.login_fragment.LoginFragmentPresenter;
import com.support.robigroup.ututor.ui.login.reg_fragment.RegPhoneNumberFragmentPresenter;
import com.support.robigroup.ututor.ui.login.reg_fragment.RegistrationFragmentPresenter;
import com.support.robigroup.ututor.utils.AppSchedulerProvider;
import com.support.robigroup.ututor.utils.SchedulerProvider;


import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by janisharali on 27/01/17.
 */

@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    RateMvpPresenter<RateMvpView> provideRatePresenter(
            RatePresenter<RateMvpView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    ChatMvpPresenter<ChatMvpView> provideChatPresenter(
            ChatPresenter<ChatMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    HistoryMvpPresenter<HistoryMvpView> provideHistoryPresenter(
            HistoryPresenter<HistoryMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginRegistrationActivityMvpPresenter<LoginRegistrationActivityMvpView> provideLoginRegistrationActivityPresenter(
            LoginRegistrationActivityPresenter<LoginRegistrationActivityMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginFragmentMvpPresenter<LoginFragmentMvpView> provideLoginFragmentPresenter(
            LoginFragmentPresenter<LoginFragmentMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    RegistrationFragmentMvpPresenter<RegistrationFragmentView> provideRegistrationFragmentPresenter(
            RegistrationFragmentPresenter<RegistrationFragmentView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    RegPhoneNumberFragmentMvpPresenter<RegPhoneNumberFragmentView> provideRegPhoneFragmentPresenter(
            RegPhoneNumberFragmentPresenter<RegPhoneNumberFragmentView> presenter){
        return presenter;
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }
}
