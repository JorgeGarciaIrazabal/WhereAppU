package com.application.jorge.whereappu.Classes;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.application.jorge.whereappu.Activities.App;

import java.util.ArrayList;

public class PhoneContact {
    private String name;
    public ArrayList<String> phoneNumbers = new ArrayList<String>();
    private Uri photo;

    public PhoneContact() {
        ;
    }

    PhoneContact(String n, ArrayList<String> pn, Uri ph) {
        setName(n);
        setPhoneNumber(pn);
        setPhoto(ph);
    }

    PhoneContact(String n, Uri ph) {
        setName(n);
        setPhoto(ph);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public void setPhoneNumber(ArrayList<String> phone_number) {
        this.phoneNumbers = phoneNumbers;
    }

    public void addPhoneNumber(String phone_number) {
        this.phoneNumbers.add(phone_number);
    }

    public static Uri GetPhotoContact(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = App.getAppContext().getContentResolver().query(photoUri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return photoUri;
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static ArrayList<PhoneContact> GetContacts() {
        ArrayList<PhoneContact> cnta = new ArrayList<PhoneContact>();
        Cursor phones = null;
        try {
            phones = App.getAppContext().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC");
            String name, phoneNumber;
            String preName = "-123sasd213";
            while (phones.moveToNext()) {
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (!name.equals(preName)) {
                    Long id = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID));
                    cnta.add(new PhoneContact(name, GetPhotoContact(id)));
                    cnta.get(cnta.size() - 1).addPhoneNumber(phoneNumber);
                    preName = name;
                } else
                    cnta.get(cnta.size() - 1).addPhoneNumber(phoneNumber);
            }

            return cnta;
        } finally {
            if (phones != null) phones.close();
        }
        // Uri myPerson = ContentUris.withAppendedId(contentUri, id)
    }

    public static ArrayList<String> GetAllPhones() {
        ArrayList<String> cnta = new ArrayList<String>();
        Cursor phones = null;
        try {
            phones = App.getAppContext().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            while (phones.moveToNext()) {
                String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                cnta.add(phone);
            }
            return cnta;
        }  finally {
            if (phones != null) phones.close();
        }
        // Uri myPerson = ContentUris.withAppendedId(contentUri, id)
    }

    public static PhoneContact GetContact(String phonenum) {
        PhoneContact cnta = null;
        Cursor phones = null;
        try {
            String[] necessaydata = new String[]{ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
            phones = App
                    .getAppContext()
                    .getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, necessaydata, ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " like " + "'%" + phonenum + "%'", null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            String name, phoneNumber;
            while (phones.moveToNext()) {
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                Long id = Long.parseLong(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID)));
                cnta = new PhoneContact(name, GetPhotoContact(id));
                cnta.addPhoneNumber(phoneNumber);

            }
            return cnta;
        }  finally {
            if (phones != null) phones.close();
        }
        // Uri myPerson = ContentUris.withAppendedId(contentUri, id)
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }



}
