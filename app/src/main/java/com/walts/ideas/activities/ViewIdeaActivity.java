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
import android.widget.LinearLayout;
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

    @InjectView(R.id.title) TextView titleTextView;
    @InjectView(R.id.createdDate) TextView createdDateTextView;
    @InjectView(R.id.location_container) LinearLayout locationContainer;
    @InjectView(R.id.latitude) TextView latitudeTextView;
    @InjectView(R.id.longitude) TextView longitudeTextView;
    @InjectView(R.id.address_container) LinearLayout addressContainer;
    @InjectView(R.id.address) TextView addressTextView;
    @InjectView(R.id.desc) TextView descTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_idea);

        ButterKnife.inject(this);

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
        titleTextView.setText(idea.title);

        descTextView.setText(idea.desc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());

        createdDateTextView.setText(idea.createdDate);

        if (idea.latitude > 0 && idea.longitude > 0) {

            locationContainer.setVisibility(View.VISIBLE);

            latitudeTextView.setText(String.valueOf(idea.latitude));
            longitudeTextView.setText(String.valueOf(idea.longitude));

            if (idea.address != null && idea.address.length() > 0) {

                addressContainer.setVisibility(View.VISIBLE);

                addressTextView.setText(String.valueOf(idea.address));

            } else {

                String address = locationHelper.getAddress(idea.latitude, idea.longitude);
                if (address != null) {
                    idea.address = address;

                    addressContainer.setVisibility(View.VISIBLE);

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
