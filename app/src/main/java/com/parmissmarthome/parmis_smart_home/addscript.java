package com.parmissmarthome.parmis_smart_home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.parmissmarthome.parmis_smart_home.db.mainmenudb;
import com.parmissmarthome.parmis_smart_home.db.scriptdb;
//import com.parmissmarthome.parmis_smart_home.wheelpicker.NumberPicker;

import java.util.ArrayList;

public class addscript extends AppCompatActivity {
    pjadpscriptadd adpscript;
    ArrayList<script> list;
    ArrayList<Long> dellist;
    Context context;

    private Long editID;
    int saveme=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addscript);

        if(!MainActivity.isClient)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context=this;
        list= new ArrayList<script>() ;
        dellist= new ArrayList<>();

        editID= Long.valueOf(-1);
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            editID = b.getLong("ID", -1);
            if (editID != -1) {
                saveme= b.getInt("saveme");
                ((EditText) findViewById(R.id.edtscriptname)).setText(b.getString("name"));
                scriptdb db= new scriptdb(this);
//            db.insertrecord(txtn.getText().toString(), MainActivity.infoMe.andMacAddress, RemoteCode, txtg.getText().toString(), selectedImagePath, ifToggle, 0, 0, 0);
                Cursor cursor = db.queryedit(editID);
                if (cursor!= null && cursor.moveToFirst()){
                    do{
//                        Log.d(MainActivity.Tag, "ورود به ویرایش سناریو"+ cursor.getLong(cursor.getColumnIndex(scriptdb.script_actID)));
                        script s=new script(cursor.getLong(cursor.getColumnIndex(scriptdb.script_ID)),
                                            cursor.getLong(cursor.getColumnIndex(scriptdb.script_actID)),
                                            cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_Name)),
                                            cursor.getInt(cursor.getColumnIndex(scriptdb.script_actstate)),
                                cursor.getInt(cursor.getColumnIndex(scriptdb.script_SaveMeAction)),
                                cursor.getString(cursor.getColumnIndex(scriptdb.script_ServerAction)));
                        list.add(s);
                        if(!cursor.getString(cursor.getColumnIndex(scriptdb.script_Duration)).equals("00:00:00")) {
                            s = new script(cursor.getLong(cursor.getColumnIndex(scriptdb.script_ID)),1,
                                    cursor.getString(cursor.getColumnIndex(scriptdb.script_Duration)), script.ScriptTime, 0,
                                    cursor.getString(cursor.getColumnIndex(scriptdb.script_ServerAction)));
                            list.add(s);
                        }
                    }while(cursor.moveToNext());
                }

            }
        }
        adpscript= new pjadpscriptadd(list, this);
        ListView lv= (ListView) findViewById(R.id.listViewscript);
        lv.setAdapter(adpscript);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onLongListclick(view, position);
                return true;
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        final int wme = (int) (display.getWidth() );/// getResources().getDisplayMetrics().density);

        FloatingActionButton fabs = (FloatingActionButton) findViewById(R.id.addactionscript);
        fabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, sel_action_script.class), 123);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addtimescript);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                final Dialog dialog=new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(7000000));//android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.activity_add_time_to_script);
//                dialog.setTitle(null);

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = 0;
                wmlp.y = 0;
                wmlp.width= wme;

                /*final NumberPicker hours = (NumberPicker ) dialog.findViewById(R.id.hour);
//                hours.setViewAdapter(new NumericWheelAdapter(context, 0, 12));
                hours.setMaxValue(12);
                hours.setMinValue(0);

                final NumberPicker  mins = (NumberPicker ) dialog.findViewById(R.id.mins);
//                mins.setViewAdapter(new NumericWheelAdapter(context, 0, 59, "%02d"));
                mins.setMinValue(0);
                mins.setMaxValue(60);
//                mins.setCyclic(true);

                final NumberPicker  seconds = (NumberPicker ) dialog.findViewById(R.id.secondss);
//                seconds.setViewAdapter(new NumericWheelAdapter(context, 0, 59, "%02d"));
                seconds.setMaxValue(60);
                seconds.setMinValue(0);
//                seconds.setCyclic(true);
*/

//                Calendar c = Calendar.getInstance();
//                mins.setCurrentItem(c.get(Calendar.MINUTE));
//                hours.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));

                final NumberPicker hours= (NumberPicker) dialog.findViewById(R.id.houraddtimescript);
                hours.setMaxValue(12);
                hours.setMinValue(0);

                final NumberPicker mins= (NumberPicker) dialog.findViewById(R.id.minaddtimescript);
                mins.setMaxValue(60);
                mins.setMinValue(0);

                final NumberPicker seconds= (NumberPicker) dialog.findViewById(R.id.secaddtimescript);
                seconds.setMaxValue(60);
                seconds.setMinValue(2);

                Button btn= (Button) dialog.findViewById(R.id.btnokaddtimescrpt);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String h= (hours.getValue()<10) ?  "0"+ hours.getValue(): ""+hours.getValue();
                        String m= (mins.getValue()<10) ?  "0"+ mins.getValue(): ""+mins.getValue();
                        String s= (seconds.getValue()<10) ?  "0"+ seconds.getValue(): ""+seconds.getValue();
                        list.add(new script(-1, 1,h+":"+m+":"+ s, script.ScriptTime, 0,
                                MainActivity.infoMe.macme));
                        adpscript.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_save:

                mainmenudb mdb = new mainmenudb(context);
                long idscript;
                if (editID==-1)
                    idscript= mdb.insertScript(((EditText) findViewById(R.id.edtscriptname)).getText().toString(), 0, MainActivity.MacServer);
                else{
                    idscript= editID;
                    mdb.updatescript(idscript, saveme, ((EditText) findViewById(R.id.edtscriptname)).getText().toString(), MainActivity.MacServer);
                }

                scriptdb dbs = new scriptdb(context);
                for (int i=0; i<dellist.size(); i++){
                    dbs.delrecord(dellist.get(i));
                }
                String duration = "00:00:02";

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).state == script.ScriptTime)
                        duration = list.get(i).getName().toString();
                    else {
                        if(list.get(i).id==-1)
                            dbs.insertrecord(idscript, list.get(i).actionid, duration, list.get(i).state, saveme,list.get(i).saveme, MainActivity.MacServer, list.get(i).server);
                        else
                            dbs.updaterecord(list.get(i).id, list.get(i).actionid, duration, list.get(i).state, list.get(i).saveme, list.get(i).server);
                        duration = "00:00:02";
                    }
                }
                Log.e(MainActivity.Tag, "سناریو ذخیره شد");
                setResult(RESULT_OK);
                finish();
                return true;
            case android.R.id.home:
                finish();return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==123&&resultCode==RESULT_OK){
            Log.e(MainActivity.Tag, "دریافت از لیست برنامه" + data.getLongExtra("id", 0));
            script s=new script(-1, (data.getLongExtra("id", 0))
                    , data.getStringExtra("name"), data.getIntExtra("state", 0), data.getIntExtra("saveme", 0),
                    data.getStringExtra("server"));
            list.add(s);
            adpscript.notifyDataSetChanged();
        }
    }

    public void onLongListclick(View v, final int position){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        if (list.size()-1>=position && list.get(position).state != script.ScriptTime)
                            dellist.add(list.get(position).id);

                        list.remove(position);
                        if (list.size()-1>=position && list.get(position).state == script.ScriptTime)
                            list.remove(position);
                        adpscript.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("آیا از حذف این گزینه مطمئن هستید؟")
                .setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();

    }

}
