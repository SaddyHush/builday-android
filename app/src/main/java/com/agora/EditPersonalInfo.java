package com.agora;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.agora.model.Response;
import com.agora.model.User;
import com.agora.network.NetworkUtil;
import com.agora.utils.Constants;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EditPersonalInfo extends AppCompatActivity {

    private EditText enterWorkDetails;
    private EditText enterYourInterests;
    private EditText enterInfoAboutYourself;
    private Button submitInfoDetails;
    private CompositeSubscription mSubscriptions;
    private String mToken;
    private String mEmail;
    private SharedPreferences mSharedPreferences;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_edit_personal_info);
        initView();
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
    }

    private void initView(){
        enterWorkDetails = (EditText) findViewById(R.id.enterYourWorkPlace);
        enterYourInterests = (EditText) findViewById(R.id.enterYourInterests);
        enterInfoAboutYourself = (EditText) findViewById(R.id.tellSomethingAboutYourself);
        submitInfoDetails = (Button) findViewById(R.id.bt_infosubmitbutton);
    }

    public void register(){
        String work = enterWorkDetails.getText().toString();
        String interests = enterYourInterests.getText().toString();
        String info = enterInfoAboutYourself.getText().toString();


        user.setWorkPlace(work);
        user.setYourInterests(interests);
        user.setYourInfo(info);

        registerProcess(user);
    }

    public void submitInfo(View v){
        register();
    }

    private void registerProcess(User user){

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).setUserChanges(mEmail, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

//        showSnackBarMessage(response.getMessage());

//        startActivity(new Intent(EditPersonalInfo.this, ProfileActivity.class));
        this.finish();

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
        }
    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.activity_profile),message,Snackbar.LENGTH_SHORT).show();

    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        System.out.println(mToken + "aaa" + mEmail);

    }



    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        this.user = user;
    }

}
