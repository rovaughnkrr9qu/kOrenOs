package sk.uniza.fri.korenos.horizoncamera.DatabaseEntities;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Markos on 12. 11. 2016.
 */

public class Frame extends EntityInterface{

    public final static String NAME = "Frame";
    public final static String[] COLUMN_NAMES = {"FrameNumber", "FrameName", "IDBunch", "Format", "Angle", "Orientation"};
    public final static String[] COLUMN_TYPES = {"integer","text",  "integer", "integer", "real", "real"};
    public final static ArrayList<String> COLUMN_SPECIFICATIONS =
            new ArrayList<>(Arrays.asList(
                    "not null",
                    "not null",
                    "not null references Bunch(IDBunch)",
                    "not null"));
    public final static String[] SPECIAL_CONDITION = {"primary key (FrameNumber, FrameName)"};
    public static final String CREATE_TABLE_STRING = "create table Frame(FrameNumber integer not null,FrameName text not null,IDBunch integer not null references Bunch(IDBunch),Format integer not null,Angle real,Orientation real,primary key (FrameNumber, FrameName));";

    private Integer FrameNumber;
    private String FrameName;
    private Integer IDBunch;
    private Integer Format;
    private Double Angle;
    private Double Orientation;

    public Frame(Integer paFrameNumber, String paFrameName, Integer paIDBunch, Integer paFormat, Double paAngle, Double paOrientation) {
        Angle = paAngle;
        FrameNumber = paFrameNumber;
        FrameName = paFrameName;
        IDBunch = paIDBunch;
        Format = paFormat;
        Orientation = paOrientation;

        composeAll.add(FrameNumber);
        composeAll.add(FrameName);
        composeAll.add(IDBunch);
        composeAll.add(Format);
        composeAll.add(Angle);
        composeAll.add(Orientation);
    }

    public Integer getFrameNumber() {
        return FrameNumber;
    }

    public String getFrameName() {
        return FrameName;
    }

    public Integer getIDBunch() {
        return IDBunch;
    }

    public Integer getFormat() {
        return Format;
    }

    public Double getAngle() {
        return Angle;
    }

    public Double getOrientation() {
        return Orientation;
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
        if(FrameNumber!=null)content.put("FrameNumber", FrameNumber);
        if(FrameName!=null)content.put("FrameName", FrameName);
        if(IDBunch!=null)content.put("IDBunch", IDBunch);
        if(Format!=null)content.put("Format", Format);
        if(Angle!=null)content.put("Angle", Angle);
        if(Orientation!=null)content.put("Orientation", Orientation);

        return content;
    }
}
