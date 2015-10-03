package griffith.busstoptab;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Griffith on 2015-10-03.
 */
public class NavigationDrawerFragment extends Fragment {

    private DrawerLayout mDrawerLayout;
    private SearchView mSearchView;
    private ListView mListView;
    private CostumBaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.navigation_drawer_fragment, container, false);

        //Initialize objects
        mSearchView = (SearchView) view.findViewById(R.id.search_view);
        mListView = (ListView) view.findViewById(R.id.list_view);

        //Create and add adapter for the listview
        adapter = new CostumBaseAdapter();
        mListView.setAdapter(adapter);
        createOnItemClickListener();
        setupSearchView();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

    }

    /**
     * Creates a onItemClickListener and decides what action to be taken upon a item click
     */
    private void createOnItemClickListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
            }
        });
    }

    /**
     * Sets up the SearchView and filter configuration
     */
    private void setupSearchView() {

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
