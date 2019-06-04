package com.benrostudios.flync;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.benrostudios.flync.data.History;
import java.util.Arrays;
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
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.history_list_item, parent, false);
        }

        History historyItem = (History) getItem(position);

        TextView fileNameTextView = convertView.findViewById(R.id.file_name_text_view);
        TextView fileSizeTextView = convertView.findViewById(R.id.file_size_text_view);
        TextView fileTimeTextView = convertView.findViewById(R.id.file_time_text_view);
        ImageView sendReceiveImageView = convertView.findViewById(R.id.send_receive_indicator_image_view);
        ImageView fileTypeImageView = convertView.findViewById(R.id.file_type_image_view);

        fileNameTextView.setText(historyItem.getFileName());
        fileSizeTextView.setText(Formatter.formatFileSize(mContext, historyItem.getFileSize()));
        fileTimeTextView.setText(historyItem.getDateAndTime());
        sendReceiveImageView.setImageResource(getSendReceiveResource(historyItem));
        fileTypeImageView.setImageResource(getFileTypeResource(historyItem));

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

    private String getFileExtension(String fileName)
    {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0)
        {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    private int getSendReceiveResource(History historyItem)
    {

        int sendOrReceive = historyItem.getSendOrReceive();

        if(sendOrReceive == History.RECEIVE)
        {
            return R.drawable.round_call_received_24;
        }
        else
        {
            return R.drawable.round_call_made_24;
        }
    }

    private int getFileTypeResource(History historyItem)
    {
        String extension = getFileExtension(historyItem.getFileName());
        if(imageFileTypes.contains(extension))
        {
            return R.drawable.outline_image_24;
        }
        else if(audioFileTypes.contains(extension))
        {
            return R.drawable.outline_audiotrack_24;
        }
        else if(documentFileTypes.contains(extension))
        {
            return R.drawable.outline_insert_drive_file_24;
        }
        else if(movieFileTypes.contains(extension))
        {
            return R.drawable.outline_movie_24;
        }
        else
        {
            return R.drawable.outline_folder_24;
        }
    }

    private List imageFileTypes = Arrays.asList("tif", "tiff", "bmp", "jpg", "jpeg", "png", "gif", "eps", "raw", "cr2", "nef", "orf", "sr2");
    private List audioFileTypes = Arrays.asList("3gp", "aa", "aac", "aax", "act", "aiff", "amr", "ape", "au", "awb", "dct", "dss", "dvf", "flac", "gsm", "m4a", "m4b", "m4p", "mp3", "nsf", "ogg", "oga", "mogg", "opus", "ra", "rm", "tta", "voc", "wav", "wma", "wv", "webm", "8svx");
    private List documentFileTypes = Arrays.asList("doc", "docx", "log", "msg", "odt", "pages", "rtf", "tex", "txt", "wpd", "wps", "pdf");
    private List movieFileTypes = Arrays.asList("3g2", "3gp", "asf", "flv", "m4v", "mov", "mp4", "mpg", "mpeg", "rm", "srt", "swf", "vob", "wmv");
}
