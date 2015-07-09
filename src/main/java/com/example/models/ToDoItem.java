package com.example.models;

public class ToDoItem {
    private final String text;
    private final boolean completed;
    private final long id;

    public ToDoItem(long id, String text, boolean completed) {
        this.id = id;
        this.text = text;
        this.completed = completed;
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getId() {
        return id;
    }
}
