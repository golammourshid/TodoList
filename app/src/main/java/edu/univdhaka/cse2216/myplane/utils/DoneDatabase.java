package edu.univdhaka.cse2216.myplane.utils;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import edu.univdhaka.cse2216.myplane.domain.Tasks;

public class DoneDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "doneData.db";
    private static final String TABLE_NAME = "plan_details";
    private static final String ID = "_ID";
    private static final String TASK_NAME = "TASK_NAME";
    private static final String TASK_DATE = "TASK_DATE";
    private static final String TASK_TIME = "TASK_TIME";
    private static final String DONE_TIME = "DONE_TIME";
    private static final String TASK_TYPE = "TASK_TYPE";
    private static final String TASK_ALARM = "TASK_ALARM";
    private static final String TASK_DETAILS = "TASK_DETAILS";
    private static final String ITEMS = ID + "," + TASK_NAME + "," + TASK_DATE + "," + TASK_TIME + "," + TASK_TYPE + "," + TASK_ALARM;
    private static final String SELECT_ALL = "SELECT " +ITEMS+" FROM "+TABLE_NAME+" ORDER BY "+TASK_DATE+" DESC,"+TASK_TIME+" DESC ;";
    private static final String SELECT_TASK_DETAILS = "SELECT " + ID + "," + TASK_DETAILS + " FROM " + TABLE_NAME + ";";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_NAME + " VARCHAR (255)," + TASK_DATE + " VARCHAR(25)," + TASK_TIME + " VARCHAR(25)," + TASK_TYPE + " VARCHAR(25)," + TASK_ALARM + " VARCHAR (15), " + TASK_DETAILS + " VARCHAR );";

    private static final int VERSION = 1;

    private Context context;

    public DoneDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            //.makeText(context, "TABLE CREATED ", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Toast.makeText(context, "Exception " + e, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        try {
            //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            onCreate(sqLiteDatabase);
        } catch (Exception e) {
            //Toast.makeText(context, "Exception " + e, Toast.LENGTH_LONG).show();
        }
    }

    public long insertData(Tasks tasks) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = getContentValues(tasks);
        return sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_ALL, null);
        return cursor;
    }

    public String getTaskDetails(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SELECT_TASK_DETAILS, null);
        String ans = "";
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String curID = cursor.getString(0);
                if (id.equalsIgnoreCase(curID)) {
                    ans = cursor.getString(1);
                    break;
                }
            }
        }
        return ans;
    }

    public int deleteData(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, ID + " = ?", new String[]{id});
    }

    @NonNull
    private ContentValues getContentValues(Tasks tasks) {
        ContentValues values = new ContentValues();
        values.put(TASK_NAME, tasks.getTask_name());
        values.put(TASK_DATE, tasks.getTask_date());
        values.put(TASK_TIME, tasks.getTask_time());
        values.put(TASK_TYPE, tasks.getTask_type());
        if (tasks.getIsAlarm() == 0)
            values.put(TASK_ALARM, "no");
        else values.put(TASK_ALARM, "yes");
        values.put(TASK_DETAILS, tasks.getTask_details());
        return values;
    }
}


