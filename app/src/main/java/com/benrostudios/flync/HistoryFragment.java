package com.benrostudios.flync;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    HistoryAdapter adapter;
    List<History> histories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragview = inflater.inflate(R.layout.fragment_history, null);
        setHasOptionsMenu(true);

        AppDatabase db = AppDatabase.getAppDatabase(getActivity());
        histories = db.historyDao().getAllHistory();
        adapter = new HistoryAdapter(getActivity(), histories);
        ListView itemsListView  = fragview.findViewById(R.id.history_list_view);
        itemsListView.setDivider(null);
        itemsListView.setEmptyView(fragview.findViewById(R.id.empty_list_view));
        itemsListView.setAdapter(adapter);
        return fragview;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.history_fragment_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.delete_history:
                showDeleteConfirmationDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteConfirmationDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.clear_history));
        builder.setMessage(getString(R.string.clear_history_confirmation));
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int arg1)
            {
                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                db.historyDao().deleteAllHistory();
                histories.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        AlertDialog alert = builder.create();
        alert.show();
    }

}