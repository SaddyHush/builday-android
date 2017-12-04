package com.agora.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agora.R;
import com.agora.model.Event;
import com.agora.model.Status;

import org.w3c.dom.Text;

import java.util.ArrayList;
/**
 * Created by Saddy on 7/29/2017.
 */

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.StatusHolder> {
    private ArrayList<Status> mEvents;
    public StatusRecyclerAdapter(ArrayList<Status> events) {
        mEvents = events;
    }

    @Override
    public StatusRecyclerAdapter.StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_layout, parent, false);
        return new StatusHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(StatusRecyclerAdapter.StatusHolder holder, int position) {
        Status itemPhoto = mEvents.get(position);
        holder.bindStatus(itemPhoto);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
    public static class StatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUserName;
        private TextView mText;
        private TextView mDay;
        private TextView mMonth;

        public StatusHolder(View v) {
            super(v);

            mDay = (TextView) v.findViewById(R.id.dateDay);
            mMonth = (TextView) v.findViewById(R.id.dateMonth);
            mUserName = (TextView) v.findViewById(R.id.username);
            mText = (TextView) v.findViewById(R.id.text);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
        public void bindStatus(Event event) {
            mDay.setText(event.getEventDateTime());
            mUserName.setText(event.getTitle());
            mText.setText(event.getTitle());
        }
    }
}
