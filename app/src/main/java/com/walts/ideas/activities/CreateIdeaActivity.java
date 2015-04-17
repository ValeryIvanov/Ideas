package com.walts.ideas.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.LocationHelper;
import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

public class CreateIdeaActivity extends ActionBarActivity {

    private static final String TAG = "CreateIdeaActivity";

    private IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);

    private Location location = null;
    private String address = null;

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_idea);

        locationHelper = new LocationHelper(this);
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

    public void addOrRemoveLocation(final View view) {
        final Button button = (Button) findViewById(R.id.addOrRemoveLocation_button);
        if (location == null) {

            button.setEnabled(false);

            final ProgressBar progressBar = (android.widget.ProgressBar) findViewById(R.id.progressBar);
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

                                progressBar.setVisibility(View.GONE);
                                findViewById(R.id.progressBar_container).setVisibility(View.GONE);

                                TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
                                latitudeTextView.setText(String.valueOf(location.getLatitude()));

                                TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
                                longitudeTextView.setText(String.valueOf(location.getLongitude()));

                                findViewById(R.id.location_container).setVisibility(View.VISIBLE);

                                button.setText(getString(R.string.remove_location));
                                button.setEnabled(true);

                                if (address != null) {
                                    TextView addressTextView = (TextView) findViewById(R.id.address);
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
                button.setEnabled(true);
            }
        } else {
            location = null;
            address = null;

            TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
            latitudeTextView.setText("");

            TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
            longitudeTextView.setText("");

            TextView addressTextView = (TextView) findViewById(R.id.address);
            addressTextView.setText("");

            button.setText(R.string.add_current_location);

            findViewById(R.id.location_container).setVisibility(View.GONE);

            findViewById(R.id.progressBar_container).setVisibility(View.VISIBLE);
        }

    }
}
