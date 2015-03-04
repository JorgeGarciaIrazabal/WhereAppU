package com.application.jorge.whereappu.Classes;

import android.database.Cursor;
import com.application.jorge.whereappu.Activities.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jorge on 22/07/13.
 */
public class QueryTable {

    public class QTExcep extends Exception {
        public QTExcep(String message) {
            super(message);
        }
    }

    private ArrayList<ArrayList<Object>> table = new ArrayList<>();
    public ArrayList<String> headers = new ArrayList<>();
    public boolean updateNullValues = false;
    public boolean jsonNullValues = false;
    public static final DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public QueryTable(Cursor cursor) {
        //getting headers
        this.constructFromCursor(cursor);
    }

    public QueryTable() {
    }

    public QueryTable(String stringJson) {
        JSONArray json = App.parseJSONArray(stringJson);
        constructFromJSON(json);
    }

    public QueryTable(JSONArray json) {
        constructFromJSON(json);
    }

    public void addRow() {
        table.add(new ArrayList<Object>());
        for (String head : headers)
            table.get(table.size() - 1).add(null);
    }

    public QueryTable clone() {
        QueryTable newQueryTable = new QueryTable();
        newQueryTable.headers = new ArrayList<>(headers);
        newQueryTable.table = new ArrayList<>(table);
        return newQueryTable;
    }

    public void constructFromJSON(JSONArray json) {
        if (json != null && json.length() > 0) {
            try {
                JSONObject jsonHead = json.getJSONObject(0);
                Iterator<?> keys = jsonHead.keys();
                while (keys.hasNext()) {
                    String head = (String) keys.next();
                    headers.add(head.toLowerCase());
                }

                for (int i = 0; i < json.length(); i++) {
                    table.add(new ArrayList<Object>());
                    JSONObject jsonBody = json.getJSONObject(i);
                    for (int j = 0; j < headers.size(); j++) {
                        table.get(table.size() - 1).add(jsonBody.get(headers.get(j)));
                    }
                }
            } catch (JSONException ignored) {
            }
        }
    }

    public void constructFromCursor(Cursor cursor) {
        //getting headers
        String columnNames[] = cursor.getColumnNames();

        for (String columnName : columnNames) {
            headers.add(columnName.toLowerCase());
        }
        //getting table
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                table.add(new ArrayList<Object>());
                for (int i = 0; i < columnNames.length; i++)
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_BLOB:
                            table.get(table.size() - 1).add(cursor.getBlob(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            table.get(table.size() - 1).add(cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            table.get(table.size() - 1).add(cursor.getLong(i));
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            table.get(table.size() - 1).add(null);
                            break;
                        default:
                            String str = cursor.getString(i);
                            try {
                                table.get(table.size() - 1).add(dFormat.parse(str));
                            } catch (ParseException e) {
                                table.get(table.size() - 1).add(cursor.getString(i));
                            }
                    }
            } while (cursor.moveToNext());
        }
    }

    public int getColumnIndex(String column) throws QTExcep {
        int ret = headers.indexOf(column.toLowerCase());
        if (ret < 0)
            throw new QTExcep("no column with name: " + column);
        return ret;
    }

    public Object getData(String column, int row) throws QTExcep {
        return getData(getColumnIndex(column));
    }

    public Object getData(int column, int row) {
        return this.table.get(row).get(column);
    }

    public Object getData(String column) throws QTExcep {
        return getData(column, 0);
    }

    public Object getData(int column) {
        return getData(column, 0);
    }

    public ArrayList<String> getHeadersNotNull(int row) throws QTExcep {
        if (row > height()) return null;
        ArrayList<String> headersWithValuesNotNull = new ArrayList<>();
        for (String head : headers)
            if (this.getData(head, row) != null)
                headersWithValuesNotNull.add(head);
        return headersWithValuesNotNull;
    }

    public String getInsertQueryString(String table, int row) throws QTExcep {
        HashMap<String, String> typeRelationship = checkTableCompatibility(table);
        if (typeRelationship == null) return null;

        //we don't want to add values not inserted so we get rid of them
        ArrayList<String> headersWithValuesNotNull = getHeadersNotNull(row);

        String query = "INSERT INTO " + table + " (" + App.join(headersWithValuesNotNull, ", ") + " ) VALUES (";

        /*ArrayList<String> values = new ArrayList<>();
        for (String head : headersWithValuesNotNull) {
            Object data =  getData(head, row);
            String type = typeRelationship.get(head);
            switch (type) {
                case "TEXT":
                    values.add("'" + data.replaceAll("'", "''") + "'");
                    break;
                case "INTEGER":
                    values.add(data);
                    break;
                case "DOUBLE":
                    values.add(data);
                    break;
                default:
                    return null;
            }
        }
        query = query + App.join(values, ", ") + ")";*/
        return query;//TODO reimplement
    }

