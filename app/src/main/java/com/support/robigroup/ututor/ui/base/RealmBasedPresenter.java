package com.support.robigroup.ututor.ui.base;

import com.support.robigroup.ututor.Constants;
import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.commons.ChatLesson;
import com.support.robigroup.ututor.data.DataManager;
import com.support.robigroup.ututor.ui.chat.model.ChatMessage;
import com.support.robigroup.ututor.utils.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
import io.realm.RealmResults;


public class RealmBasedPresenter<V extends MvpView>  extends BasePresenter<V> implements MvpRealmPresenter{

    private Realm realm;
    private ChatInformation chatInformation;
    private RealmResults<ChatMessage> chatMessages;

    @Inject
    public RealmBasedPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
    }

    @Override
    public void updateChatInformation(final ChatLesson chatLesson) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatInformation request = realm.where(ChatInformation.class).findFirst();
                if(request!=null){
                    request.setStatusId(Constants.STATUS_COMPLETED);
                    request.setInvoiceSum(chatLesson.getInvoiceSum());
                    request.setEndTime(chatLesson.getEndTime());
                    request.setDuration(chatLesson.getDuration());
                    request.setInvoiceTariff(chatLesson.getInvoiceTariff());
                }
            }
        });
    }

    @Override
    public Realm getRealm() {
        return realm;
    }

    @Override
    public ChatInformation getChatInformation() {
        if(chatInformation==null && !realm.where(ChatInformation.class).findAll().isEmpty()){
            chatInformation = realm.where(ChatInformation.class).findAll().last();
        }
        return chatInformation;
    }

    @Override
    public RealmResults<ChatMessage> getChatMessages() {
        if(chatMessages==null){
            chatMessages = realm.where(ChatMessage.class).findAll();
        }
        return chatMessages;
    }


}
