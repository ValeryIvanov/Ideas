package com.walts.ideas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

public class CreateIdeaActivity extends ActionBarActivity {

    private static final String TAG = "CreateIdeaActivity";

    private IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_idea);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ListIdeasActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_idea, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void createIdea(View view) {
        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        String title = titleView.getText().toString().trim();

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        String desc = descView.getText().toString().trim();

        if (title.equals("")) {
            titleView.setError(getString(R.string.title_required));
        } else if (desc.equals("")) {
            descView.setError(getString(R.string.desc_required));
        } else {
            Idea idea = new Idea(title, desc);

            long id = dbHelper.insertIdea(idea);
            if (id == -1) {
                //ERROR
                Intent intent = new Intent(this, ListIdeasActivity.class);

                startActivity(intent);
                finish();

                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else {
                viewIdea(id);
            }
        }
    }

    private void viewIdea(long id) {
        Intent intent = new Intent(this, ViewIdeaActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();

        Toast.makeText(this, R.string.idea_created, Toast.LENGTH_SHORT).show();
    }
}
