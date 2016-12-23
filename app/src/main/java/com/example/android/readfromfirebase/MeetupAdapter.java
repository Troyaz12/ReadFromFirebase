package com.example.android.readfromfirebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by TroysMacBook on 12/23/16.
 */

public class MeetupAdapter extends ArrayAdapter<ScheduleMeetup> {
    public MeetupAdapter(Context context, int resource, List<ScheduleMeetup> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);

        ScheduleMeetup message = getItem(position);


            messageTextView.setText(message.getMessage());



        return convertView;
    }
}
