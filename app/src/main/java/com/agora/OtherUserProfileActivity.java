package com.agora;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agora.model.Event;
import com.agora.model.Response;
import com.agora.model.User;
import com.agora.network.NetworkUtil;
import com.agora.utils.Constants;
import com.agora.utils.ProfileEventsRecyclerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OtherUserProfileActivity extends Activity {
    private ImageView profilePhoto;
    private TextView name;
    private String mToken;
    private String mEmail;
    private User user;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private Button declineButton;
    private Button acceptButton;
    private TextView tv_interest;
    private String userID;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        init();
        loadUserProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
    }

    private void init() {
        profilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
        name = (TextView) findViewById(R.id.tv_name);
        tv_interest = (TextView) findViewById(R.id.tv_interest);
        declineButton = (Button) findViewById(R.id.btnDecline);
        acceptButton = (Button) findViewById(R.id.btnAccept);
        userID = getIntent().getStringExtra("user");
        eventID = getIntent().getStringExtra("event");
        acceptButton.setOnClickListener(view -> acceptUser());
        declineButton.setOnClickListener(view -> declineUser());
    }

    private void acceptUser(){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).acceptUserToEvent(userID, eventID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this:: handleSuccess ,this::handleError));
    }

    private void declineUser(){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).declineUserToEvent(userID, eventID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this:: handleSuccess ,this::handleError));
    }

    private void handleSuccess(Response response) {
    }

    private void loadUserProfile() {
        mSubscriptions.add(NetworkUtil.getRetrofit().getOtherUserProfile(userID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::userLoaded,this::handleError));
    }

    private void userLoaded(User user) {
        this.user = user;
        name.setText(user.getName() + " " + user.getSurname());
        if (user.getYourInterests() != null) {
            tv_interest.setText("Interests: " + user.getYourInterests());
        }
        if (user.getMainPhotoSmall() != null) {
            byte[] decodedString = Base64.decode(user.getMainPhotoSmall(), Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePhoto.setImageBitmap(photo);
        }
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
            error.printStackTrace();
        }
    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.activity_profile),message,Snackbar.LENGTH_SHORT).show();

    }
}
