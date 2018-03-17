package com.support.robigroup.ututor.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;


import com.support.robigroup.ututor.di.ActivityContext;
import com.support.robigroup.ututor.di.PerActivity;
import com.support.robigroup.ututor.ui.chat.ChatMvpPresenter;
import com.support.robigroup.ututor.ui.chat.ChatMvpView;
import com.support.robigroup.ututor.ui.chat.ChatPresenter;
import com.support.robigroup.ututor.ui.chat.RateMvpPresenter;
import com.support.robigroup.ututor.ui.chat.RateMvpView;
import com.support.robigroup.ututor.ui.chat.eval.RatePresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerMvpPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerMvpView;
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.account.AccountFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.account.AccountFragmentMvpView;
import com.support.robigroup.ututor.ui.navigationDrawer.account.AccountFragmentPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.feedback.FeedbackFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.feedback.FeedbackFragmentMvpView;
import com.support.robigroup.ututor.ui.navigationDrawer.feedback.FeedbackFragmentPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.history.ChatListMvpPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.history.ChatsListMvpView;
import com.support.robigroup.ututor.ui.navigationDrawer.history.HistoryChatListPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.history.HistoryChatMessagesMvpPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.history.HistoryChatMessagesMvpView;
import com.support.robigroup.ututor.ui.history.HistoryChatMessagesPresenter;
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpView;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityMvpPresenter;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityMvpView;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityPresenter;
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentView;
import com.support.robigroup.ututor.ui.login.RegistrationFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.RegistrationFragmentView;
import com.support.robigroup.ututor.ui.login.SetPasswordFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.SetPasswordFragmentView;
import com.support.robigroup.ututor.ui.login.VerifyPhoneNumberFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.login.VerifyPhoneNumberFragmentView;
import com.support.robigroup.ututor.ui.login.loginFragment.LoginFragmentPresenter;
import com.support.robigroup.ututor.ui.login.regFragment.RegistrationFragmentPresenter;
import com.support.robigroup.ututor.ui.login.regFragment.VerifyPhoneNumberFragmentPresenter;
import com.support.robigroup.ututor.ui.login.regPhoneNumberFragment.RegPhoneNumberFragmentPresenter;
import com.support.robigroup.ututor.ui.login.setPasswordFragment.SetPasswordFragmentPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.main.MainFragmentMvpPresenter;
import com.support.robigroup.ututor.ui.navigationDrawer.main.MainFragmentMvpView;
import com.support.robigroup.ututor.ui.navigationDrawer.main.MainFragmentPresenter;
import com.support.robigroup.ututor.utils.AppSchedulerProvider;
import com.support.robigroup.ututor.utils.SchedulerProvider;


import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

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
    HistoryChatMessagesMvpPresenter<HistoryChatMessagesMvpView> provideHistoryPresenter(
            HistoryChatMessagesPresenter<HistoryChatMessagesMvpView> presenter) {
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
    @PerActivity
    VerifyPhoneNumberFragmentMvpPresenter<VerifyPhoneNumberFragmentView> provideVerifyPhoneNumberFragmentPresenter(
            VerifyPhoneNumberFragmentPresenter<VerifyPhoneNumberFragmentView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    SetPasswordFragmentMvpPresenter<SetPasswordFragmentView> provideSetPasswordFragmentPresenter(
            SetPasswordFragmentPresenter<SetPasswordFragmentView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    DrawerMvpPresenter<DrawerMvpView> provideDrawerPresenter(
            DrawerPresenter<DrawerMvpView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    ChatListMvpPresenter<ChatsListMvpView> provideChatListPresenter(
            HistoryChatListPresenter<ChatsListMvpView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    MainFragmentMvpPresenter<MainFragmentMvpView> provideMainFragmentPresenter(
            MainFragmentPresenter<MainFragmentMvpView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    AccountFragmentMvpPresenter<AccountFragmentMvpView> provideAccountFragmentPresenter(
            AccountFragmentPresenter<AccountFragmentMvpView> presenter){
        return presenter;
    }

    @Provides
    @PerActivity
    FeedbackFragmentMvpPresenter<FeedbackFragmentMvpView> provideFeedbackFragmentPresenter(
            FeedbackFragmentPresenter<FeedbackFragmentMvpView> presenter){
        return presenter;
    }
}
