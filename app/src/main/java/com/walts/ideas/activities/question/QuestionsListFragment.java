package com.walts.ideas.activities.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.walts.ideas.R;
import com.walts.ideas.adapter.QuestionsAdapter;
import com.walts.ideas.db.IdeasDbHelper;
import com.walts.ideas.db.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class QuestionsListFragment extends Fragment {

    private List<Question> questions = new ArrayList<>();
    private QuestionsAdapter arrayAdapter;

    @Optional @InjectView(R.id.listView) ListView listView;
    @Optional @InjectView(R.id.motivational_text) TextView motivationalTextTextView;

    private int numberOfQuestions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        numberOfQuestions = populateQuestions();

        View rootView;

        if (numberOfQuestions == 0) {
            rootView = inflater.inflate(R.layout.card_fragment, container, false);
            ButterKnife.inject(this, rootView);

            Spanned motivationalText = Html.fromHtml(getString(R.string.motivational_text));
            motivationalTextTextView.setText(motivationalText);
        } else {
            rootView = inflater.inflate(R.layout.questions_list_fragment, container, false);
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
        inflater.inflate(R.menu.menu_list_questions, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (numberOfQuestions == 0) {
            menu.findItem(R.id.action_sort_by_question).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_sort_by_question:
                arrayAdapter.sortByQuestion();
                return true;
            case R.id.action_sort_by_created_date:
                arrayAdapter.sortByCreatedDate();
                return true;
            case R.id.action_create_new_question:
                createNewQuestion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createNewQuestion() {
        Intent intent = new Intent(getActivity(), CreateQuestionActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private int populateQuestions() {
        IdeasDbHelper dbHelper = IdeasDbHelper.getInstance(getActivity());
        questions = dbHelper.getAllQuestions();
        Collections.reverse(questions); //show newest first
        return questions.size();
    }

    private void registerClickCallback() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Question question = questions.get(position);
                viewQuestion(question);
            }
        });
    }

    private void viewQuestion(Question question) {
        Intent intent = new Intent(getActivity(), ViewQuestionActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong("id", question.id);
        intent.putExtras(bundle);

        startActivity(intent);
        getActivity().finish();
    }

    private void populateListView() {
        arrayAdapter = new QuestionsAdapter(getActivity(), R.layout.question, questions);
        listView.setAdapter(arrayAdapter);
    }


}
