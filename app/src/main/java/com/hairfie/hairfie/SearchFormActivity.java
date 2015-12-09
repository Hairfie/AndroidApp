package com.hairfie.hairfie;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class SearchFormActivity extends AppCompatActivity {

    List<Category> mSelectedCategories = new ArrayList<Category>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter() instanceof Adapter) {
                    Adapter adapter = (Adapter) parent.getAdapter();
                    Category category = adapter.getItem(position);

                    if (mSelectedCategories.contains(category)) {
                        mSelectedCategories.remove(category);
                    } else {
                        mSelectedCategories.add(category);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        Category.fetchAll(new ResultCallback.Single<List<Category>>() {
            @Override
            public void onComplete(@Nullable List<Category> object, @Nullable ResultCallback.Error error) {
                if (null == error && null != object) {
                    Adapter adapter = new Adapter();
                    adapter.addAll(object);
                    listView.setAdapter(adapter);

                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void touchSearch(View v) {

    }

    public class Adapter extends ArrayAdapter<Category> {
        public Adapter() {
            super(SearchFormActivity.this, R.layout.fragment_category, R.id.category);


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View result = super.getView(position, convertView, parent);

            Category category = getItem(position);
            TextView label = (TextView)result.findViewById(R.id.category);
            label.setText(category.name);

            ImageView image = (ImageView)result.findViewById(R.id.selector);
            image.setImageResource(mSelectedCategories.contains(category) ? R.drawable.filter_selected : R.drawable.filter_not_selected);

            return result;
        }


    }
}
