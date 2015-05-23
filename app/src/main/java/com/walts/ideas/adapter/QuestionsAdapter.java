package com.walts.ideas.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walts.ideas.R;
import com.walts.ideas.db.Question;

import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QuestionsAdapter extends BaseArrayAdapter<Question> {

    private final Context context;
    private boolean questionSortAsc = true;

    public QuestionsAdapter(Context context, int resource, List<Question> objects) {
        super(context, resource, objects);
        this.context = context;
    }


    public void sortByQuestion() {
        if (questionSortAsc) {
            this.sort(questionComparatorAsc);
            questionSortAsc = false;
        } else {
            this.sort(questionComparatorDesc);
            questionSortAsc = true;
        }
    }

    private final Comparator<Question> questionComparatorAsc = new Comparator<Question>() {
        @Override
        public int compare(Question question1, Question question2) {
            return question1.question.compareToIgnoreCase(question2.question);
        }
    };

    private final Comparator<Question> questionComparatorDesc = new Comparator<Question>() {
        @Override
        public int compare(Question question1, Question question2) {
            return question2.question.compareToIgnoreCase(question1.question);
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        QuestionViewHolder holder;

        if (convertView != null) {
            holder = (QuestionViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.question, parent, false);
            holder = new QuestionViewHolder(convertView);
            convertView.setTag(holder);
        }
        Question question = getItem(position);

        holder.question.setText(question.question);
        holder.createdDate.setText(question.createdDate);

        return convertView;
    }

    protected class QuestionViewHolder {
        @InjectView(R.id.question) TextView question;
        @InjectView(R.id.createdDate) TextView createdDate;

        public QuestionViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
