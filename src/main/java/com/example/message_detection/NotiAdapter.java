package com.example.message_detection;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NotiAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MessageData> message;

    public NotiAdapter(Context context, ArrayList<MessageData> data) {
        mContext = context;
        message = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void updateAdapter(ArrayList<MessageData> newData) {
        message = newData;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return message.size();
    }

    @Override
    public Object getItem(int position) {
        return message.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView titleno;
        TextView textnoti;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.appnotilist, parent, false);
            holder = new ViewHolder();
            holder.titleno = convertView.findViewById(R.id.titlenoti);
            holder.textnoti = convertView.findViewById(R.id.notitext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String title = message.get(position).getTitle();
        String text = message.get(position).getText();
        holder.titleno.setText(title);
        holder.textnoti.setText(text);
        Log.d("NotiAdapter",title + text);
        return convertView;
    }
}
