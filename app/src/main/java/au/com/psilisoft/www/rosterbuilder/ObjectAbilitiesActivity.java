package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class ObjectAbilitiesActivity extends Activity {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_LINK_CONTENT_URI = "extra_link_content_uri";
    public static final String EXTRA_ABILITY_CONTENT_URI = "extra_ability_content_uri";
    public static final String EXTRA_COLUMN_LINK_TABLE_OBJECT_ID = "extra_column_link_table_object_id";
    public static final String EXTRA_COLUMN_LINK_TABLE_ABILITY_ID = "extra_column_ling table_ability_id";
    public static final String EXTRA_COLUMN_ABILITY_TABLE_NAME = "extra_column_ability_table_name";

    private ListView mListView;
    private SimpleCursorAdapter mAdapter;
    private Cursor mAllAbilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);

        Intent i = getIntent();
        final long objectId = i.getLongExtra(EXTRA_ID, -1);
        final Uri linkContentUri = i.getParcelableExtra(EXTRA_LINK_CONTENT_URI);
        final Uri abilityContentUri = i.getParcelableExtra(EXTRA_ABILITY_CONTENT_URI);
        final String colObjectId = i.getStringExtra(EXTRA_COLUMN_LINK_TABLE_OBJECT_ID);
        final String colAbilityId = i.getStringExtra(EXTRA_COLUMN_LINK_TABLE_ABILITY_ID);
        final String colAbilityName = i.getStringExtra(EXTRA_COLUMN_ABILITY_TABLE_NAME);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long abilityId) {
                if (mListView.isItemChecked(position)) {
                    ContentValues values = new ContentValues();
                    values.put(colObjectId, objectId);
                    values.put(colAbilityId, abilityId);
                    getContentResolver().insert(linkContentUri, values);
                } else {
                    String where = colObjectId + "=? AND " + colAbilityId + "=?";
                    String[] whereArgs = {String.valueOf(objectId), String.valueOf(abilityId)};
                    getContentResolver().delete(linkContentUri, where, whereArgs);
                }
            }
        });
        mAllAbilities = getContentResolver().query(abilityContentUri,
                null, null, null, null);

        String[] projection = {colAbilityId};
        String where = colObjectId + "=?";
        String[] whereArgs = {String.valueOf(objectId)};
        final Cursor objectAbilities = getContentResolver().query(linkContentUri,
                projection, where, whereArgs, null);

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,
                mAllAbilities, new String[]{colAbilityName},
                new int[]{android.R.id.text1}, 0);
        mListView.setAdapter(mAdapter);
        if (objectAbilities.moveToFirst()) {
            do {
                if (mAllAbilities.moveToFirst()) {
                    do {
                        if (mAllAbilities.getLong(mAllAbilities
                                .getColumnIndex(Contract.Ability.ID))
                                == objectAbilities.getLong(objectAbilities
                                .getColumnIndex(Contract.StaffAbilities.ABILITY_ID))) {

                            mListView.setItemChecked(mAllAbilities.getPosition(), true);
                        }
                    } while (mAllAbilities.moveToNext());
                }
            } while (objectAbilities.moveToNext());
        }
        objectAbilities.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_staff_abilities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAllAbilities.close();
    }
}
