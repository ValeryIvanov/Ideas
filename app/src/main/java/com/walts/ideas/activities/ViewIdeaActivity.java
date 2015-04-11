package com.walts.ideas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

public class ViewIdeaActivity extends ActionBarActivity {

    private static final String TAG = "ViewIdeaActivity";

    private Idea idea;

    private IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_idea);

        populateIdea();
    }

    private void populateIdea() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_idea, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteIdea(View view) {
        Dialogs.showDeleteDialog(this, idea);
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
