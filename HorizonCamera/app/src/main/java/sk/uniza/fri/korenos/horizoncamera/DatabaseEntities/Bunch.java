package sk.uniza.fri.korenos.horizoncamera.DatabaseEntities;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Markos on 12. 11. 2016.
 */

public class Bunch extends EntityInterface{

    public final static String NAME = "Bunch";
    public final static String[] COLUMN_NAMES = {"IDBunch","BunchName", "Date", "Path", "Latitude", "Longitude"};
    public final static String[] COLUMN_TYPES = {"integer","text", "integer", "text", "real", "real"};
    public final static ArrayList<String> COLUMN_SPECIFICATIONS =
            new ArrayList<>(Arrays.asList(
                    "primary key autoincrement",
                    "not null unique",
                    "not null",
                    "not null"));
    public final static String[] SPECIAL_CONDITION = {};
    public static final String CREATE_TABLE_STRING = "create table Bunch(IDBunch integer primary key autoincrement,BunchName text not null unique,Date integer not null,Path text not null,Latitude real,Longitude real);";

    private Integer IDBunch;
    private String BunchName;
    private Integer Date;
    private String Path;
    private Double Latitude;
    private Double Longitude;

    public Bunch(Integer paIDBunch, String paBunchName, Integer paDate, String paPath, Double paLatitude, Double paLongitude) {
        IDBunch = paIDBunch;
        BunchName = paBunchName;
        Date = paDate;
        Path = paPath;
        Latitude = paLatitude;
        Longitude = paLongitude;

        composeAll.add(IDBunch);
        composeAll.add(BunchName);
        composeAll.add(Date);
        composeAll.add(Path);
        composeAll.add(Latitude);
        composeAll.add(Longitude);
    }

    public Integer getIDBunch() {
        return IDBunch;
    }

    public String getBunchName() {
        return BunchName;
    }

    public Integer getDate() {
        return Date;
    }

    public String getPath() {
        return Path;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String getTableName() {
        return NAME;
    }

    @Override
    public ContentValues getInsertContent() {
        ContentValues content = new ContentValues();
        if(IDBunch!=null)content.put("IDBunch", IDBunch);
        if(BunchName!=null)content.put("BunchName", BunchName);
        if(Date!=null)content.put("Date", Date);
        if(Path!=null)content.put("Path", Path);
        if(Latitude!=null)content.put("Latitude", Latitude);
        if(Longitude!=null)content.put("Longitude", Longitude);

        return content;
    }
}
