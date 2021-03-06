package com.example.app;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;


public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Message> mMessages;
    private int lastItemClicked;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
        lastItemClicked = -1;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    public void positionClicked(int position){
        lastItemClicked = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = (Message) this.getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if(message.isText){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_row, parent, false);
                holder.message = (TextView) convertView.findViewById(R.id.message_text);
                holder.timestamp = (TextView)convertView.findViewById(R.id.msgTimestamp);
            }
            if(message.isAudio){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_audio_row, parent, false);
                holder.message = (TextView) convertView.findViewById(R.id.message_audio_text);
                holder.timestamp = (TextView)convertView.findViewById(R.id.msgAudioTimestamp);
                if(position == lastItemClicked){
                    Log.w("MessageAdapter", "Entered position last item clicked code");
                    ImageView temp = (ImageView)convertView.findViewById(R.id.message_audio_play);
                    temp.setImageResource(R.drawable.ic_action_pause);
                    parent.childDrawableStateChanged(temp);
                }
                if(message.isPlayingAudio){
                    Log.w("MessageAdapter", "Entered position last item clicked code");
                    ImageView temp = (ImageView)convertView.findViewById(R.id.message_audio_play);
                    temp.setImageResource(R.drawable.ic_action_pause);
                    parent.childDrawableStateChanged(temp);
                }
                if(!message.isPlayingAudio) {
                    Log.w("MessageAdapter", "Entered position last item clicked code");
                    ImageView temp = (ImageView) convertView.findViewById(R.id.message_audio_play);
                    temp.setImageResource(R.drawable.ic_action_play);
                    parent.childDrawableStateChanged(temp);
                }
            }

            if(convertView!= null)
                convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if(message.isAudio){
            holder.message.setText(" ");
        }
        else{
            holder.message.setText(message.getMessage());
        }
        holder.timestamp.setText(message.getTimestamp());

        LayoutParams messagelp = (LayoutParams) holder.message.getLayoutParams();
        LayoutParams timestamplp = (LayoutParams) holder.timestamp.getLayoutParams();
        //check if it is a status message then remove background, and change text color.
        if (message.isStatusMessage()) {
            holder.message.setBackgroundDrawable(null);
            messagelp.gravity = Gravity.LEFT;
            holder.message.setTextColor(R.color.textColor);
        } else {
            //Check whether message is mine to show right speech bubble and align to right
            if (message.isMine()) {
                holder.message.setBackgroundResource(R.drawable.speech_bubble_right);
                messagelp.gravity = Gravity.RIGHT;
                timestamplp.gravity = Gravity.RIGHT;
            }
            //If not mine then it is from sender to show left speech bubble and align to left
            else {
                holder.message.setBackgroundResource(R.drawable.speech_bubble_left);
                messagelp.gravity = Gravity.LEFT;
                timestamplp.gravity = Gravity.LEFT;
            }
            holder.message.setLayoutParams(messagelp);
            holder.timestamp.setLayoutParams(timestamplp);
            holder.message.setTextColor(R.color.textColor);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView message;
        TextView timestamp;
    }

    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return position;
    }

}