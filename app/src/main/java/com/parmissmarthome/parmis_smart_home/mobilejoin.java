package com.parmissmarthome.parmis_smart_home;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.parmissmarthome.parmis_smart_home.db.MobileDB;

public class mobilejoin extends AppCompatActivity {

    private String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilejoin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!MainActivity.isClient)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mac= getIntent().getStringExtra("mac");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_join:
                int addkey= 0;
                if(((Switch) findViewById(R.id.ifaddkeyuser)).isChecked()) addkey=1;

                int addscrpt= 0;
                if(((Switch) findViewById(R.id.ifaddscriptuser)).isChecked()) addscrpt=1;

                String ifsms= "0";
                if(((Switch) findViewById(R.id.ifsendsms)).isChecked()) ifsms="0";

                int ifweb= 0;
                if(((Switch) findViewById(R.id.ifweb)).isChecked()) ifweb=1;

                MobileDB mobileDB= new MobileDB(this);
                mobileDB.insertrecord(mac, addkey, addscrpt, 0, 0, 0, null, null, "", ifweb);

                int imac=MainActivity.findlistClientMobiles(mac);
                if (imac!=1) {
                    MainActivity.listClientMobiles.get(imac).acceptjoin = true;
//                    MainActivity.listClientMobiles.get(imac).sendmessage("*Accept#");
                }

                Log.d(MainActivity.Tag, "I Mac is: "+ imac);
                Toast.makeText(this, "موبایل اضافه شد", Toast.LENGTH_SHORT).show();

                finish();
                break;
        }
//        return super.onOptionsItemSelected(item);
        return  true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mobilejoin, menu);
        return true;
    }
}
