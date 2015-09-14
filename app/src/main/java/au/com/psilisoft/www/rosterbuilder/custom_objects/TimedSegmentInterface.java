package au.com.psilisoft.www.rosterbuilder.custom_objects;

import java.util.Calendar;

/**
 * Created by Fletcher on 17/08/2015.
 */
public interface TimedSegmentInterface {

    long getId();

    int getDay();

    Calendar getStartTime();

    Calendar getFinishTime();

    String getDateString();

    String getStartTimeString();

    String getFinishTimeString();

}
