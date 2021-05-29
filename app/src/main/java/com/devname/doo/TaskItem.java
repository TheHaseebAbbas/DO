package com.devname.doo;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskItem implements Parcelable {
    private final String task;
    private final String date;

    public TaskItem(String task, String date) {
        this.task = task;
        this.date = date;
    }

    protected TaskItem(Parcel in) {
        task = in.readString();
        date = in.readString();
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(task);
        dest.writeString(date);
    }
}
