package com.walts.ideas.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.walts.ideas.db.Entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

public class BaseArrayAdapter<T extends Entity> extends ArrayAdapter<T> {

    private boolean createdDateSortAsc = true;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public BaseArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    private final Comparator<T> createdDateComparatorAsc = new Comparator<T>() {
        @Override
        public int compare(T entity1, T entity2) {
            try {
                return dateFormat.parse(entity1.createdDate).compareTo((dateFormat.parse(entity2.createdDate)));
            } catch (ParseException e) {
                return 0;
            }
        }
    };

    private final Comparator<T> createdDateComparatorDesc = new Comparator<T>() {
        @Override
        public int compare(T entity1, T entity2) {
            try {
                return dateFormat.parse(entity2.createdDate).compareTo((dateFormat.parse(entity1.createdDate)));
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
