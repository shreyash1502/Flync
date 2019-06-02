package com.benrostudios.flync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benrostudios.flync.data.AppDatabase;
import com.benrostudios.flync.data.History;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragview = inflater.inflate(R.layout.fragment_history, null);

        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
        List<History> histories = db.historyDao().getAllHistory();
        HistoryAdapter adapter = new HistoryAdapter(getActivity(), histories);
        ListView itemsListView  = fragview.findViewById(R.id.history_list_view);
        itemsListView.setAdapter(adapter);

        return fragview;
    }

}