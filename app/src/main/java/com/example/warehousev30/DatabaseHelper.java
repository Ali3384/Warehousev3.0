package com.example.warehousev30;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_WAREHOUSE = "Data";
    public static final String COLUMN_ID = "Item_Id";
    public static final String COLUMN_ITEM_CODE = "Item_Code";
    public static final String COLUMN_ITEM_PLACE = "Item_Place";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to create tables here if you're using a pre-created database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void copyDatabaseFromResources(Context context) {
        File dbFile = context.getDatabasePath("data.db");

        // Check if the database file exists
        if (dbFile.exists()) {
            // If the database file exists, delete it
            if (context.deleteDatabase("data.db")) {
                Log.d("Database", "Existing database file deleted");
            } else {
                Log.e("Database", "Failed to delete existing database file");
                return; // Exit the method if failed to delete
            }
        }

        // Copy the database file from resources to internal storage
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.data);
            OutputStream outputStream = new FileOutputStream(dbFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Log.d("Database", "Database file copied from resources to internal storage");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Database", "Error copying database file from resources to internal storage: " + e.getMessage());
        }
    }

    public void copyDatabaseFromAssets() throws IOException {

        File dbFile = context.getDatabasePath("data.db");

        // Check if the database file exists
        if (dbFile.exists()) {
            // If the database file exists, delete it
            if (context.deleteDatabase("data.db")) {
                Log.d("Database", "Existing database file deleted");
            } else {
                Log.e("Database", "Failed to delete existing database file");
                return; // Exit the method if failed to delete
            }
        }

        // Copy the database file from assets to internal storage
        try {
            InputStream inputStream = context.getAssets().open("Data.db");
            OutputStream outputStream = new FileOutputStream(dbFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Log.d("Database", "Database file copied from assets to internal storage");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Database", "Error copying database file from assets to internal storage: " + e.getMessage());
        }

    }
    public String getItemCodeColumnName() {
        return COLUMN_ITEM_CODE;
    }

    public String getItemPlaceColumnName() {
        return COLUMN_ITEM_PLACE;
    }
}