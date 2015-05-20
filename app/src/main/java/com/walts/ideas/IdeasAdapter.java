package com.walts.ideas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.walts.ideas.db.Idea;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IdeasAdapter extends ArrayAdapter<Idea> {

    private boolean titleSortAsc = true;
    private boolean createdDateSortAsc = true;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final Context context;

    public IdeasAdapter(Context context, int resource, List<Idea> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.idea, parent, false);
            holder = new ViewHolder(convertView);
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

    static class ViewHolder {
        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.desc) TextView desc;
        @InjectView(R.id.createdDate) TextView createdDate;

        public ViewHolder(View view) {
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

    private final Comparator<Idea> createdDateComparatorAsc = new Comparator<Idea>() {
        @Override
        public int compare(Idea idea1, Idea idea2) {
            try {
                return dateFormat.parse(idea1.createdDate).compareTo((dateFormat.parse(idea2.createdDate)));
            } catch (ParseException e) {
                return 0;
            }
        }
    };

    private final Comparator<Idea> createdDateComparatorDesc = new Comparator<Idea>() {
        @Override
        public int compare(Idea idea1, Idea idea2) {
            try {
                return dateFormat.parse(idea2.createdDate).compareTo((dateFormat.parse(idea1.createdDate)));
            } catch (ParseException e) {
                return 0;
            }
        }
    };

    public void sortByCreatedDate() {
        if (createdDateSortAsc) {
            this.sort(createdDateComparatorAsc);
            createdDateSortAsc = false;
        } else {
            this.sort(createdDateComparatorDesc);
            createdDateSortAsc = true;
        }
    }

}
