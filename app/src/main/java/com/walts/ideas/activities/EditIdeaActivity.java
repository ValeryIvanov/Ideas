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

    private static final String TAG = "EditIdeaActivity";

    private Idea idea;

    private IdeasDbHelper dbHelper = new IdeasDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea);

        Bundle bundle = getIntent().getExtras();
        long id = bundle.getLong("id");
        idea = dbHelper.getIdea(id);

        if (idea == null) {
            //ERROR
            Intent intent = new Intent(this, ListIdeasActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        } else {
            populateView();
        }
    }

    private void populateView() {
        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        titleView.setText(idea.title);

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        descView.setText(idea.desc);
    }

    public void saveIdea(View view) {
        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        String title = titleView.getText().toString().trim();

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        String desc = descView.getText().toString().trim();

        if (title.equals("")) {
            titleView.setError(getString(R.string.title_required));
        } else if (desc.equals("")) {
            descView.setError(getString(R.string.desc_required));
        } else {
            idea.title = title;
            idea.desc = desc;
            int rowsAffected = dbHelper.updateIdea(idea);

            if (rowsAffected == 1) {
                Intent intent = new Intent(this, ViewIdeaActivity.class);

                Bundle bundle = new Bundle();
                bundle.putLong("id", idea.id);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();

                Toast.makeText(this, R.string.idea_updated, Toast.LENGTH_SHORT).show();
            } else {
                //ERROR
                Intent intent = new Intent(this, ListIdeasActivity.class);

                startActivity(intent);
                finish();

                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteIdea(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.delete_this_idea)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int rowsAffected = dbHelper.deleteIdea(idea.id);
                        if (rowsAffected == 1) {
                            Intent intent = new Intent(EditIdeaActivity.this, ListIdeasActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(EditIdeaActivity.this, R.string.idea_deleted, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(EditIdeaActivity.this, ListIdeasActivity.class);

                            startActivity(intent);
                            finish();

                            Toast.makeText(EditIdeaActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
