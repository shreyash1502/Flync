package com.benrostudios.flync.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface HistoryDao
{
    @Insert
    void insert(History historyItem);

    @Delete
    void delete(History historyItem);

    @Query("SELECT * from history ORDER BY uid DESC")
    List<History> getAllHistory();

    @Query("SELECT COUNT(*) FROM history")
    int getCount();

    @Query("DELETE FROM history")
    void deleteAllHistory();
}
