package com.example.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ToDo.db";

    private static DbMigration[] migrations = {
            new DbMigration() {
                @Override
                public void migrate(SQLiteDatabase db) {
                    db.execSQL("CREATE TABLE " + ToDoDatabaseContract.ToDoEntry.TABLE_NAME + " (" +
                            ToDoDatabaseContract.ToDoEntry._ID + " INTEGER PRIMARY KEY," +
                            ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_TEXT + " TEXT" +
                            " )");
                }
            },
            new DbMigration() {
                @Override
                public void migrate(SQLiteDatabase db) {
                    db.execSQL("ALTER TABLE " + ToDoDatabaseContract.ToDoEntry.TABLE_NAME + " ADD " + ToDoDatabaseContract.ToDoEntry.COLUMN_NAME_COMPLETED + " INTEGER");
                }
            }
    };

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, migrations.length);
    }

    public void onCreate(SQLiteDatabase db) {
        for (DbMigration migration : migrations) {
            migration.migrate(db);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
        for (int version = oldVersion; version < newVersion; version++) {
            migrations[version].migrate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private interface DbMigration {
        void migrate(SQLiteDatabase db);
    }
}
