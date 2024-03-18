package com.example.warehousev30;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

public class WarehouseDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;
    public WarehouseDataSource(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        dbHelper.copyDatabaseFromResources(context);
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String getItemPlaceById(String itemId) {
        String itemPlace = null;
        String query = "SELECT " + dbHelper.getItemPlaceColumnName() +
                " FROM " + DatabaseHelper.TABLE_WAREHOUSE +
                " WHERE " + dbHelper.COLUMN_ID + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{itemId});
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(dbHelper.getItemPlaceColumnName());
            if (columnIndex >= 0) { // Check if column index is valid
                itemPlace = cursor.getString(columnIndex);
            } else {
                // Handle the case where the column index is -1
                // Log an error message or throw an exception if necessary
            }
            cursor.close();
        }
        return itemPlace;
    }
}