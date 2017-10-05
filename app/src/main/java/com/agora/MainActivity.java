package com.agora;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.agora.fragments.LoginFragment;
import com.agora.fragments.ResetPasswordDialog;
import com.agora.model.Response;
import com.agora.network.NetworkUtil;
import com.agora.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements ResetPasswordDialog.Listener ,DialogInterface.OnClickListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    private LoginFragment mLoginFragment;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;
    private ResetPasswordDialog mResetPasswordDialog;
    private CompositeSubscription mSubscriptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mSubscriptions = new CompositeSubscription();
        validateToken();
    }

    private void loadFragment(){

        if (mLoginFragment == null) {

            mLoginFragment = new LoginFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragmentFrame,mLoginFragment,LoginFragment.TAG).commit();
    }

    private void validateToken(){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).validateToken(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(Response response){
        String token = response.getToken();
        if (token.equals(mToken)){
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(Constants.TOKEN,response.getToken());
            editor.apply();
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    private void handleError(Throwable error) {
        loadFragment();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String data = intent.getData().getLastPathSegment();
        Log.d(TAG, "onNewIntent: "+data);

        mResetPasswordDialog = (ResetPasswordDialog) getFragmentManager().findFragmentByTag(ResetPasswordDialog.TAG);

        if (mResetPasswordDialog != null)
            mResetPasswordDialog.setToken(data);
    }

    @Override
    public void onPasswordReset(String message) {

        showSnackBarMessage(message);
    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.activity_main),message,Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}