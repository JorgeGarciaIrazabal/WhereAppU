package com.application.jorge.whereappu.Classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.application.jorge.whereappu.Activities.App;
import com.github.pierry.simpletoast.SimpleToast;
import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;

/**
 * Created by Jorge on 30/05/2015.
 */
public class alert {
    public static void soft(Object ms) {
        SimpleToast.info(App.getAppContext(), ms.toString());
    }

    public static void soft(int ms) {
        soft(String.valueOf(ms));
    }

    public static void soft(float ms) {
        soft(String.valueOf(ms));
    }

    public static void soft(ArrayList<String> mss) {
        soft(utils.join(mss, ", "));
    }

    public static void DEBUG(int ms) {
        soft("" + ms);
    }

    public static void popUp(Context c, String text) {
        //TODO reimplement alert function in App
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle("alert");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void popUp(Context c, ArrayList<String> text) {
        popUp(c, utils.join(text, ", "));
    }

    public static void popUp(String text) {
        popUp(App.Activity, text);
    }

    public static LoadToast load(String text) {
        LoadToast lt = new LoadToast(App.getAppContext());
        lt.setText(text);
        lt.show();
        return lt;
    }
}
