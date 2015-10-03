package griffith.busstoptab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Griffith on 2015-10-03.
 */
public class CostumBaseAdapter extends BaseAdapter implements Filterable{

    ArrayList<String> members = new ArrayList<String>();
    ArrayList<String> filterList;
    CostumFilter filter;

    CostumBaseAdapter() {
        members.add("Kevin");
        members.add("Anders");
        members.add("Erik");
        members.add("Elvedin");
        filterList = new ArrayList<String>(members);
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if(convertView == null) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.costum_row, parent, false);
        } else {
            row = convertView;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.text_view);
        titleTextView.setText(members.get(position));

        return row;
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new CostumFilter();
        }
        return filter;
    }

    class CostumFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = filterList;
                results.count = filterList.size();
            } else {
                // We perform filtering operation
                ArrayList<String> filteredList = new ArrayList<String>();

                for (String s : filterList) {
                    //Possibility to add different filters
                    if (s.toUpperCase().contains(constraint.toString().toUpperCase())
                            && constraint.length() > 1) {
                        filteredList.add(s);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            members = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}
