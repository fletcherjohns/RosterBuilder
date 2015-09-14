package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by Fletcher on 21/07/2015.
 */
public abstract class SimpleListActivity extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        ((TextView) findViewById(R.id.text_view_title)).setText(getMessage());

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(getAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(mListView.getAdapter().getItem(position));
            }
        });
        registerForContextMenu(mListView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_new:
                onActionNewClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId() == R.id.list_view) {
            menu.add("Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            delete(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id);
            updateAdapter();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void updateAdapter() {
        updateAdapter((BaseAdapter) mListView.getAdapter());
    }
    protected abstract String getMessage();
    protected abstract BaseAdapter getAdapter();
    protected abstract void updateAdapter(BaseAdapter adapter);
    protected abstract void onActionNewClicked();
    protected abstract void onListItemClick(Object object);
    protected abstract void delete(long id);

}
