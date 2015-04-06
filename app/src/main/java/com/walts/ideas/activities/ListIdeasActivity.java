package com.walts.ideas.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.ArrayList;
import java.util.List;


public class ListIdeasActivity extends ActionBarActivity {

    private static final String TAG = "ListIdeasActivity";

    private List<Idea> ideas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ideas);

        IdeasDbHelper dbHelper = new IdeasDbHelper(this);
        ideas = dbHelper.getAllIdeas();

        populateListView();
        registerClickCallback();
    }

    private void registerClickCallback() {
        ListView listView = (ListView) findViewById(R.id.listView);
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

    private void populateListView() {
        ArrayAdapter<Idea> arrayAdapter = new IdeasAdapter(this, R.layout.idea, ideas);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
    }

    public void createNewIdea(View view) {
        Intent intent = new Intent(this, CreateIdeaActivity.class);
        startActivity(intent);
        finish();
    }

    private class IdeasAdapter extends ArrayAdapter<Idea> {
        public IdeasAdapter(Context context, int resource, List<Idea> objects) {
            super(context, resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.idea, parent, false);
            }
            Idea idea = getItem(position);

            TextView titleView = (TextView) itemView.findViewById(R.id.title);
            titleView.setText(idea.title);

            TextView descView = (TextView) itemView.findViewById(R.id.desc);
            descView.setText(idea.desc);

            return itemView;
        }
    }

}
