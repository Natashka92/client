package com.example.client.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.client.model.Pizza;
import com.example.client.constants.Constants;
import java.util.ArrayList;


public class PizzashopDatabase extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase dealsDB;

    public PizzashopDatabase(Context context){
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table " +
                Constants.TABLE_PIZZA + " ( " +
                Constants.PIZZA_ID + " integer primary key autoincrement, " +
                Constants.PIZZA_NAME + " text, " +
                Constants.PIZZA_PRICE + " real )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addDataToPizza(Pizza pizza){
        dealsDB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.PIZZA_NAME, pizza.getName());
        values.put(Constants.PIZZA_PRICE, pizza.getPrice());

        if(dealsDB.insert(Constants.TABLE_PIZZA, null, values) == -1){
            Log.e("ERROR", "Can not write to DB");
        }
        dealsDB.close();
    }

    public void addDataToPizza(ArrayList<Pizza> list){
        if(list != null){
            for(int i=0; i<list.size(); i++){
                addDataToPizza(list.get(i));
            }
        }
    }

    public void getValidDataFromPizza(ArrayList<Pizza> list){
        dealsDB = getWritableDatabase();
        String[] columns = new String[]{Constants.PIZZA_NAME, Constants.PIZZA_PRICE};
        Cursor cursor = dealsDB.query(Constants.TABLE_PIZZA, columns, null, null, null, null, null);
        list.clear();

        if(!cursor.isAfterLast()){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Pizza temp = new Pizza();
                temp.setName(cursor.getString(cursor.getColumnIndex(Constants.PIZZA_NAME)));
                temp.setPrice(Double.valueOf(cursor.getString(cursor.getColumnIndex(Constants.PIZZA_PRICE))));
                list.add(temp);
                cursor.moveToNext();
            }
        }
        cursor.close();
        dealsDB.close();
    }
}
