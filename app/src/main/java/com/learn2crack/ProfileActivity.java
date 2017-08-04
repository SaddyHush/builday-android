package com.learn2crack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learn2crack.Animation.GalleryUtil;
import com.learn2crack.fragments.ChangePasswordDialog;
import com.learn2crack.model.Response;
import com.learn2crack.model.Status;
import com.learn2crack.model.User;
import com.learn2crack.network.NetworkUtil;
import com.learn2crack.utils.Constants;
import com.learn2crack.utils.StatusRecyclerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfileActivity extends AppCompatActivity implements ChangePasswordDialog.Listener {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    private ImageView profilePhoto;
    private TextView name;
    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
    private int statusInd;

    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;
    private User user;
    private StatusRecyclerAdapter mAdapter;

    private FloatingActionButton floatingActionButton;

    private Bitmap selectedBitmap;

    private CompositeSubscription mSubscriptions;
    private Button editButton;
    private TextView tv_interest;
    private TextView tv_workplace;
    private EditText tv_status;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Status> statuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSubscriptions = new CompositeSubscription();
        initViews();
        initSharedPreferences();
        loadProfile();
        setRecyclerViewScrollListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStatusAmount();

    }
    private void getStatusAmount(){
        mSubscriptions.add(NetworkUtil.getRetrofit().getStatusAmount(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::setStatusInd,this::handleStatusIndError));
    }

    private void setStatusInd(int response){
        statusInd = --response;
        if (statuses.size() == 0 && statusInd != -1) {
            requestStatus(statusInd--);
        }
    }

    private void handleStatusIndError(Throwable error){
        getStatusAmount();
    }

    public void requestStatus(int n){
        mSubscriptions.add(NetworkUtil.getRetrofit().getStatus(mEmail, n)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(v -> handleStatusResponse(v, n),this::handleError));
    }
    private void handleStatusResponse(String status, int n) {
        String fullName = user.getName() + " " + user.getSurname();
        Status s = new Status(fullName, status, n);
        statuses.add(s);
        Collections.sort(statuses);
        mAdapter.notifyItemInserted(statuses.size());
    }

    private int getLastVisibleItemPosition() {
        return mLinearLayoutManager.findLastVisibleItemPosition();
    }
    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new
                                                  RecyclerView.OnScrollListener() {
                                                      @Override
                                                      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                          super.onScrollStateChanged(recyclerView, newState);
                                                          int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                                                          if (totalItemCount == getLastVisibleItemPosition() + 1 && statusInd != -1) {
                                                              requestStatus(statusInd--);
                                                          }
                                                      }
                                                  });
    }

    public void statusConfirm(View view){
        String status = tv_status.getText().toString();

        if(status.equals("")){
            return;
        }
        User user1 = new User();
        user1.setStatus(status);
        addStatus(user1);
    }
    private void addStatus(User status) {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).createStatus(mEmail, status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void updateProcess(User user) {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).setUserChanges(mEmail, user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        showSnackBarMessage(response.getMessage());
    }

    private void register(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        user.setMainPhoto(encodedImage);
        updateProcess(user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GALLERY_ACTIVITY_CODE) {
                if (resultCode == RESULT_OK) {
                    String picturePath = data.getStringExtra("picturePath");
                    performCrop(picturePath);
                }
            }

            if (requestCode == RESULT_CROP) {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    selectedBitmap = extras.getParcelable("data");
                    register();

                    profilePhoto.setImageBitmap(selectedBitmap);
                    profilePhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        }

        catch (Exception ex){
            Toast.makeText(this, ""+ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performCrop(String picUri) {
        try {
//Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
// indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
// set crop properties
            cropIntent.putExtra("crop", "true");
// indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
// indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

// retrieve data on return
            cropIntent.putExtra("return-data", true);
// start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
// respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
// display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void initViews() {
        profilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
        name = (TextView) findViewById(R.id.tv_name);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.btnMap);
        editButton = (Button) findViewById(R.id.btnEditInfo);
        tv_interest = (TextView) findViewById(R.id.tv_interest);
        tv_workplace = (TextView) findViewById(R.id.tv_workplace);
        tv_status = (EditText) findViewById(R.id.tv_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        statuses = new ArrayList<Status>();
        mAdapter = new
                StatusRecyclerAdapter(statuses);
        mRecyclerView.setAdapter(mAdapter);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
//Start Activity To Select Image From Gallery
                    Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                    startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                }
                catch (Exception ex){
                    Toast.makeText(getApplication().getApplicationContext(), ""+ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditPersonalInfo.class));
            }
        });

    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

    }

    private void logout() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();
        finish();
    }

    private void showDialog(){

        ChangePasswordDialog fragment = new ChangePasswordDialog();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EMAIL, mEmail);
        bundle.putString(Constants.TOKEN,mToken);
        fragment.setArguments(bundle);

        fragment.show(getFragmentManager(), ChangePasswordDialog.TAG);
    }

    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        this.user = user;
        name.setText(user.getName() + " " + user.getSurname());
        if(user.getWorkPlace() != null){
            tv_workplace.setText("Working at: " + user.getWorkPlace());
        }
        if(user.getYourInterests() != null){
            tv_interest.setText("Interests: " + user.getYourInterests());
        }

        if (user.getMainPhoto() != null){
            byte[] decodedString = Base64.decode(user.getMainPhoto(), Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePhoto.setImageBitmap(photo);
            profilePhoto.setScaleType(ImageView.ScaleType.FIT_XY);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onPasswordChanged() {

        showSnackBarMessage("Password Changed Successfully!");

    }
}