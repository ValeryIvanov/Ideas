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

public class IdeasAdapter extends ArrayAdapter<Idea> {

    private boolean titleSortAsc = true;
    private boolean createdDateSortAsc = true;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private Context context;

    public IdeasAdapter(Context context, int resource, List<Idea> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View itemView = convertView;
        if (itemView == null) {
            itemView = layoutInflater.inflate(R.layout.idea, parent, false);
        }
        Idea idea = getItem(position);

        TextView titleView = (TextView) itemView.findViewById(R.id.title);
        titleView.setText(idea.title);

        TextView descView = (TextView) itemView.findViewById(R.id.desc);
        descView.setText(idea.desc);

        TextView createdDateView = (TextView) itemView.findViewById(R.id.createdDate);
        createdDateView.setText(idea.createdDate);

        return itemView;
    }

    private Comparator<Idea> titleComparatorAsc = new Comparator<Idea>() {
        @Override
        public int compare(Idea idea1, Idea idea2) {
            return idea1.title.compareToIgnoreCase(idea2.title);
        }
    };

    private Comparator<Idea> titleComparatorDesc = new Comparator<Idea>() {
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

    private Comparator<Idea> createdDateComparatorAsc = new Comparator<Idea>() {
        @Override
        public int compare(Idea idea1, Idea idea2) {
            try {
                return dateFormat.parse(idea1.createdDate).compareTo((dateFormat.parse(idea2.createdDate)));
            } catch (ParseException e) {
                return 0;
            }
        }
    };

    private Comparator<Idea> createdDateComparatorDesc = new Comparator<Idea>() {
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
