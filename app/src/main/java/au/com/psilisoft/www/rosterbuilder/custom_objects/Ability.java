package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.database.Cursor;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

/**
 * Created by Fletcher on 5/08/2015.
 */
public class Ability {

    private long mId;
    private String mName;

    public Ability(Cursor c) {
        mId = c.getLong(c.getColumnIndex(Contract.Ability.ID));
        mName = c.getString(c.getColumnIndex(Contract.Ability.NAME));
    }

    public Ability(long id, String name) {
        mId = id;
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
