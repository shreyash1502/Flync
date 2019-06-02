package com.benrostudios.flync.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "history")
public class History
{

    public final static int SEND = 0;
    public final static int RECEIVE = 1;

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "send_or_receive")
    private int sendOrReceive;

    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "file_size")
    private long fileSize;

    @ColumnInfo(name = "computer_name")
    private String computerName;

    @ColumnInfo(name = "date_and_time")
    private String dateAndTime;

    public void setUid(int id)
    {
        uid = id;
    }

    public History(int sendOrReceive, String fileName, long fileSize, String computerName, String dateAndTime)
    {
        this.sendOrReceive = sendOrReceive;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.computerName = computerName;
        this.dateAndTime = dateAndTime;
    }

    public int getUid()
    {
        return uid;
    }

    public int getSendOrReceive()
    {
        return sendOrReceive;
    }

    public String getFileName()
    {
        return fileName;
    }

    public long getFileSize()
    {
        return fileSize;
    }

    public String getComputerName()
    {
        return computerName;
    }

    public String getDateAndTime()
    {
        return dateAndTime;
    }
}
