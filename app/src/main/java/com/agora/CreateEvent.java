package com.agora;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agora.fragments.DatePickerFragment;
import com.agora.fragments.TimePickerFragment;
import com.agora.model.Event;
import com.agora.model.Response;
import com.agora.network.NetworkUtil;
import com.agora.utils.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.agora.utils.Validation.validateFields;

public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText mEtTitle;
    private EditText mEtInterest;
    private EditText mEtMaxUsers;
    private TextInputLayout mTiTitle;
    private TextInputLayout mTiInterest;
    private TextInputLayout mTiMaxUsers;
    private Button mbtnSubmit;
    private ProgressBar mProgressbar;
    private LatLng position;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private double lat;
    private double lng;
    private String mToken;
    private String mEmail;
    private String date;
    private String time;
    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        initViews();
    }
    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

    }
    private void initViews(){
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtInterest = (EditText) findViewById(R.id.et_interest);
        mEtMaxUsers = (EditText) findViewById(R.id.et_maxUsers);
        mTiTitle = (TextInputLayout) findViewById(R.id.ti_title);
        mTiInterest = (TextInputLayout) findViewById(R.id.ti_interest);
        mTiMaxUsers = (TextInputLayout) findViewById(R.id.ti_maxUsers);
        mbtnSubmit = (Button) findViewById(R.id.btn_submit);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);

        mbtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
        position = getIntent().getParcelableExtra("position");
        lat = position.latitude;
        lng = position.longitude;

        date = "";
        time = "";
    }

    private void createEvent(){
        String title = mEtTitle.getText().toString();
        String interest = mEtInterest.getText().toString();
        Integer maxUsers = Integer.parseInt(mEtMaxUsers.getText().toString());

        int err = 0;
        if (!validateFields(title)) {
            err++;
            mTiTitle.setError("Title should not be empty!");
        }

        if (!validateFields(interest)) {
            err++;
            mTiInterest.setError("Interest should not be empty!");
        }

        if (!validateFields(maxUsers.toString()) || maxUsers < 1) {
            err++;
            mTiMaxUsers.setError("Limit should not be empty or less than 1!");
        }

        if (!validateFields(date)) {
            err++;
        }

        if (!validateFields(time)) {
            err++;
        }

        if (err == 0) {
            dateTime = date + " " + time;
            Event event = new Event();
            event.setTitle(mEtTitle.getText().toString());
            event.setInterest(mEtInterest.getText().toString());
            event.setUsersLimit(Double.parseDouble(mEtMaxUsers.getText().toString()));
            event.setLat(lat);
            event.setLng(lng);
            event.setOwnerID(mEmail);
            event.setEventDateTime(dateTime);
            eventCreationProcess(event);

            mProgressbar.setVisibility(View.VISIBLE);
        }else {
            showToastMessage("Enter Valid Details !");

        }
    }

    private void showToastMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void eventCreationProcess(Event event){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).uploadEvent(mEmail, event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(Response response){
        showToastMessage("Event created successfully");
        mProgressbar.setVisibility(View.GONE);
        finish();
    }
    private void handleError(Throwable e){
        mProgressbar.setVisibility(View.GONE);

        if (e instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) e).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showToastMessage(response.getMessage());

            } catch (IOException error) {
                error.printStackTrace();
            }
        } else {

            showToastMessage("Network Error !");
            e.printStackTrace();
        }
    }
    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    public void showTimePickerDialog(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        date = "";
        date += i2 + "/" + i1 + "/" + i;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        time = "";
        time += i + ":" + i1;
    }
}
