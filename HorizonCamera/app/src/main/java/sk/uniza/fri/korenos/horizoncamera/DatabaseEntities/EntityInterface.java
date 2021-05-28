package sk.uniza.fri.korenos.horizoncamera.DatabaseEntities;

import android.content.ContentValues;

/**
 * Created by Markos on 12. 11. 2016.
 */

public interface EntityInterface {
    String getTableName();
    ContentValues getInsertContent();
    String getSelectionString();
}
