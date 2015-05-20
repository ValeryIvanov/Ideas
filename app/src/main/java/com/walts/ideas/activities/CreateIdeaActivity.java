package com.walts.ideas.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.LocationHelper;
import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreateIdeaActivity extends ActionBarActivity {

    private static final String TAG = "CreateIdeaActivity";

    private final IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);

    private Location location = null;
    private String address = null;

    private LocationHelper locationHelper;

    @InjectView(R.id.latitude) TextView latitudeTextView;
    @InjectView(R.id.longitude) TextView longitudeTextView;
    @InjectView(R.id.address) TextView addressTextView;
    @InjectView(R.id.location_container) LinearLayout locationContainer;
    @InjectView(R.id.progress_bar) ProgressBar progressBar;
    @InjectView(R.id.title) TextView titleTextView;
    @InjectView(R.id.desc) TextView descTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_idea);

        ButterKnife.inject(this);

        locationHelper = new LocationHelper(this);

        if (savedInstanceState != null) {
            location = savedInstanceState.getParcelable("location");
            address = savedInstanceState.getString("address");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("location", location);
        outState.putString("address", address);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_idea, menu);

        populateLocationView(menu);
        return super.onCreateOptionsMenu(menu);
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

    public void createIdea(MenuItem item) {
        String title = titleTextView.getText().toString().trim();
        String desc = descTextView.getText().toString().trim();

        if (title.equals("")) {
            titleTextView.setError(getString(R.string.title_required));
        } else if (desc.equals("")) {
            descTextView.setError(getString(R.string.desc_required));
        } else {
            Idea idea = new Idea(title, desc);

            if (location != null) {
                idea.latitude = location.getLatitude();
                idea.longitude = location.getLongitude();
            }
            if (address != null) {
                idea.address = address;
            }

            long id = dbHelper.insertIdea(idea);
            if (id == -1) {
                //ERROR
                Intent intent = new Intent(this, MainActivity.class);

                startActivity(intent);
                finish();

                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else {
                viewIdea(id);
            }
        }
    }

    public void addOrRemoveLocation(final MenuItem item) {
        if (location == null) {
            item.setEnabled(false);

            progressBar.setVisibility(View.VISIBLE);

            LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
                @Override
                public void gotLocation(final Location location){
                    CreateIdeaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (location != null) {
                                CreateIdeaActivity.this.location = location;
                                String address = locationHelper.getAddress(location.getLatitude(), location.getLongitude());
                                CreateIdeaActivity.this.address = address;

                                progressBar.setVisibility(View.GONE);

                                latitudeTextView.setText(String.valueOf(location.getLatitude()));
                                longitudeTextView.setText(String.valueOf(location.getLongitude()));

                                locationContainer.setVisibility(View.VISIBLE);

                                item.setTitle(getString(R.string.action_remove_location));
                                item.setIcon(R.drawable.ic_action_location_off);
                                item.setEnabled(true);

                                if (address != null) {
                                    addressTextView.setText(address);
                                } else {
                                    Toast.makeText(CreateIdeaActivity.this, R.string.address_fetching_failed, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Dialogs.showAlertMessage(CreateIdeaActivity.this, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), getString(R.string.location_null));
                            }
                        }
                    });
                }
            };
            boolean requestSuccessful = locationHelper.requestLocation(locationResult);
            if (!requestSuccessful) {
                progressBar.setVisibility(View.GONE);
                item.setEnabled(true);
            }
        } else {
            location = null;
            address = null;

            latitudeTextView.setText("");
            longitudeTextView.setText("");
            addressTextView.setText("");

            item.setTitle(R.string.add_current_location);
            item.setIcon(R.drawable.ic_action_place);

            locationContainer.setVisibility(View.GONE);
        }
    }

    private void populateLocationView(Menu menu) {
        if (location != null) {
            latitudeTextView.setText(String.valueOf(location.getLatitude()));
            longitudeTextView.setText(String.valueOf(location.getLongitude()));

            locationContainer.setVisibility(View.VISIBLE);

            MenuItem locationItem = menu.findItem(R.id.action_add_current_location);

            locationItem.setTitle(getString(R.string.action_remove_location));
            locationItem.setIcon(R.drawable.ic_action_location_off);
            locationItem.setEnabled(true);
        }
        if (address != null) {
            addressTextView.setText(address);
        }
    }
}
