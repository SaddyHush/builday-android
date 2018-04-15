package com.agora.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agora.OtherUserProfileActivity;
import com.agora.R;
import com.agora.model.Notification;
import com.agora.model.Response;
import com.agora.model.User;
import com.agora.network.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by asadz on 4/9/18.
 */

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationViewHolder> {
    private ArrayList<Notification> notifications;
    private CompositeSubscription mSubscriptions;
    private Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView dateTime;
        public ImageView photo;
        public NotificationViewHolder(View v) {
            super(v);
            context = v.getContext();
            message = (TextView)v.findViewById(R.id.message);
            dateTime = (TextView)v.findViewById(R.id.dateTime);
            photo = (ImageView)v.findViewById(R.id.image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationsRecyclerAdapter(ArrayList<Notification> notifications) {
        this.notifications=notifications;
        mSubscriptions = new CompositeSubscription();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_layout, parent, false);
        NotificationViewHolder vh = new NotificationViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        loadOtherUser(notifications.get(position).getSecondUserID(), holder);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.message.setText(notifications.get(position).getBody());
        holder.dateTime.setText(notifications.get(position).getDateTime());
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherUserProfileActivity.class);
                intent.putExtra("user", notifications.get(position).getSecondUserID());
                intent.putExtra("event", notifications.get(position).getEventID());
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void loadOtherUser(String otherUsersEmail, NotificationViewHolder holder) {
        mSubscriptions.add(NetworkUtil.getRetrofit().getOtherUserProfile(otherUsersEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((User user) ->handleLoadOtherUserResponse(user, holder), this::handleLoadOtherUserError));
    }

    private void handleLoadOtherUserResponse(User user, NotificationViewHolder holder) {
        byte[] decodedString = Base64.decode(user.getMainPhotoSmall(), Base64.DEFAULT);
        Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.photo.setImageBitmap(photo);
    }

    private void handleLoadOtherUserError(Throwable error) {
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
//                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            showSnackBarMessage("Network Error !");
            error.printStackTrace();
        }
    }
}

