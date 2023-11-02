package com.example.message_detection;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MsgAdapter extends BaseAdapter {
    static final int MAX_LENGTH = 30;
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<MessageData> message;

    public MsgAdapter(Context context, ArrayList<MessageData> data) {
        mContext = context;
        message = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void addMessage(MessageData newMessage) {
        // If the list size is already 30, remove the first item
        if (message.size() >= 30) {
            message.remove(0);
        }

        // Add the new item
        message.add(newMessage);

        // Notify the ListView that the data has changed so it can update itself
        this.notifyDataSetChanged();
    }



    public void updateAdapter() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return message.size();
    }

    public ArrayList<MessageData> getMessageList() {
        return this.message;
    }

    @Override
    public MessageData getItem(int position) {
        return message.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView phn_nbr;
        TextView msg_text;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        int type = message.get(position).getType();

        if (convertView == null) {

            if (type == 1) {
                convertView = mLayoutInflater.inflate(R.layout.noti_view, parent, false);
                holder.msg_text = (TextView) convertView.findViewById(R.id.notiTitle);
                String title = message.get(position).getTitle();
                String AppPackage = message.get(position).getPackage();
                holder.msg_text.setText(title);
                convertView.setTag(holder);

            } else if (type == 2) {
                convertView = mLayoutInflater.inflate(R.layout.msg_view, parent, false);
                holder.phn_nbr = (TextView) convertView.findViewById(R.id.phone_nbr);
                holder.msg_text = (TextView) convertView.findViewById(R.id.msg_text);
                holder.phn_nbr.setText(message.get(position).getTitle());
                Log.d("getView", "phonenumber" + message.get(position).getTitle());

                String text = message.get(position).getText();
                text = text.replace("\n", " ");

                if (text.length() > MAX_LENGTH) {
                    text = text.substring(0, MAX_LENGTH) + "...";
                }

                holder.msg_text.setText(text);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
