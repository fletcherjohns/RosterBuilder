package au.com.psilisoft.www.rosterbuilder;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

public class AbilitiesActivity extends SimpleListActivity {
    @Override
    protected String getMessage() {
        return "This list of abilities is used to restrict the rostering of staff to shifts";
    }

    protected Cursor getCursor() {
        return getContentResolver().query(Contract.Ability.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, getCursor(),
                new String[]{Contract.Ability.NAME}, new int[]{android.R.id.text1}, 0);
    }

    @Override
    protected void updateAdapter(BaseAdapter adapter) {
        ((SimpleCursorAdapter)adapter).changeCursor(getCursor());
    }

    @Override
    protected void onActionNewClicked() {
        showAbilityDialog(-1, "");
    }

    @Override
    protected void onListItemClick(Object object) {
        showAbilityDialog(((Cursor) object).getLong(((Cursor) object).getColumnIndex(Contract.Ability.ID)),
                ((Cursor) object).getString(((Cursor) object).getColumnIndex(Contract.Ability.NAME)));
    }

    protected void insertAbility(String name) {
        ContentValues values = new ContentValues();
        values.put(Contract.Ability.NAME, name);
        getContentResolver().insert(Contract.Ability.CONTENT_URI, values);

        updateAdapter();
    }

    protected void updateAbility(long id, String name) {
        ContentValues values = new ContentValues();
        values.put(Contract.Ability.NAME, name);
        getContentResolver().update(Uri.withAppendedPath(Contract.Ability.CONTENT_URI,
                String.valueOf(id)), values, null, null);

        updateAdapter();
    }

    private void showAbilityDialog(final long id, String name) {
        final EditText editText = new EditText(this);
        editText.setText(name);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(id > 0 ? "Update \"" + name + "\" " : "Create New")
                .setMessage("Enter a name")
                .setView(editText)
                .setPositiveButton(id > 0 ? "UPDATE" : "CREATE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (id > 0) {
                                    updateAbility(id, editText.getText().toString());
                                } else {
                                    insertAbility(editText.getText().toString());
                                }
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    @Override
    protected void delete(long id) {
        getContentResolver().delete(Uri.withAppendedPath(Contract.Ability.CONTENT_URI,
                String.valueOf(id)), null, null);
    }
}
