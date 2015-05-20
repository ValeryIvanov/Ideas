package com.walts.ideas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.LocationHelper;
import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewIdeaActivity extends ActionBarActivity {

    private static final String TAG = "ViewIdeaActivity";

    private Idea idea;

    private final IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);

    private LocationHelper locationHelper;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_idea);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        locationHelper = new LocationHelper(this);

        populateIdea();
    }

    private void populateIdea() {
        Bundle bundle = getIntent().getExtras();
        long id = bundle.getLong("id");
        idea = dbHelper.getIdea(id);

        if (idea == null) {
            //ERROR
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        } else {
            populateView();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewIdeaActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void populateView() {
        TextView titleView = (TextView) this.findViewById(R.id.title_textView);
        titleView.setText(idea.title);

        TextView descView = (TextView) this.findViewById(R.id.desc_textView);
        descView.setText(idea.desc);
        descView.setMovementMethod(new ScrollingMovementMethod());

        TextView createdDateView = (TextView) this.findViewById(R.id.createdDate_textView);
        createdDateView.setText(idea.createdDate);

        if (idea.latitude > 0 && idea.longitude > 0) {
            findViewById(R.id.location_container).setVisibility(View.VISIBLE);

            TextView latitudeTextView = (TextView) findViewById(R.id.latitude);
            latitudeTextView.setText(String.valueOf(idea.latitude));

            TextView longitudeTextView = (TextView) findViewById(R.id.longitude);
            longitudeTextView.setText(String.valueOf(idea.longitude));

            if (idea.address != null && idea.address.length() > 0) {
                findViewById(R.id.address_container).setVisibility(View.VISIBLE);

                TextView addressTextView = (TextView) findViewById(R.id.address);
                addressTextView.setText(String.valueOf(idea.address));
            } else {
                String address = locationHelper.getAddress(idea.latitude, idea.longitude);
                if (address != null) {
                    idea.address = address;

                    findViewById(R.id.address_container).setVisibility(View.VISIBLE);

                    TextView addressTextView = (TextView) findViewById(R.id.address);
                    addressTextView.setText(String.valueOf(idea.address));

                    dbHelper.updateIdea(idea);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_idea, menu);

        if (idea != null && idea.latitude > 0 && idea.longitude > 0) {
            menu.findItem(R.id.action_remove_location).setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void editIdea(MenuItem item) {
        if (idea != null) {
            Intent intent = new Intent(ViewIdeaActivity.this, EditIdeaActivity.class);

            Bundle bundle = new Bundle();
            bundle.putLong("id", idea.id);
            intent.putExtras(bundle);

            startActivity(intent);
        } else {
            //ERROR
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteIdea(MenuItem item) {
        Dialogs.showDeleteDialog(this, idea);
    }

    public void removeLocation(MenuItem item) {
        Dialogs.showConfirmationDialog(this, getString(R.string.remove_location_message), new Callable() {
            @Override
            public Object call() throws Exception {
                idea.latitude = 0;
                idea.longitude = 0;
                idea.address = null;

                int rowsAffected = dbHelper.updateIdea(idea);

                if (rowsAffected == 1) {
                    Intent intent = new Intent(ViewIdeaActivity.this, ViewIdeaActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //finishes view activity

                    Bundle bundle = new Bundle();
                    bundle.putLong("id", idea.id);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();

                    Toast.makeText(ViewIdeaActivity.this, R.string.idea_updated, Toast.LENGTH_SHORT).show();
                } else {
                    //ERROR
                    Intent intent = new Intent(ViewIdeaActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //finishes view activity

                    startActivity(intent);
                    finish();

                    Toast.makeText(ViewIdeaActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }
}
