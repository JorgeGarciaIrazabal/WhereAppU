package com.application.jorge.whereappu.Classes;

/**
 * Created by Jorge on 19/07/13.
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.R;
import android.content.Context;

public class IconManager {
    private static Map<Integer, String> MapInt2String;
    protected static Map<Integer, String> MapString2Int;

    public static void Init() {
        Field[] drawables = R.drawable.class.getFields();
        MapInt2String = new HashMap<Integer, String>(drawables.length);
        Context context = App.getAppContext();
        for (Field f : drawables) {
            try {
                MapInt2String.put(context.getResources().getIdentifier(f.getName(), "drawable", context.getPackageName()), f.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Integer> GetIcons(String startWith) {

        ArrayList<Integer> ret = new ArrayList<Integer>();
        Context context = App.getAppContext();
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            try {
                if (f.getName().startsWith(startWith)) {
                    ret.add(context.getResources().getIdentifier(f.getName(), "drawable", context.getPackageName()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static ArrayList<Integer> GetIcons(String[] startWith) {

        ArrayList<Integer> ret = new ArrayList<Integer>();
        Context context = App.getAppContext();
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            try {
                boolean conditional = false;
                for (String s : startWith)
                    conditional = conditional || f.getName().startsWith(s);
                if (conditional) {
                    ret.add(context.getResources().getIdentifier(f.getName(), "drawable", context.getPackageName()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static String GetIconName(int Rid) {
        return (String) MapInt2String.get(Rid);
    }

    public static int GetIconInt(String name) {
        Context context = App.getAppContext();
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
