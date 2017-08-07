package com.agora.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agora.R;
import com.agora.model.Status;

import java.util.ArrayList;
/**
 * Created by Saddy on 7/29/2017.
 */

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.StatusHolder> {
    private ArrayList<Status> mStatuses;
    public StatusRecyclerAdapter(ArrayList<Status> statuses) {
        mStatuses = statuses;
    }

    @Override
    public StatusRecyclerAdapter.StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_layout, parent, false);
        return new StatusHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(StatusRecyclerAdapter.StatusHolder holder, int position) {
        Status itemPhoto = mStatuses.get(position);
        holder.bindStatus(itemPhoto);
    }

    @Override
    public int getItemCount() {
        return mStatuses.size();
    }
    public static class StatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUserName;
        private TextView mText;

        public StatusHolder(View v) {
            super(v);

            mUserName = (TextView) v.findViewById(R.id.username);
            mText = (TextView) v.findViewById(R.id.text);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
        public void bindStatus(Status status) {
            mUserName.setText(status.getName());
            mText.setText(status.getText());
        }
    }
}
