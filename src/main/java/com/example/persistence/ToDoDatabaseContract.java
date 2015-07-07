package com.example.persistence;

import android.provider.BaseColumns;

public final class ToDoDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ToDoDatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ToDoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_TEXT = "text";
    }
}
