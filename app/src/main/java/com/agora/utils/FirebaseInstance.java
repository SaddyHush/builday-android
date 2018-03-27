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
        Log.d("Token", refreshedToken);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.FCMTOKEN, refreshedToken);
        editor.apply();
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

    }
}
