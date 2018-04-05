package com.agora.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.agora.R;

import java.util.ArrayList;

/**
 * Created by Saddy on 3/1/2018.
 */

public class Notification extends Activity {
    ArrayList<String> friends = new ArrayList<String>();

    public void notificate(View view){
        ListView notificationsList = (ListView) findViewById(R.id.notificationListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, friends);
        notificationsList.setAdapter(arrayAdapter);
        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), friends.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_notification);
        friends.add("Asad");
        friends.add("Seymur");
        friends.add("Qalib");
    }
}
