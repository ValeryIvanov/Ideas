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

public class ViewIdeaActivity extends ActionBarActivity {

    private static final String TAG = "ViewIdeaActivity";

    private Idea idea;

    private IdeasDbHelper dbHelper = new IdeasDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_idea);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewIdeaActivity.this, ListIdeasActivity.class);
        startActivity(intent);
        finish();
    }

    private void populateView() {
        TextView titleView = (TextView) this.findViewById(R.id.title_textView);
        titleView.setText(idea.title);

        TextView descView = (TextView) this.findViewById(R.id.desc_textView);
        descView.setText(idea.desc);

        TextView createdDateView = (TextView) this.findViewById(R.id.createdDate_textView);
        createdDateView.setText(idea.createdDate);
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
                            Intent intent = new Intent(ViewIdeaActivity.this, ListIdeasActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(ViewIdeaActivity.this, R.string.idea_deleted, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(ViewIdeaActivity.this, ListIdeasActivity.class);

                            startActivity(intent);
                            finish();

                            Toast.makeText(ViewIdeaActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void editIdea(View view) {
        if (idea != null) {
            Intent intent = new Intent(ViewIdeaActivity.this, EditIdeaActivity.class);

            Bundle bundle = new Bundle();
            bundle.putLong("id", idea.id);
            intent.putExtras(bundle);

            startActivity(intent);
        } else {
            //ERROR
            Intent intent = new Intent(this, ListIdeasActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }
    }

}
