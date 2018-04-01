package ru.ya.rrmstu.core.abstracts;

import java.util.Calendar;

public abstract class AbstractOperation {

    private long id;
    private Calendar dateTime;
    private String addInfo;

    public AbstractOperation(long id, Calendar dateTime, String addInfo) {
        this.id = id;
        this.dateTime = dateTime;
        this.addInfo = addInfo;
    }

    public AbstractOperation(long id) {
        this.id = id;
    }

    public AbstractOperation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }
}
