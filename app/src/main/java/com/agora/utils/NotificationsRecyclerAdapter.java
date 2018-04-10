package com.agora.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agora.R;
import com.agora.model.Notification;

import java.util.ArrayList;

/**
 * Created by asadz on 4/9/18.
 */

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationViewHolder> {
    private ArrayList<Notification> notifications;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView dateTime;
        public ImageView photo;
        public NotificationViewHolder(View v) {
            super(v);
            message = (TextView)v.findViewById(R.id.message);
            dateTime = (TextView)v.findViewById(R.id.dateTime);
            photo = (ImageView)v.findViewById(R.id.image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationsRecyclerAdapter(ArrayList<Notification> notifications) {
        this.notifications=notifications;
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.message.setText(notifications.get(position).getMessage());
        holder.dateTime.setText(notifications.get(position).getDateTime());
//        holder.message.setText(notifications.get(position).get);
        //TODO: Add photo
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
