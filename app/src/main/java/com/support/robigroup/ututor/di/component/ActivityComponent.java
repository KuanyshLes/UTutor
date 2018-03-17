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

package com.support.robigroup.ututor.di.component;

import com.support.robigroup.ututor.di.PerActivity;
import com.support.robigroup.ututor.di.module.ActivityModule;
import com.support.robigroup.ututor.ui.chat.ActivityChat;
import com.support.robigroup.ututor.ui.chat.eval.RateDialog;
import com.support.robigroup.ututor.ui.history.HistoryChatMessagesActivity;
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivity;
import com.support.robigroup.ututor.ui.login.loginFragment.LoginFragment;
import com.support.robigroup.ututor.ui.login.regFragment.RegistrationFragment;
import com.support.robigroup.ututor.ui.login.regPhoneNumberFragment.RegPhoneNumberFragment;
import com.support.robigroup.ututor.ui.login.setPasswordFragment.SetPasswordFragment;
import com.support.robigroup.ututor.ui.login.verifyPhoneNumberFragment.VerifyPhoneNumberFragment;
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerActivity;
import com.support.robigroup.ututor.ui.navigationDrawer.account.AccountFragment;
import com.support.robigroup.ututor.ui.navigationDrawer.feedback.FeedbackFragment;
import com.support.robigroup.ututor.ui.navigationDrawer.history.HistoryChatListFragment;
import com.support.robigroup.ututor.ui.navigationDrawer.main.MainFragment;

import dagger.Component;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(ActivityChat activity);

    void inject(RateDialog rateDialog);

    void inject(HistoryChatMessagesActivity activity);

    void inject(LoginRegistrationActivity activity);

    void inject(LoginFragment loginFragment);

    void inject(RegistrationFragment registrationFragment);

    void inject(RegPhoneNumberFragment regPhoneNumberFragment);

    void inject(VerifyPhoneNumberFragment verifyPhoneNumberFragment);

    void inject(SetPasswordFragment setPasswordFragment);

    void inject(DrawerActivity drawerActivity);

    void inject(HistoryChatListFragment historyChatListFragment);

    void inject(MainFragment historyChatListFragment);

    void inject(AccountFragment accountFragment);

    void inject(FeedbackFragment feedbackFragment);

}
