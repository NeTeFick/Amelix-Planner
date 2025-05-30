package com.example.planner;

public class Event {
    private long id;
    private String date;
    private String title;

    public Event(long id, String date, String title) {
        this.id = id;
        this.date = date;
        this.title = title;
    }

    public long getId() { return id; }
    public String getDate() { return date; }
    public String getTitle() { return title; }

    public void setId(long id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTitle(String title) { this.title = title; }
}
