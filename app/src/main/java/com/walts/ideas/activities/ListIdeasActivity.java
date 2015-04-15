package com.walts.ideas.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.IdeasAdapter;
import com.walts.ideas.LocationHelper;
import com.walts.ideas.LocationResult;
import com.walts.ideas.MyLocation;
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
        //locationHelper.startLocationUpdates();

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

    public void getLocation(View view) {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Dialogs.showAlertMessage(this, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), "Your GPS seems to be disabled, do you want to enable it?");
        } else if (!isNetworkAvailable()) {
            Dialogs.showAlertMessage(this, new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS), "Your Internet access seems to be disabled, do you want to enable it?");
        } else {
            LocationResult locationResult = new LocationResult(){
                @Override
                public void gotLocation(final Location location){
                    ListIdeasActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (location != null) {
                                Toast.makeText(ListIdeasActivity.this, "Latitude : " + location.getLatitude() + ", longitude : " + location.getLongitude() + ", location is : " + locationHelper.getAddress(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ListIdeasActivity.this, "Could not get location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            MyLocation myLocation = new MyLocation(this);
            myLocation.canGetLocation(locationResult);
        }

        /*
        String location = locationHelper.getLocation();
        Toast.makeText(this, "Your current location is : " + location, Toast.LENGTH_SHORT).show();
        */
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }

}
