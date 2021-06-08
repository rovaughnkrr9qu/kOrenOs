package sk.uniza.fri.korenos.horizoncamera.DatabaseEntities;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by Markos on 12. 11. 2016.
 */

public abstract class EntityInterface {

    protected ArrayList<Object> composeAll = new ArrayList<>();


    public String getTableName(){return null;};

    public ContentValues getInsertContent(){return null;};

    public String[] getColumnNames(){return null;}

    public String getSelectionString(){
        StringBuilder selectionString = new StringBuilder("");

        for(int i = 0; i < getColumnNames().length; i++){
            if(composeAll.get(i) != null) {
                if(selectionString.toString().compareTo("")!=0){
                    selectionString.append(" AND ");
                }
                if (composeAll.get(i).getClass() == String.class) {
                    selectionString.append(getColumnNames()[i] + "='" + (String) composeAll.get(i) + "'");
                }
                if (composeAll.get(i).getClass() == Integer.class) {
                    selectionString.append(getColumnNames()[i] + "=" + (Integer) composeAll.get(i) + "");
                }
                if (composeAll.get(i).getClass() == Double.class) {
                    selectionString.append(getColumnNames()[i] + "=" + (Double) composeAll.get(i) + "");
                }
                if (composeAll.get(i).getClass() == Long.class) {
                    selectionString.append(getColumnNames()[i] + "=" + (Long) composeAll.get(i) + "");
                }
            }
        }

        return selectionString.toString();
    };
}
