package com.agora.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agora.R;
import com.agora.model.Event;
import com.agora.model.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Saddy on 7/29/2017.
 */

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.EventHolder> {
    private ArrayList<Event> mEvents;
    public StatusRecyclerAdapter(ArrayList<Event> events) {
        mEvents = events;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_layout, parent, false);
        return new EventHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Event itemPhoto = mEvents.get(position);
        holder.bindStatus(itemPhoto);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
    public static class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUserName;
        private TextView mText;
        private TextView mDay;
        private TextView mMonth;
        private TextView mUsersJoined;

        public EventHolder(View v) {
            super(v);

            mDay = (TextView) v.findViewById(R.id.dateDay);
            mMonth = (TextView) v.findViewById(R.id.dateMonth);
            mUserName = (TextView) v.findViewById(R.id.username);
            mText = (TextView) v.findViewById(R.id.text);
            mUsersJoined = (TextView) v.findViewById(R.id.usersJoined);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
        public void bindStatus(Event event) {
            String [] splits = event.getEventDateTime().split(" ");
            mDay.setText(splits[2]);
            mMonth.setText(splits[1]);
            mUserName.setText(event.getTitle());
            mText.setText("Interest Category: " + event.getInterest());
            mUsersJoined.setText("Users joined: " + event.getAcceptedUserID().size() + "/" + (int)event.getUsersLimit());
        }
    }
}
