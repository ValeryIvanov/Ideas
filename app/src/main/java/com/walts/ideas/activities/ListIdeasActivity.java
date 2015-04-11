package com.walts.ideas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walts.ideas.IdeasAdapter;
import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListIdeasActivity extends ActionBarActivity {

    private static final String TAG = "ListIdeasActivity";

    private List<Idea> ideas = new ArrayList<>();
    private IdeasAdapter arrayAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ideas);

        populateIdeas();
        populateListView();
        registerClickCallback();
    }

    private void populateIdeas() {
        IdeasDbHelper dbHelper = new IdeasDbHelper(this);
        ideas = dbHelper.getAllIdeas();
        Collections.reverse(ideas); //show newest first
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_ideas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void registerClickCallback() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Idea idea = ideas.get(position);
                Intent intent = new Intent(ListIdeasActivity.this, ViewIdeaActivity.class);

                Bundle bundle = new Bundle();
                bundle.putLong("id", idea.id);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.sort_by_title:
                arrayAdapter.sortByTitle();
                return true;
            case R.id.sort_by_created_date:
                arrayAdapter.sortByCreatedDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateListView() {
        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new IdeasAdapter(this, R.layout.idea, ideas);
        listView.setAdapter(arrayAdapter);
    }

    public void createNewIdea(View view) {
        Intent intent = new Intent(this, CreateIdeaActivity.class);
        startActivity(intent);
        finish();
    }


}
