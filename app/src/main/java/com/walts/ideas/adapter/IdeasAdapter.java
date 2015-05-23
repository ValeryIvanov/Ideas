package com.walts.ideas.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walts.ideas.R;
import com.walts.ideas.db.Idea;

import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IdeasAdapter extends BaseArrayAdapter<Idea> {

    private boolean titleSortAsc = true;
    private final Context context;

    public IdeasAdapter(Context context, int resource, List<Idea> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        IdeaViewHolder holder;

        if (convertView != null) {
            holder = (IdeaViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.idea, parent, false);
            holder = new IdeaViewHolder(convertView);
            convertView.setTag(holder);
        }
        Idea idea = getItem(position);

        holder.title.setText(idea.title);

        if (idea.password == null || idea.password.length() == 0) { //show idea's desc only if it is not password protected
            holder.desc.setText(idea.desc);
        }

        holder.createdDate.setText(idea.createdDate);

        return convertView;
    }

    protected class IdeaViewHolder {
        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.desc) TextView desc;
        @InjectView(R.id.createdDate) TextView createdDate;

        public IdeaViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private final Comparator<Idea> titleComparatorAsc = new Comparator<Idea>() {
        @Override
        public int compare(Idea idea1, Idea idea2) {
            return idea1.title.compareToIgnoreCase(idea2.title);
        }
    };

    private final Comparator<Idea> titleComparatorDesc = new Comparator<Idea>() {
        @Override
        public int compare(Idea idea1, Idea idea2) {
            return idea2.title.compareToIgnoreCase(idea1.title);
        }
    };

    public void sortByTitle() {
        if (titleSortAsc) {
            this.sort(titleComparatorAsc);
            titleSortAsc = false;
        } else {
            this.sort(titleComparatorDesc);
            titleSortAsc = true;
        }
    }

}
