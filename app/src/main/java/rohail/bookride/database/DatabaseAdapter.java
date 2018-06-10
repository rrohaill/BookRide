package rohail.bookride.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import rohail.bookride.models.BookingModel;


public class DatabaseAdapter {

    public static final int NAME_COLUMN = 1;
    static final String DATABASE_NAME = "bookride.db";
    static final int DATABASE_VERSION = 1;
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table History( ID integer primary key autoincrement,NAME  text,CONTACT  text,PICKUP_LOCATION text,DROPOFF_LOCATION text, PICKUP_TIME text, CAR_TYPE, text); ";
    // Variable to hold the database instance
    public static SQLiteDatabase db;
    // Database open/upgrade helper
    private static DataBaseHelper dbHelper;
    // Context of the application using the database.
    private final Context context;
    String ok = "OK";

    public DatabaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method to openthe Database
    public DatabaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    // Method to close the Database
    public void close() {
        db.close();
    }

    // method returns an Instance of the Database
    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    // method to insert a record in Table
    public String insertEntry(String name, String contact, String pickupLocation, String dropoffLocation, String pickupTime, String carType) {

        try {

            ContentValues newValues = new ContentValues();
            // Assign values for each column.
            newValues.put("NAME", name);
            newValues.put("CONTACT", contact);
            newValues.put("PICKUP_LOCATION", pickupLocation);
            newValues.put("DROPOFF_LOCATION", dropoffLocation);
            newValues.put("PICKUP_TIME", pickupTime);
            newValues.put("CAR_TYPE", carType);


            // Insert the row into your table
            db = dbHelper.getWritableDatabase();
            long result = db.insert("History", null, newValues);
            System.out.print(result);
            Toast.makeText(context, "User Info Saved", Toast.LENGTH_LONG).show();


        } catch (Exception ex) {
            System.out.println("Exceptions " + ex);
            Log.e("Note", "One row entered");
        }
        return ok;
    }

    // method to delete a Record of UserName
    public int deleteEntry(String name) {

        String where = "NAME=?";
        int numberOFEntriesDeleted = db.delete("History", where, new String[]{name});
        Toast.makeText(context, "Number fo Entry Deleted Successfully : " + numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }

    //method to get all records
    public ArrayList<BookingModel> selectAllProducts() throws ParseException {

        ArrayList<BookingModel> transactionsList = new ArrayList<BookingModel>();

        String selectQuery = "SELECT * FROM " + "History";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BookingModel trx = new BookingModel();

                trx.setName(cursor.getString(1));
                trx.setContact(cursor.getString(2));
                trx.setPickupLocation(cursor.getString(3));
                trx.setDropLocation(cursor.getString(4));
                trx.setPickupTime(cursor.getString(5));
                trx.setCarType(cursor.getString(6));
                // Adding Transaction
                transactionsList.add(trx);
            } while (cursor.moveToNext());
        }
        return transactionsList;
    }

}
