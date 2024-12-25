package com.drone.app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static final String DB_NAME = "contacts_db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public DbHelper(Context context) {
        db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY, name TEXT, phoneNo TEXT);");
    }
//check ll are eorking correctly or not
    public void addContact(ContactModel contact) {
        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("phoneNo", contact.getPhoneNo());
        db.insert("contacts", null, values);
    }

    public List<ContactModel> getAllContacts() {
        List<ContactModel> contactList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM contacts", null);
        if (cursor.moveToFirst()) {
            do {
                ContactModel contact = new ContactModel(cursor.getString(1), cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactList;
    }
}