    public String getInsertQueryString(String table) throws QTExcep {
        if (height() != 1) return null;
        return getInsertQueryString(table, 0);

    }

    public String getUploadQueryString(String table, int row) {
        HashMap<String, String> typeRelationship = checkTableCompatibility(table);
        if (typeRelationship == null) return null;
        String query = "UPDATE " + table + " SET ";
        /*ArrayList<String> values = new ArrayList<>();
        ArrayList<String> headersWithValuesNotNull = updateNullValues ? headers : getHeadersNotNull(row);
        for (String head : headersWithValuesNotNull) {
            String data = getData(head, row);
            data = data == null ? "null" : data;
            String type = typeRelationship.get(head);
            switch (type) {
                case "TEXT":
                    if (!data.equals("null"))
                        values.add(head + " = '" + data.replaceAll("'", "''") + "' ");
                    else
                        values.add(head + " = " + data);
                    break;
                case "INTEGER":
                    values.add(head + " = " + data);
                    break;
                case "DOUBLE":
                    values.add(head + " = " + data);
                    break;
                case "REAL":
                    values.add(head + " = " + data);
                    break;
                case "TIMESTAMP":
                    if (!data.equals("null"))
                        values.add(head + " = '" + data.replaceAll("'", "''") + "' ");
                    else
                        values.add(head + " = " + data);
                    break;
                default:
                    App.softAlert(" necessary to add type: " + type);
                    return null;
            }
        }
        query = query + App.join(values, ", ");*/
        return query;
    }

    public String getUploadQueryString(String table) {
        return getUploadQueryString(table, 0);
    }

    private HashMap<String, String> checkTableCompatibility(String table) {
        HashMap<String, String> typeRelationship = new HashMap<>();
        /*QueryTable tableInfo = null; //MainActivity.db.tableInfo(table);
        //checking headers
        if (tableInfo.height() != headers.height()) return null;

        for (int i = 0; i < tableInfo.height(); i++) {
            if (!this.table.containsKey(tableInfo.getData("name", i)))
                return null;
            typeRelationship.put(tableInfo.getData("name", i), tableInfo.getData("type", i));
        }*/
        return typeRelationship;
    }

    public JSONArray getJSONArray() throws QTExcep {
        JSONArray jArray = new JSONArray();
        for (int i = 0; i < height(); i++) {
            JSONObject jObject = getJSONObject(i);
            if (jObject != null)
                jArray.put(jObject);
            else
                return null;
        }
        return jArray;
    }

    public JSONObject getJSONObject(int row) throws QTExcep {
        if (row < height()) {
            try {
                JSONObject jobject = new JSONObject();
                if (jsonNullValues)
                    for (String head : headers)
                        jobject.put(head, getData(head, row) == null ? "null" : getData(head, row));
                else
                    for (String head : headers)
                        jobject.put(head, getData(head, row));
                return jobject;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else
            return null;
    }

    public JSONObject getJSONObject() throws QTExcep {
        return getJSONObject(0);
    }

    public QueryTable getRow(int row) throws QTExcep {
        QueryTable newQt = new QueryTable();
        newQt.headers = headers;

        if (this.table.size() > row) {
            this.addRow();
            for (String header : headers) {
                newQt.table.get(0).add(this.getData(header));
            }
        }
        return newQt;
    }

    public String getRowString(int row, String separator) throws QTExcep {
        if (this.table.size() > row) {
            ArrayList<Object> rowString = new ArrayList<>();
            for (String header : headers) {
                rowString.add(this.getData(header, row));
            }
            return App.join(rowString, separator);
        }
        return null;
    }

    public String getRowString(int row) throws QTExcep {
        return getRowString(row, "\t");
    }

    public void remove(int row) {
        table.remove(row);
    }

    public void removeAll() {
        table.clear();
        headers.clear();
    }

    public <T> void setData(String column, int row, T data) throws QTExcep {
        setData(getColumnIndex(column), row, data);
    }

    public <T> void setData(int column, int row, T data) {
        table.get(row).set(column, data);
    }

    public <T> void setData(String column, T data) throws QTExcep {
        setData(column, 0, data);
    }

    public <T> void setData(int column, T data) {
        setData(column, 0, data);
    }

    public int height() {
        return this.table.size();
    }

    public int width() {
        return this.headers.size();
    }

    public String toString() {
        ArrayList<String> rows = new ArrayList<>();
        rows.add(App.join(headers, "\t"));
        for (int i = 0; i < height(); i++)
            try {
                rows.add(getRowString(i));
            } catch (QTExcep qtExcep) {
                qtExcep.printStackTrace();
            }
        return App.join(rows, "\n");
    }
/*
    public void removeColumn(String column) {
        if (table.containsKey(column)) {
            table.remove(column);
            for (int i = 0; i < headers.size(); i++)
                if (column.equals(headers.get(i))) {
                    headers.remove(i);
                    return;
                }
        }
    }*/

}



