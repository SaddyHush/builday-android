package com.agora.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.agora.R;
import com.agora.model.Response;
import com.agora.model.User;
import com.agora.network.NetworkUtil;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.agora.utils.Validation.validateEmail;
import static com.agora.utils.Validation.validateFields;

public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText mEtName;
    private EditText mEtSurname;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button   mBtRegister;
    private TextView mTvLogin;
    private RadioGroup radioGroup;
    private TextInputLayout mTiName;
    private TextInputLayout mTiSurname;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private TextInputLayout mTiGender;
    private ProgressBar mProgressbar;


    private CompositeSubscription mSubscriptions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register,container,false);

        mSubscriptions = new CompositeSubscription();

        initViews(view);

        return view;
    }



    private void initViews(View v) {

        mEtName = (EditText) v.findViewById(R.id.et_name);
        mEtSurname = (EditText) v.findViewById(R.id.et_surname);
        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtRegister = (Button) v.findViewById(R.id.btn_register);
        mTvLogin = (TextView) v.findViewById(R.id.tv_login);
        mTiName = (TextInputLayout) v.findViewById(R.id.ti_name);
        mTiSurname = (TextInputLayout) v.findViewById(R.id.ti_surname);
        mTiGender = (TextInputLayout) v.findViewById(R.id.ti_gender);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mProgressbar = (ProgressBar) v.findViewById(R.id.progress);

        radioGroup = (RadioGroup)v.findViewById(R.id.rgroup);
        mBtRegister.setOnClickListener(view -> register());
        mTvLogin.setOnClickListener(view -> goToLogin());
    }

    private void register() {

        setError();

        String name = mEtName.getText().toString();
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        String surname = mEtSurname.getText().toString();

        int radioButtonID = radioGroup.getCheckedRadioButtonId();

        RadioButton r = (RadioButton) radioGroup.findViewById(radioButtonID);
        String gender = r.getText().toString();


        int err = 0;

        if (!validateFields(name)) {

            err++;
            mTiName.setError("Name should not be empty!");
        }

        if (!validateFields(surname)) {

            err++;
            mTiSurname.setError("Surname should not be empty!");
        }

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Email should be valid!");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty!");
        }
        if (!validateFields(gender)) {

            err++;
            mTiPassword.setError("Gender has to be selected!");
        }

        if (err == 0) {

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setSurname(surname);
            user.setGender(gender);


            mProgressbar.setVisibility(View.VISIBLE);
            registerProcess(user);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }



    private void setError() {

        mTiName.setError(null);
        mTiEmail.setError(null);
        mTiPassword.setError(null);
        mTiGender.setError(null);
    }

    private void registerProcess(User user) {

        mSubscriptions.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressbar.setVisibility(View.GONE);
        showSnackBarMessage(response.getMessage());
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

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

        if (getView() != null) {

            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToLogin(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }}