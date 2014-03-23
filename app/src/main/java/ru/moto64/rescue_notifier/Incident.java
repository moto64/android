package ru.moto64.rescue_notifier;

import java.util.Date;

/**
 * Created by ehorohorin on 23/03/14.
 */

public class Incident {

    //private variables
    int _id;
    private String _date;
    private String _title;
    private String _text;
    private String _url;
    private int _isRead;



    // constructor
    public Incident(int id, String date, String title, String text, String url, int isRead){
        this._id = id;
        this._date = date;
        this._title = title;
        this._text = text;
        this._url = url;
        this._isRead = isRead;
    }


    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting date
    public String getDate(){
        return this._date;
    }

    // setting date
    public void setDate(String date){
        this._date = date;
    }

    // getting text
    public String getText(){
        return this._text;
    }

    // setting text
    public void setText(String text){
        this._text = text;
    }

    // getting title
    public String getTitle(){
        return this._title;
    }

    // setting title
    public void setTitle(String title){
        this._title = title;
    }

    public String getUrl() {
        return this._url;
    }

    public void setUrl(String url) {
        this._url = url;
    }

    public int getIsRead() {
        return this._isRead;
    }

    public void setReadable(int isRead) {
        this._isRead = isRead;
    }

    public String toString() {
        return getTitle();
    }
}