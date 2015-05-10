package com.walts.ideas.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.walts.ideas.Dialogs;
import com.walts.ideas.R;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.concurrent.Callable;

public class EditIdeaActivity extends ActionBarActivity {

    private static final String TAG = "EditIdeaActivity";

    private Idea idea;

    private IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea);

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

    private MenuItem.OnMenuItemClickListener removePasswordOnClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final MenuItem item) {
            Callable function = new Callable() {
                @Override
                public Object call() throws Exception {
                    int rowsAffected = dbHelper.removePassword(idea);
                    if (rowsAffected == 1) {
                        item.setTitle(R.string.password_protect);
                        item.setIcon(R.drawable.ic_action_secure);
                        item.setOnMenuItemClickListener(passwordProtectOnClickListener);
                        Toast.makeText(EditIdeaActivity.this, getString(R.string.password_removed), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditIdeaActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                    return null;
                }
            };
            Dialogs.showConfirmationDialog(EditIdeaActivity.this, getResources().getString(R.string.remove_password_message), function);
            return true;
        }
    };

    private MenuItem.OnMenuItemClickListener passwordProtectOnClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(final MenuItem item) {

            final EditText editText = new EditText(EditIdeaActivity.this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            final AlertDialog alertDialog = new AlertDialog.Builder(EditIdeaActivity.this)
                    .setView(editText)
                    .setTitle(R.string.password_protect)
                    .setMessage(R.string.password_protect_desc)
                    .setIcon(android.R.drawable.ic_lock_idle_lock)
                    .setPositiveButton(R.string.password_protect, null)
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
                            if (password.length() == 0) {
                                editText.setError(getString(R.string.password_required));
                            } else {
                                idea.password = editText.getText().toString();

                                int rowsAffected = dbHelper.addPassword(idea);

                                if (rowsAffected == 1) {
                                    item.setTitle(getString(R.string.remove_password));
                                    item.setIcon(R.drawable.ic_action_not_secure);
                                    item.setOnMenuItemClickListener(removePasswordOnClickListener);

                                    Toast.makeText(EditIdeaActivity.this, R.string.password_added, Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(EditIdeaActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
                }
            });
            alertDialog.show();

            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_idea, menu);

        MenuItem passwordMenuItem = menu.findItem(R.id.action_password_protect_idea);
        if (idea.password != null && idea.password.length() > 0) {
            passwordMenuItem.setTitle(getString(R.string.remove_password));
            passwordMenuItem.setIcon(R.drawable.ic_action_not_secure);
            passwordMenuItem.setOnMenuItemClickListener(removePasswordOnClickListener);
        } else {
            passwordMenuItem.setTitle(R.string.password_protect);
            passwordMenuItem.setIcon(R.drawable.ic_action_secure);
            passwordMenuItem.setOnMenuItemClickListener(passwordProtectOnClickListener);
        }
        return super.onCreateOptionsMenu(menu);
    }


    private void populateView() {
        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        titleView.setText(idea.title);

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        descView.setText(idea.desc);

    }

    public void saveIdea(MenuItem item) {
        TextView titleView = (TextView) this.findViewById(R.id.title_editBox);
        String title = titleView.getText().toString().trim();

        TextView descView = (TextView) this.findViewById(R.id.desc_editBox);
        String desc = descView.getText().toString().trim();

        if (title.equals("")) {
            titleView.setError(getString(R.string.title_required));
        } else if (desc.equals("")) {
            descView.setError(getString(R.string.desc_required));
        } else {
            idea.title = title;
            idea.desc = desc;
            int rowsAffected = dbHelper.updateIdea(idea);

            if (rowsAffected == 1) {
                Intent intent = new Intent(this, ViewIdeaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //finishes view activity

                Bundle bundle = new Bundle();
                bundle.putLong("id", idea.id);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();

                Toast.makeText(this, R.string.idea_updated, Toast.LENGTH_SHORT).show();
            } else {
                //ERROR
                Intent intent = new Intent(this, ViewIdeaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //finishes view activity

                Bundle bundle = new Bundle();
                bundle.putLong("id", idea.id);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();

                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteIdea(MenuItem item) {
        Dialogs.showDeleteDialog(this, idea);
    }

}
