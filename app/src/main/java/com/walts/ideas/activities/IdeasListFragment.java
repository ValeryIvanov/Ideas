package com.walts.ideas.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.walts.ideas.IdeasAdapter;
import com.walts.ideas.R;
import com.walts.ideas.SHA1;
import com.walts.ideas.db.Idea;
import com.walts.ideas.db.IdeasDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class IdeasListFragment extends Fragment {

    private List<Idea> ideas = new ArrayList<>();
    private IdeasAdapter arrayAdapter;

    @Optional @InjectView(R.id.listView) ListView listView;
    @Optional @InjectView(R.id.motivational_text) TextView motivationalTextTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int numberOfIdeas = populateIdeas();

        View rootView;

        if (numberOfIdeas == 0) {
            rootView = inflater.inflate(R.layout.card_fragment, container, false);
            ButterKnife.inject(this, rootView);

            Spanned motivationalText = Html.fromHtml(getString(R.string.motivational_text));
            motivationalTextTextView.setText(motivationalText);
        } else {
            rootView = inflater.inflate(R.layout.ideas_list_fragment, container, false);
            ButterKnife.inject(this, rootView);

            populateListView();
            registerClickCallback();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_list_ideas, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_sort_by_title:
                arrayAdapter.sortByTitle();
                return true;
            case R.id.action_sort_by_created_date:
                arrayAdapter.sortByCreatedDate();
                return true;
            case R.id.action_create_new_idea:
                createNewIdea();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createNewIdea() {
        Intent intent = new Intent(getActivity(), CreateIdeaActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private int populateIdeas() {
        IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(getActivity());
        ideas = dbHelper.getAllIdeas();
        Collections.reverse(ideas); //show newest first
        return ideas.size();
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
        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
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
                            alertDialog.dismiss();
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
        Intent intent = new Intent(getActivity(), ViewIdeaActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong("id", idea.id);
        intent.putExtras(bundle);

        startActivity(intent);
        getActivity().finish();
    }

    private void populateListView() {
        arrayAdapter = new IdeasAdapter(getActivity(), R.layout.idea, ideas);
        listView.setAdapter(arrayAdapter);
    }

}
