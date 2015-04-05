package com.walts.ideas.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

public class EditIdeaActivity extends ActionBarActivity {

    private Idea idea;
    private IdeasDbHelper dbHelper = new IdeasDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea);

        populateView();
    }

    private void populateView() {
        idea = (Idea) getIntent().getSerializableExtra("com.walts.ideas.db.Idea");

        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        titleView.setText(idea.title);

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        descView.setText(idea.desc);
    }

    public void saveIdea(View view) {
        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        idea.title = titleView.getText().toString();

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        idea.desc = descView.getText().toString();

        int rowsAffected = dbHelper.updateIdea(idea);

        if (rowsAffected == 1) {
            Toast.makeText(this, R.string.idea_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, ListIdeasActivity.class);
        startActivity(intent);
    }

    public void deleteIdea(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.delete_this_idea)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dbHelper.deleteIdea(idea.id);

                        Toast.makeText(EditIdeaActivity.this, R.string.idea_deleted, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditIdeaActivity.this, ListIdeasActivity.class);
                        startActivity(intent);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
