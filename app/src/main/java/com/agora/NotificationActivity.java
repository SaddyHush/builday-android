package com.agora;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.agora.R;
import com.agora.model.Notification;
import com.agora.utils.NotificationsRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Saddy on 3/1/2018.
 */

public class NotificationActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Notification> notifications;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        notifications = new ArrayList<>();
        notifications.add(new Notification("Sadix Huseynov wants to add himself to your event", "20/20/20", "saddy@yahoo.com"));
        mAdapter = new NotificationsRecyclerAdapter(notifications);
        mRecyclerView.setAdapter(mAdapter);
    }

//    public void notificate(View view){
//        ListView notificationsList = (ListView) findViewById(R.id.notificationListView);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, friends);
//        notificationsList.setAdapter(arrayAdapter);
//        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), friends.get(position), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}
