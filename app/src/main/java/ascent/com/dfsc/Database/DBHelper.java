package ascent.com.dfsc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ascent.com.dfsc.Modal.ResourceGetSet;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FSC.db";
    public static final String RESOURCE_TABLE_NAME = "resource";
    public static final String CART_COLUMN_ID = "id";

    private HashMap hp;
    boolean bol = false;
    String givengift = "";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.e("here", "db created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
                "create table resource " +
                        "(id integer primary key,resourceid text,key text, culture text,value text)"
        );

        Log.e("here", "table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    public void deleteDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete from cart");
        db.execSQL("DELETE FROM cart"); //delete all rows in a table
        db.close();
    }

    public boolean countRowsInResource() {

        String countQuery = "SELECT  * FROM " + "resource";
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        Boolean temp = false;
        if (cnt == 0) {
            temp = false;
        } else {
            temp = true;
        }
        return temp;
    }

    public void insertResource(HashMap<String, String> res) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < res.size(); i++) {
            values.put("resourceid", res.get("resourceid"));
            values.put("key", res.get("key"));
            values.put("culture", res.get("culture"));
            values.put("value", res.get("value"));
        }
        //etc
        long rowInserted = database.insert("resource", null, values);
        database.close();
    }

    public String getResource(String key) {
        List<ResourceGetSet> getResourceData = new ArrayList<ResourceGetSet>();
        String selectQuery = "SELECT  value FROM resource where key = " + "'" + key + "'";
        String res = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                //ResourceGetSet message = new ResourceGetSet();
                res = cursor.getString(0);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return res;
    }

    public boolean deleteResouce() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM resource"); //delete all rows in a table
        db.close();
        return true;
    }


}