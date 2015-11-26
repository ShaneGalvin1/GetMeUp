package com.example.shane.getmeup;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shane on 10/11/2015.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Alarm> alarmItems;
    private ImageButton alarmOn, alarmOff;
    private Alarm a;
    private TextView time;

    public CustomListAdapter(Activity activity, List<Alarm> alarmItems) {
        this.activity = activity;
        this.alarmItems = alarmItems;
    }

    @Override
    public int getCount() {
        return alarmItems.size();
    }

    @Override
    public Object getItem(int location) {
        return alarmItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_alarm, parent, false);


        time = (TextView) convertView.findViewById(R.id.time);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView days = (TextView) convertView.findViewById(R.id.days);
        ImageView gps = (ImageView) convertView.findViewById(R.id.gpsIcon);
        ImageView speak = (ImageView) convertView.findViewById(R.id.speakIcon);
        ImageView shake = (ImageView) convertView.findViewById(R.id.shakeIcon);


        alarmOn = (ImageButton) convertView.findViewById(R.id.alarmButton);
        alarmOn.setFocusable(false);
        alarmOn.setFocusableInTouchMode(false);
        alarmOn.setTag(position);
        alarmOff = (ImageButton) convertView.findViewById(R.id.alarmButton2);
        alarmOff.setFocusable(false);
        alarmOff.setFocusableInTouchMode(false);
        alarmOff.setTag(position);
        alarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });
        alarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });

        // getting alarm data for the row
        a = alarmItems.get(position);

        // name
        name.setText(a.getName());

        // time
        time.setText(a.getTime());

        // days
        days.setText(a.getDays());

        //Set which imagebutton is shown
        if(!a.getOn())
        {
            alarmOn.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }
        if(a.getOn())
        {
            alarmOn.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        }

        if(!a.getWalk())
        {
            gps.setAlpha(((float) 0.2));
        }
        else
        {
            gps.setAlpha(((float) 1.0));
        }
        if(!a.getShake())
        {
            shake.setAlpha(((float) 0.2));
        }
        else
        {
            shake.setAlpha(((float) 1.0));
        }
        if(!a.getSpeak())
        {
            speak.setAlpha(((float) 0.2));
        }
        else
        {
            speak.setAlpha(((float) 1.0));
        }

        return convertView;
    }

}