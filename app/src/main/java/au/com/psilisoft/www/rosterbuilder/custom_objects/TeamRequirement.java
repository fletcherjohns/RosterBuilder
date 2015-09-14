package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.database.Cursor;

/**
 * Created by Fletcher on 16/08/2015.
 */
public class TeamRequirement extends TimedSegment {

    public TeamRequirement(Cursor main) {
        super(main, null);
    }

    public TeamRequirement(Cursor main, Cursor abilities) {
        super(main, abilities);
    }

    @Override
    public boolean isShift() {
        return false;
    }

}
