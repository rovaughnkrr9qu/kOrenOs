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

    public long insertRow(EntityInterface bunchInstance){
        return getWritableDatabase().insert(bunchInstance.getTableName(), null, bunchInstance.getInsertContent());
    }

    public Cursor selectRow(EntityInterface entity){
        return getReadableDatabase().query(entity.getTableName(), null, entity.getSelectionString(), null, null,null, null);
    }

    public int deleteRow(EntityInterface entity){
        return getWritableDatabase().delete(entity.getTableName(), entity.getSelectionString(), null);
    }

    public String getBunchPath(String bunchName){
        Cursor selectedBunch = DATABASE_INSTANCE.selectRow(new Bunch(null, bunchName, null, null, null, null, null));
        selectedBunch.moveToFirst();
        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3]));
    }

    public String getBunchPath(int bunchID){
        Cursor selectedBunch = DATABASE_INSTANCE.selectRow(new Bunch(bunchID, null, null, null, null, null, null));
        selectedBunch.moveToFirst();
        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3]));
    }

    public String getPicturePath(String bunchName, String pictureName, int pictureNumber){
        String bunchPath = getBunchPath(bunchName);

        StringBuilder picturePath = new StringBuilder(bunchPath);
        picturePath.append("/");
        picturePath.append(pictureName);
        picturePath.append(pictureNumber);
        picturePath.append(".jpeg");

        return picturePath.toString();
    }

    public int findBunchID(String bunchName){
        Cursor selectedBunch = DATABASE_INSTANCE.selectRow(new Bunch(null, bunchName, null, null, null, null, null));

        if(selectedBunch.getCount() == 0){
            return -1;
        }
        selectedBunch.moveToFirst();

        return Integer.parseInt(selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[0])));
    }

    /*private static String createInitString( entityClass){
        StringBuilder bunchTableCreator = new StringBuilder("create table ");
        bunchTableCreator.append(entityClass.NAME);
        bunchTableCreator.append("(");
        for(int i = 0; i < entityClass.COLUMN_NAMES.length; i++){
            bunchTableCreator.append(entityClass.COLUMN_NAMES[i]);
            bunchTableCreator.append(" ");
            bunchTableCreator.append(entityClass.COLUMN_TYPES[i]);
            if(entityClass.COLUMN_SPECIFICATIONS.size() > i) {
                bunchTableCreator.append(" ");
                bunchTableCreator.append(entityClass.COLUMN_SPECIFICATIONS.get(i));
            }
            if(i < entityClass.COLUMN_NAMES.length-1 || entityClass.SPECIAL_CONDITION.length != 0) {
                bunchTableCreator.append(",");
            }
        }
        if(entityClass.SPECIAL_CONDITION.length != 0){
            for(int i = 0; i < entityClass.SPECIAL_CONDITION.length; i++) {
                bunchTableCreator.append(entityClass.SPECIAL_CONDITION[i]);
                if(i < entityClass.SPECIAL_CONDITION.length-1){
                    bunchTableCreator.append(",");
                }
            }
        }
        bunchTableCreator.append(");");

        return bunchTableCreator.toString();
    }*/
}
