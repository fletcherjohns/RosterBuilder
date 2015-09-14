package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends Activity {

    Button mAbilitiesButton;
    Button mStaffButton;
    Button mRosterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAbilitiesButton = (Button) findViewById(R.id.abilities_button);
        mAbilitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAbilitiesActivity();
            }
        });
        mStaffButton = (Button) findViewById(R.id.staff_button);
        mStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStaffListActivity();
            }
        });
        mRosterButton = (Button) findViewById(R.id.roster_button);
        mRosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRosterListActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    private void startAbilitiesActivity() {
        Intent intent = new Intent(this, AbilitiesActivity.class);
        startActivity(intent);
    }

    private void startStaffListActivity() {
        Intent intent = new Intent(this, StaffListActivity.class);
        startActivity(intent);
    }

    private void startRosterListActivity() {
        Intent intent = new Intent(this, RosterListActivity.class);
        startActivity(intent);
    }
}
