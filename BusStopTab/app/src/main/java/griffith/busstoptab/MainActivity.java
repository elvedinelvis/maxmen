package griffith.busstoptab;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLinearLayout;
    private SearchView mSearchView;
    private ListView mDrawerList;
    private TextView mTextView;
    private String[] mMaxMenMembers;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMaxMenMembers = getResources().getStringArray(R.array.max_men_members);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_view);
        mTextView = (TextView) findViewById(R.id.textView);
        mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        mSearchView = (SearchView) findViewById(R.id.searchView);

        mDrawerLayout.closeDrawer(mLinearLayout);
        //Set the adapter for the list view
       adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mMaxMenMembers);
        mDrawerList.setAdapter(adapter);
        //Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        mDrawerList.setTextFilterEnabled(true);

        //mSearchView.setIconifiedByDefault(false);
        //mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    mDrawerList.clearTextFilter();
                } else {
                 mDrawerList.setFilterText(query.toString());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView temp = (TextView) view;
        mDrawerList.setItemChecked(position, true);
        mTextView.setText(temp.getText());
        mDrawerLayout.closeDrawer(mLinearLayout);
    }
}