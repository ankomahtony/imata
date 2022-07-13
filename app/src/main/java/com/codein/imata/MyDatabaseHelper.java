package com.codein.imata;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "imatadb_fine.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "deposits";
    private static final String TABLE_NAME_2 = "users";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_USER = "user_id";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "dept_date";
    private static final String COLUMN_CUSTOMER = "customer";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_SYNC = "sync";
    private static final String COLUMN_USER_ID = "user_id";

    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user_query = "CREATE TABLE " + TABLE_NAME_2 +
                " (" + COLUMN_USER + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT);";

        db.execSQL(user_query);
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " VARCHAR(64), " +
                COLUMN_CUSTOMER + " VARCHAR(64), " +
                COLUMN_SYNC + " BOOLEAN, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_AMOUNT + " DOUBLE);";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addDeposit(String cutomer, Double amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Integer user_id = MySingletonClass.getInstance().getValue();

        cv.put(COLUMN_CUSTOMER, cutomer);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_SYNC, false);
        cv.put(COLUMN_DATE, formattedDate);
        cv.put(COLUMN_USER_ID, user_id);
        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    void addUser(Integer user_id, String name, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

//        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);

        cv.put(COLUMN_USER, user_id);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_NAME_2,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "User Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME + " where sync=?";
        SQLiteDatabase db = this.getReadableDatabase();
        String syn = "0";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,new String[] {syn});
        }
        return cursor;
    }

    Cursor getUser(String email){
        String query = "SELECT * FROM " + TABLE_NAME_2 + " where email=? limit 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,new String[] {email});
        }
        return cursor;
    }

    void updateData(String row_id, String customer, String date, String amount){
//        String syn = "1";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CUSTOMER, customer);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_AMOUNT, amount);
//        cv.put(COLUMN_SYNC, syn);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }


    void updateDataAfterSync(String row_id, String customer, String date, String amount, Integer user_id){
        String syn = "1";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CUSTOMER, customer);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_SYNC, syn);
        cv.put(COLUMN_USER_ID, user_id);

        long result = db.update(TABLE_NAME, cv, "id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }


}
