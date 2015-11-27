package com.example.shane.getmeup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Shane on 25/11/2015.
 */
public class CalendarAdapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Alarm> alarmItems;
    private Alarm a;
    private TextView time, title, created;
    private Button create;
    private NumberPicker hours, mins;

    public CalendarAdapter(Activity activity) {
        this.activity = activity;
    }

    public CalendarAdapter(Activity activity, List<Alarm> alarmItems) {
        this.activity = activity;
        this.alarmItems = alarmItems;
    }
    public void setList(List<Alarm> alarmItems) {
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
            convertView = inflater.inflate(R.layout.row_main, parent, false);


        time = (TextView) convertView.findViewById(R.id.event_time);
        title = (TextView) convertView.findViewById(R.id.event_title);
        created = (TextView) convertView.findViewById(R.id.created);
        hours = (NumberPicker) convertView.findViewById(R.id.hours);
        mins = (NumberPicker) convertView.findViewById(R.id.mins);
        create = (Button) convertView.findViewById(R.id.create);
        create.setFocusable(false);
        create.setFocusableInTouchMode(false);
        create.setTag(position);

        // getting alarm data for the row
        a = alarmItems.get(position);
        if(a.getOn()) {
            create.setVisibility(View.GONE);
            created.setVisibility(View.VISIBLE);
        }
        else {
            created.setVisibility(View.GONE);
            create.setVisibility(View.VISIBLE);
        }

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });

        title.setText(a.getName());
        time.setText(a.getTime() + " - " + a.getDays());
        hours.setMaxValue(12);
        hours.setMinValue(0);
        hours.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        mins.setMaxValue(59);
        mins.setMinValue(0);
        mins.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        return convertView;
    }

}
