package com.walts.ideas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.walts.ideas.activities.MainActivity;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.concurrent.Callable;

public class Dialogs {

    private static void deleteIdea(final ActionBarActivity activity, final Idea idea) {
        int rowsAffected = IdeasDbHelper.getInstance(activity).deleteIdea(idea.id);
        if (rowsAffected == 1) {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.startActivity(intent);
            activity.finish();

            Toast.makeText(activity, R.string.idea_deleted, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(activity, MainActivity.class);

            activity.startActivity(intent);
            activity.finish();

            Toast.makeText(activity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showDeleteDialog(final ActionBarActivity activity, final Idea idea) {
        showConfirmationDialog(activity, activity.getResources().getString(R.string.delete_this_idea), new Callable() {
            @Override
            public Object call() throws Exception {
                deleteIdea(activity, idea);
                return null;
            }
        });
    }

    public static void showConfirmationDialog(final ActionBarActivity activity, String message, final Callable function) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.are_you_sure)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            function.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public static void showAlertMessage(final ActionBarActivity activity, final Intent intent, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
