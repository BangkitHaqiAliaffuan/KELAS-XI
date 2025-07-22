package com.kelasxi.sqlitedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "Eps27";
    private static final int VERSION = 4;
    SQLiteDatabase db;
    
    public Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        db = this.getWritableDatabase();
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tblbarang table with proper data types
        String createTableQuery = "CREATE TABLE tblbarang (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "barang TEXT, " +
                "stok INTEGER, " +
                "harga REAL" +
                ")";
        db.execSQL(createTableQuery);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate table to update structure
        db.execSQL("DROP TABLE IF EXISTS tblbarang");
        onCreate(db);
    }
    
    // Method to execute SQL queries with result handling
    public boolean runSQL(String sql) {
        try {
            db.execSQL(sql);
            return true; // Insert successful
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Insert failed
        }
    }
    
    // Method to execute SELECT queries and return Cursor
    public Cursor select(String SQL) {
        try {
            return db.rawQuery(SQL, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to execute SELECT queries with WHERE clause
    public Cursor selectWhere(String whereClause) {
        try {
            String sql = "SELECT * FROM tblbarang WHERE " + whereClause + " ORDER BY barang ASC";
            return db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to search by item name (practical example)
    public Cursor searchByName(String itemName) {
        try {
            String sql = "SELECT * FROM tblbarang WHERE barang LIKE '%" + itemName + "%' ORDER BY barang ASC";
            return db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to filter by stock range (practical example)
    public Cursor filterByStockRange(int minStock, int maxStock) {
        try {
            String sql = "SELECT * FROM tblbarang WHERE stok >= " + minStock + " AND stok <= " + maxStock + " ORDER BY stok ASC";
            return db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to filter by price range (practical example)
    public Cursor filterByPriceRange(double minPrice, double maxPrice) {
        try {
            String sql = "SELECT * FROM tblbarang WHERE harga >= " + minPrice + " AND harga <= " + maxPrice + " ORDER BY harga ASC";
            return db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
