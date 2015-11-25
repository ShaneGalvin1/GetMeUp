package com.example.valentincomte.getup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.valentincomte.getup.AlarmModel;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<AlarmModel> alarmItems;
    private ImageButton alarmOn, alarmOff;
    private AlarmModel a;
    private TextView time;

    public CustomListAdapter(Activity activity, List<AlarmModel> alarmItems) {
        this.activity = activity;
        this.alarmItems = alarmItems;
        if(this.alarmItems == null){
            this.alarmItems = new ArrayList<AlarmModel>();
        }
    }

    public void setAlarms(List<AlarmModel> alarmitems) {
        this.alarmItems = alarmitems;
    }

    public List<AlarmModel> getAlarms() {
        return this.alarmItems;
    }

    @Override
    public int getCount() {
        if(alarmItems == null){
            return 0;
        }else {
            return alarmItems.size();
        }
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
        ImageView talk = (ImageView) convertView.findViewById(R.id.speakIcon);
        ImageView shake = (ImageView) convertView.findViewById(R.id.shakeIcon);
        ImageView puzzle = (ImageView) convertView.findViewById(R.id.puzzleIcon);

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
        /*
        alarmOn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alarmOff.setVisibility(View.VISIBLE);
                alarmOn.setVisibility(View.GONE);
                a.setOn(false);
            }
        });


        alarmOff.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alarmOff.setVisibility(View.GONE);
                alarmOn.setVisibility(View.VISIBLE);
                a.setOn(true);
            }
        });
        */



        // getting alarm data for the row
        a = alarmItems.get(position);

        // name
        name.setText(a.name);

        // time
        time.setText(a.Hour + " : " + a.Minute);

        // days
        days.setText(a.getStringDays());


        if(!a.isEnabled)
        {
            alarmOn.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }
        if(a.isEnabled)
        {
            alarmOn.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        }

        if(!a.walk)
        {
            gps.setAlpha(((float) 0.2));
        }
        else
        {
            gps.setAlpha(((float) 1.0));
        }
        if(!a.shake)
        {
            shake.setAlpha(((float) 0.2));
        }
        else
        {
            shake.setAlpha(((float) 1.0));
        }
        if(!a.talk)
        {
            talk.setAlpha(((float) 0.2));
        }
        else
        {
            talk.setAlpha(((float) 1.0));
        }
        if(!a.puzzle)
        {
            puzzle.setAlpha(((float) 0.2));
        }
        else
        {
            puzzle.setAlpha(((float) 1.0));
        }
        return convertView;
    }

}
