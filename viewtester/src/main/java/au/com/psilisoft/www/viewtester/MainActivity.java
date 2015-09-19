package au.com.psilisoft.www.viewtester;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import au.com.psilisoft.www.staffrosterviews.CustomNumberPicker;
import au.com.psilisoft.www.staffrosterviews.ScrollManager;

/**
 *
 */

public class MainActivity extends Activity {

    private CustomNumberPicker mPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPicker = (CustomNumberPicker) findViewById(R.id.picker);
        if (savedInstanceState != null) {
            mPicker.setScrollPosition(savedInstanceState.getFloat("position"));
        }
        mPicker.setOnNumberChangeListener(new CustomNumberPicker.OnNumberChangeListener() {
            @Override
            public void numberSelected(int number) {
                toast("You have selected " + number);
            }

            @Override
            public void looped(int direction) {
                toast("looped " + (direction == ScrollManager.LOOP_FORWARD ? "Forwards" : "Backwards"));
            }
        });
    }

    private void toast(String text) {

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putFloat("position", mPicker.getScrollPosition());
    }
}
