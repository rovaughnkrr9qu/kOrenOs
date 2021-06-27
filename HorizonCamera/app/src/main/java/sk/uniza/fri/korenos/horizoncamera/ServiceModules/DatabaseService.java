package sk.uniza.fri.korenos.horizoncamera.ServiceModules;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.EntityInterface;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;

/**
 * Created by Markos on 12. 11. 2016.
 */

public class DatabaseService extends SQLiteOpenHelper{

    private static final String DB_NAME = "HorizonCameraDatabase";
    private static DatabaseService DATABASE_INSTANCE;

    public static DatabaseService getDbInstance(Context context){
        if(DATABASE_INSTANCE == null){
            DATABASE_INSTANCE = new DatabaseService(context);
        }
        return DATABASE_INSTANCE;
    }

    public DatabaseService(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Bunch.CREATE_TABLE_STRING);
        sqLiteDatabase.execSQL(Frame.CREATE_TABLE_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public long insertRow(EntityInterface entity){
        return getWritableDatabase().insert(entity.getTableName(), null, entity.getInsertContent());
    }

    public Cursor selectRow(EntityInterface entity){
        return getReadableDatabase().query(entity.getTableName(), null, entity.getSelectionString(), null, null,null, null);
    }

    public int deleteRow(EntityInterface entity){
        return getWritableDatabase().delete(entity.getTableName(), entity.getSelectionString(), null);
    }
}
