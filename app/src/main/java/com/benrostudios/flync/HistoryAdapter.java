package com.benrostudios.flync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.benrostudios.flync.data.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter
{
    private Context mContext;
    private List<History> histories;

    public HistoryAdapter(Context context, List<History> histories)
    {
        mContext = context;
        this.histories = histories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.history_list_item, parent, false);
        }

        History historyItem = (History) getItem(position);

        TextView mFileNameTextView = convertView.findViewById(R.id.file_name_text_view);
        TextView mFileSizeTextView = convertView.findViewById(R.id.file_size_text_view);
        TextView mFileTimeTextView = convertView.findViewById(R.id.file_time_text_view);

        mFileNameTextView.setText(historyItem.getFileName());
        mFileSizeTextView.setText(String.valueOf(historyItem.getFileSize()));
        mFileTimeTextView.setText(historyItem.getDateAndTime());

        return convertView;
    }

    @Override
    public int getCount()
    {
        return histories.size();
    }

    @Override
    public Object getItem(int position)
    {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}
