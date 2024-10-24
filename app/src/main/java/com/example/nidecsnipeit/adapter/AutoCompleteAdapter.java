package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.nidecsnipeit.network.model.BasicItemModel;
import com.example.nidecsnipeit.utility.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<BasicItemModel> implements Filterable {
    private final Context context;
    private final int textViewResourceId;
    private List<BasicItemModel> mList;
    private final List<BasicItemModel> mListAll;
    public AutoCompleteAdapter(Context context, int textViewResourceId,
                               List<BasicItemModel> mList) {
        super(context, textViewResourceId, mList);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.mList = new ArrayList<>(mList);
        this.mListAll = new ArrayList<>();
        this.mListAll.addAll(mList);
    }

    @Override
    public BasicItemModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(textViewResourceId, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        BasicItemModel item = mList.get(position);
        if (textView != null && item != null) {
            textView.setText(item.getName());
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private final Filter nameFilter = new Filter() {
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                clear();
                mList = (List<BasicItemModel>) results.values;
                addAll(mList);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<BasicItemModel> filteredList = new ArrayList<>();

            if (Common.isHardScanButtonPressed) {
                // if the hard scan button is pressed, return default value
                filterResults.values = Collections.emptyList();
                filterResults.count = 0;
                return filterResults;
            }

            if (constraint != null) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BasicItemModel item : mListAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            filterResults.values = filteredList;
            filterResults.count = filteredList.size();

            return filterResults;
        }
    };
}
