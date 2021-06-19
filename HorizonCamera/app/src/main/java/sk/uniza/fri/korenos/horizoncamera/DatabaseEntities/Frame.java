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
    public final static String[] COLUMN_NAMES = {"FrameNumber", "FrameName", "IDBunch", "Date", "Format", "Pitch", "Orientation"};
    public final static String[] COLUMN_TYPES = {"integer","text", "integer", "integer", "integer", "real", "real"};
    public final static ArrayList<String> COLUMN_SPECIFICATIONS =
            new ArrayList<>(Arrays.asList(
                    "not null",
                    "not null",
                    "not null references Bunch(IDBunch)",
                    "not null",
                    "not null"));
    public final static String[] SPECIAL_CONDITION = {"primary key (FrameNumber, FrameName, IDBunch)"};
    public static final String CREATE_TABLE_STRING = "create table Frame(FrameNumber integer not null,FrameName text not null,IDBunch integer not null references Bunch(IDBunch),Date integer not null, Format integer not null,Pitch real,Orientation real,primary key (FrameNumber, FrameName, IDBunch));";

    private Integer FrameNumber;
    private String FrameName;
    private Integer IDBunch;
    private Long Date;
    private Integer Format;
    private Double Pitch;
    private Double Orientation;

    public Frame(Integer paFrameNumber, String paFrameName, Integer paIDBunch, Long paDate, Integer paFormat, Double paPitch, Double paOrientation) {
        Pitch = paPitch;
        FrameNumber = paFrameNumber;
        FrameName = paFrameName;
        IDBunch = paIDBunch;
        Date = paDate;
        Format = paFormat;
        Orientation = paOrientation;

        composeAll.add(FrameNumber);
        composeAll.add(FrameName);
        composeAll.add(IDBunch);
        composeAll.add(Date);
        composeAll.add(Format);
        composeAll.add(Pitch);
        composeAll.add(Orientation);
    }

    public Object getDataOfColumn(int columnID){
        switch (columnID){
            case 0: return FrameNumber;
            case 1: return FrameName;
            case 2: return IDBunch;
            case 3: return Date;
            case 4: return Format;
            case 5: return Pitch;
            case 6: return Orientation;
            default:return null;
        }
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

    public Double getPitch() {
        return Pitch;
    }

    public Double getOrientation() {
        return Orientation;
    }

    public Long getDate() {
        return Date;
    }

    public String getFullFrameName(){
        return FrameName+FrameNumber+".jpeg";
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
        if(Date!=null)content.put("Date", Date);
        if(Format!=null)content.put("Format", Format);
        if(Pitch!=null)content.put("Pitch", Pitch);
        if(Orientation!=null)content.put("Orientation", Orientation);

        return content;
    }
}
