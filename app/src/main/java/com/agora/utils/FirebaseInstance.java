package com.agora.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.agora.model.Response;
import com.agora.network.NetworkUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by asadz on 2/27/18.
 */

public class FirebaseInstance extends FirebaseInstanceIdService {
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;


    @Override
    public void onTokenRefresh() {
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (mEmail != null && mToken != null)
            updateTokenInServer(mEmail, refreshedToken);

    }

    private void updateTokenInServer(String mEmail, String fcmToken){
        mSubscriptions.add(NetworkUtil.getRetrofit().updateFCMToken(mEmail, fcmToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    public void handleResponse(Response response){
        Log.d("Token Refresh Success", response.getMessage());
    }

    public void handleError(Throwable error){
        Log.d("Token Refresh Error", error.getMessage());
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

    }
}
