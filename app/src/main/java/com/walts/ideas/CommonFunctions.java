package com.walts.ideas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.walts.ideas.activities.ListIdeasActivity;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

public class CommonFunctions {

    public static void showDeleteDialog(final ActionBarActivity activity, final IdeasDbHelper dbHelper, final Idea idea) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.delete_this_idea)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int rowsAffected = dbHelper.deleteIdea(idea.id);
                        if (rowsAffected == 1) {
                            Intent intent = new Intent(activity, ListIdeasActivity.class);
                            activity.startActivity(intent);
                            activity.finish();

                            Toast.makeText(activity, R.string.idea_deleted, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(activity, ListIdeasActivity.class);

                            activity.startActivity(intent);
                            activity.finish();

                            Toast.makeText(activity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

}
