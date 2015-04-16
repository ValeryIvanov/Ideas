package com.walts.ideas.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.IdeasAdapter;
import com.walts.ideas.LocationHelper;
import com.walts.ideas.R;
import com.walts.ideas.SHA1;
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
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ideas);

        locationHelper = new LocationHelper(this);

        populateIdeas();
        populateListView();
        registerClickCallback();
    }

    private void populateIdeas() {
        IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);
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
                final Idea idea = ideas.get(position);
                if (idea.password == null || idea.password.length() == 0) {
                    viewIdea(idea);
                } else {
                    showPasswordDialog(idea);
                }
            }
        });
    }

    private void showPasswordDialog(final Idea idea) {
        final EditText editText = new EditText(ListIdeasActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final AlertDialog alertDialog = new AlertDialog.Builder(ListIdeasActivity.this)
                .setView(editText)
                .setTitle(R.string.enter_password)
                .setIcon(android.R.drawable.ic_lock_idle_lock)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = editText.getText().toString();
                        if (SHA1.sha1Hash(password).equals(idea.password)) {
                            viewIdea(idea);
                        } else {
                            editText.setError(getString(R.string.wrong_password));
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void viewIdea(Idea idea) {
        Intent intent = new Intent(ListIdeasActivity.this, ViewIdeaActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong("id", idea.id);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
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

    public void getLocation(final View view) {
        view.setEnabled(false);
        final ProgressBar progressBar = (android.widget.ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
            @Override
            public void gotLocation(final Location location){
                ListIdeasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (location != null) {
                            Toast.makeText(ListIdeasActivity.this, "Latitude : " + location.getLatitude() + ", longitude : " + location.getLongitude() + ", location is : " + locationHelper.getAddress(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
                        } else {
                            Dialogs.showAlertMessage(ListIdeasActivity.this, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), getString(R.string.location_null));
                        }
                        progressBar.setVisibility(View.GONE);
                        view.setEnabled(true);
                    }
                });
            }
        };
        boolean requestSuccessful = locationHelper.requestLocation(locationResult);
        if (!requestSuccessful) {
            progressBar.setVisibility(View.GONE);
            view.setEnabled(true);
        }
    }

}
