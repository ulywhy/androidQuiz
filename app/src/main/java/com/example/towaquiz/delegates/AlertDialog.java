package com.example.towaquiz.delegates;

import android.content.Context;
import android.content.DialogInterface;

public class AlertDialog {

    private AlertDialog(){}

    public static void showDialog(String message, String title, Context parent){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(parent);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

    }
}
