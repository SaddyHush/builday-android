package com.agora;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agora.model.Event;
import com.agora.model.Response;
import com.agora.network.NetworkUtil;
import com.agora.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EventDisplayActivity extends AppCompatActivity {
    private CompositeSubscription mSubscriptions;

    private String eventID;
    private ImageView profilePhoto;
    private TextView name;
    private TextView work;
    private TextView interests;
    private TextView title;
    private TextView interest;
    private String mToken;
    private String mEmail;
    private TextView maxNum;
    private Button btnConnectEvent;
    private Event thisEvent;
    private SharedPreferences mSharedPreferences;
    private boolean connected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        eventID = getIntent().getStringExtra("eventID");
        profilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
        name = (TextView) findViewById(R.id.tv_name);
        interests = (TextView) findViewById(R.id.tv_interest);
        work = (TextView) findViewById(R.id.tv_workplace);
        title = (TextView) findViewById(R.id.title);
        interest = (TextView) findViewById(R.id.interest);
        maxNum = (TextView) findViewById(R.id.maxNum);
        btnConnectEvent = (Button) findViewById(R.id.btnConnectEvent);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        getEvent(eventID);


        btnConnectEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!connected) {
                    addUserToEvent(eventID, mEmail);
                } else {
                    deleteUserFromEvent(eventID, mEmail);
                }
            }
        });
    }

    private void getEvent(String eventID){
        mSubscriptions.add(NetworkUtil.getRetrofit().getEvent(eventID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void addUserToEvent(String eventID, String userID){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).addUserToEvent(eventID, userID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleAddUser,this::handleError));
    }

    private void deleteUserFromEvent(String eventID, String userID){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).deleteUserFromEvent(eventID, userID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleDeleteUserFromEvent,this::handleError));
    }

    private void handleDeleteUserFromEvent(Response response) {
        btnConnectEvent.setText("Join this event");
        connected = false;
    }

    private void handleAddUser(Response response) {
        btnConnectEvent.setText("Disconnect from this event");
        connected = true;
    }

    private void handleResponse(Event event){
        title.setText(event.getTitle());
        interest.setText(event.getInterest());
        maxNum.setText("Maximum number of people for this event: "+ event.getUsersLimit());
        thisEvent = event;
        if (thisEvent.getJoinedUsers() != null && thisEvent.getJoinedUsers().contains(mEmail)){
            btnConnectEvent.setText("Disconnect from this event");
            connected = true;
        }else {
            connected = false;
        }

    }
    private void handleError(Throwable e) {

        if (e instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) e).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showToastMessage(response.getMessage());

            } catch (IOException error) {
                error.printStackTrace();
            }
        } else {

            showToastMessage("Network Error !");
            e.printStackTrace();
        }
    }

    private void showToastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

    }

}
