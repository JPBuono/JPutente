package it.uniba.di.ivu.sms16.gruppo10.justpizza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Alessandro on 31/05/2016.
 */
public class DB_Controller extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_NAME = "  LOGIN ";
    private static final String USER_TABLE_CREATE = "CREATE TABLE  " + DB_TABLE_NAME + " (Email VARCHAR(40),Password VARCHAR (20),PRIMARY KEY(Email,Password));";/*public DB_Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, "LOGIN.db", factory, version);*/

    public DB_Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "LOGIN.db", factory, version);
    }

    // onCreate eseguito alla prima creazione del database,
    // nel quale appunto inizializziamo la query di creazione del DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int DB_VERSION) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS LOGIN;");
        onCreate(sqLiteDatabase);
    }

    public void insertUser(String mail, String password) {
        // creiamo un oggetto di tipo ContentValues
        ContentValues contentValues = new ContentValues();
        // nel campo Email della tabella inseriremo i valori  mail  e password riceviti dal metodo insertUser
        contentValues.put("Email", mail);
        contentValues.put("Password", password);
        //insertWithOnConflict in modo da sovrascrivere eventuali valori ripetuti nonostante abbiamo dichiarato i campi della tabella unique
        this.getWritableDatabase().insertWithOnConflict("LOGIN", "", contentValues,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void dropUser() {
        // cancelliamo tutto i dati salvati nella tabella login
        this.getWritableDatabase().delete("LOGIN",null,null);
    }

    //metodo che ci ritorna i risultati della query
    public Cursor checkDB() {
        Cursor cursor =this.getWritableDatabase().rawQuery("Select * FROM  "+ DB_TABLE_NAME +" ", null);
        return cursor;
      }
}

