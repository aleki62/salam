package com.parmissmarthome.parmis_smart_home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parmissmarthome.parmis_smart_home.db.allprogram;
import com.parmissmarthome.parmis_smart_home.db.mainmenudb;
import com.parmissmarthome.parmis_smart_home.db.mainmenuitem;
import com.parmissmarthome.parmis_smart_home.db.scriptdb;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by YA-MAHDI on 29/07/2015.
 */
public class pjadpallprogram extends BaseExpandableListAdapter {
    Context context;
    private int imgh=57, imgw=115;
    private mainmenudb db;
    private List<allprogram> mainmenuList;
    boolean ifsel;
    LayoutInflater layoutInflater;

    public pjadpallprogram(FragmentActivity applicationContext, boolean ifsel) {
        context = applicationContext;
        layoutInflater= applicationContext.getLayoutInflater();
        this.ifsel=ifsel;
        fulllist();

    }
        /*
        در این قسمت اطلاعات مربوط به قسمتهای مختلف بر اساس مکان آنها دسته بندی می شود و در آرایه دوبعدی قرار داده می شود.
        * */
    private void fulllist(){
        Cursor gcursor;
        Cursor ccursor;
        db=new mainmenudb(context);
        gcursor= db.querygroup(null);
//        Log.d(MainActivity.Tag, "تعداد منو"+ gcursor.getCount());

        mainmenuList=new ArrayList<allprogram>();
        allprogram item;
        mainmenuitem mitem;

        if(gcursor.moveToFirst()) {
            do{
                item = new allprogram(gcursor.getString(gcursor.getColumnIndex(mainmenudb.MainMenu_Name)), gcursor.getLong(gcursor.getColumnIndex(mainmenudb.MainMenu_ID)));
                ccursor=db.query("t1." + mainmenudb.MainMenu_Parent + "=" + item.getId(), null);
                if(ccursor.moveToFirst()) {
//                Log.d(MainActivity.Tag ,"رنامه"+ "-----"+ccursor.getCount()/*+ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Parent))*/);
                    do {
//                Log.d(MainActivity.Tag, "لست پر شد"+ gcursor.getString(0));
                        mitem = new mainmenuitem(
                                ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Name)),
                                ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Image)),
                                ccursor.getLong(ccursor.getColumnIndex(mainmenudb.MainMenu_ID)),
                                ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Group)),
                                ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey)),
                                ccursor.getInt(ccursor.getColumnIndex(mainmenudb.MainMenu_RemoteCode)),
                                ccursor.getInt(ccursor.getColumnIndex(mainmenudb.MainMenu_Vaz)),
                                ccursor.getInt(ccursor.getColumnIndex(mainmenudb.MainMenu_VisMain)),
                                0, 0,
                                item.getId(),
                                ccursor.getInt(ccursor.getColumnIndex(mainmenudb.MainMenu_SaveMe)),
                                ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Server)));
                        item.addlist(mitem);
                    } while (ccursor.moveToNext());

                    mainmenuList.add(item);
                }
            }while (gcursor.moveToNext());
        }
    }

    @Override
    public int getGroupCount() {
        return mainmenuList.size();
//        return gcursor.getCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        Log.d(MainActivity.Tag, "فرزند "+mainmenuList.get(groupPosition).getcount());
        return mainmenuList.get(groupPosition).getcount();
//        return ccursor.getCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mainmenuList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mainmenuList.get(groupPosition).getitem(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mainmenuList.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mainmenuList.get(groupPosition).getitem(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void setImgw(int imgw, int imgh) {
        this.imgw = imgw;
        this.imgh = imgh;
    }

    static class grpview{
        public TextView title;
        long id;
    }
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
//        if(gcursor.moveToPosition(groupPosition)) {
        if(groupPosition<mainmenuList.size()){
            final grpview gv;
            View rowview = convertView;
            if (rowview == null) {
                gv = new grpview();
                LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowview = inf.inflate(R.layout.groupallprogram, parent, false);
                gv.title = (TextView) rowview.findViewById(R.id.txtgroupview);
                rowview.setTag(gv);
            } else
                gv = (grpview) rowview.getTag();
//            gv.title.setText(gcursor.getString(gcursor.getColumnIndex(mainmenudb.MainMenu_Group)));
            gv.title.setText(mainmenuList.get(groupPosition).getName());
            gv.id= mainmenuList.get(groupPosition).getId();

            final PopupMenu popupMenu = new PopupMenu(context, rowview);
            popupMenu.getMenuInflater().inflate(R.menu.ppallprg, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.ppaddtomain:
//                            db.setVisMainMenu(mainmenuList.get(groupPosition).getId(), mainmenuList.get(groupPosition).getSaveme(), 1);
                            break;
                        case R.id.ppdelmenu:
                            break;
                        case R.id.ppeditmenu:
                            break;
                    }
                    return false;
                }
            });
            /*rowview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    popupMenu.show();
                    return true;
                }
            });
*/
            return rowview;
        }
        return null;
    }
    static class childview{
        ImageView img;
        TextView title;
        ImageButton btnon;
        ImageButton btnoff;
        long id;
        int state;
    }
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, final View convertView, final ViewGroup parent) {
//        if(gcursor.moveToPosition(groupPosition)&&ccursor.moveToPosition(childPosition)){
//        if(groupPosition< mainmenuList.size()&&childPosition<mainmenuList.get(groupPosition).getcount()){
            final childview cv;
            View rowview=convertView;
            if(rowview==null){
                LayoutInflater inf=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowview=inf.inflate(R.layout.childallprogram, parent, false);
                cv=new childview();
                cv.img= (ImageView) rowview.findViewById(R.id.imgchildall);
                cv.title= (TextView) rowview.findViewById(R.id.txtchildall);
                cv.btnon= (ImageButton)rowview.findViewById(R.id.btnlampOnall);
                cv.btnoff= (ImageButton)rowview.findViewById(R.id.btnlampOffall);
//                cv.img.setLayoutParams(new RelativeLayout.LayoutParams(cv.btnon.getHeight(), cv.btnon.getHeight()));
                rowview.setTag(cv);
            }
            else cv=(childview)rowview.getTag();

//            cv.title.setText(ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Name)));
//            cv.img.setImageURI(Uri.parse(ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Image))));
            cv.title.setText(mainmenuList.get(groupPosition).getitem(childPosition).getName());
//            cv.img.setImageBitmap(MainActivity.setPic(ccursor.getString(ccursor.getColumnIndex(mainmenudb.MainMenu_Image)), imgh, imgw));

            if(mainmenuList.get(groupPosition).getitem(childPosition).getVaz()==script.ScriptYes)
                cv.img.setImageResource(R.mipmap.ic_script);
            else
                if (mainmenuList.get(groupPosition).getitem(childPosition).getSrc()!=null)
                    cv.img.setImageURI(Uri.parse(mainmenuList.get(groupPosition).getitem(childPosition).getSrc()));
                else
                    cv.img.setImageResource(R.mipmap.ic_defaultlamp);
//                cv.img.setImageBitmap(MainActivity.setPic(mainmenuList.get(groupPosition).getitem(childPosition).getSrc(), imgh, imgw, null));

            cv.title.setHeight(cv.img.getHeight());

            if(mainmenuList.get(groupPosition).getitem(childPosition).getVaz()==script.ScriptOFF||
                    mainmenuList.get(groupPosition).getitem(childPosition).getVaz()==script.ScriptCurtianAndroid) {
                cv.btnoff.setVisibility(View.VISIBLE);
                cv.btnon.setVisibility(View.VISIBLE);
                switch (mainmenuList.get(groupPosition).getitem(childPosition).getVaz()){
                    case script.ScriptOFF:
                        cv.btnoff.setImageResource(R.mipmap.lightoff);
                        cv.btnon.setImageResource(R.mipmap.lighton);
                        break;
                    case script.ScriptCurtianAndroid:
                        cv.btnoff.setImageResource(R.mipmap.ic_curtian_close);
                        cv.btnon.setImageResource(R.mipmap.ic_curtian_open);
                        break;
                }
            }
            else {
                cv.btnoff.setVisibility(View.INVISIBLE);
                cv.btnon.setVisibility(View.INVISIBLE);
            }
            cv.id= mainmenuList.get(groupPosition).getitem(childPosition).getId();
        final View finalRowview = rowview;

        cv.btnon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ifsel) {

                        MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                                String.valueOf(mainmenuList.get(groupPosition).getitem(childPosition).getRemoteCode() +
                                        Integer.valueOf(mainmenuList.get(groupPosition).getitem(childPosition).getRemoteKey())),
                                "128"), finalRowview,
                                mainmenuList.get(groupPosition).getitem(childPosition).getId(),
                                128);
                    }
                    else{
                        Intent result= new Intent();
                        result.putExtra("id", cv.id);//(String) mainmenuList.get(groupPosition).getitem(childPosition).getId());
                        result.putExtra("name", cv.title.getText().toString());
                        result.putExtra("state", script.ScriptON);
                        result.putExtra("server", mainmenuList.get(groupPosition).getitem(childPosition).getServer());
                        ((sel_action_script)context).setResult(Activity.RESULT_OK, result);
                        ((sel_action_script)context).finish();
                    }
                    v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_scale_btn));
                }
            });
        cv.btnoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ifsel) {

                        MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                                String.valueOf(mainmenuList.get(groupPosition).getitem(childPosition).getRemoteCode() +
                                        Integer.valueOf(mainmenuList.get(groupPosition).getitem(childPosition).getRemoteKey())),
                                "0"), finalRowview,
                                mainmenuList.get(groupPosition).getitem(childPosition).getId(),
                                0);

                    }
                    else{
                        Intent result= new Intent();
                        result.putExtra("id", cv.id);//(String) mainmenuList.get(groupPosition).getitem(childPosition).getId());
                        result.putExtra("name", cv.title.getText().toString());
                        result.putExtra("state", script.ScriptOFF);
                        result.putExtra("server", mainmenuList.get(groupPosition).getitem(childPosition).getServer());
                        ((sel_action_script)context).setResult(Activity.RESULT_OK, result);
                        ((sel_action_script)context).finish();
                    }
                    v.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_scale_btn));
                }
            });
/*            final PopupMenu popupMenu = new PopupMenu(context, rowview);
            popupMenu.inflate(R.menu.ppallprg);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ppaddtomain:
                        db.setVisMainMenu(mainmenuList.get(groupPosition).getitem(childPosition).getId(), 1);
                        MainActivity.changedata=true;
                        Toast.makeText(context, "اضافه شد به منوی اصلی",
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.ppdelmenu:
                        alertMessage(groupPosition, childPosition);
                        break;
                    case R.id.ppeditmenu:
                        break;
                }
                return false;
            }
        });*/
            rowview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    popupMenu.show();
/*// Create the Snackbar
                    Snackbar snackbar = Snackbar.make(finalRowview, "", Snackbar.LENGTH_LONG);
// Get the Snackbar's layout view
                    Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
// Hide the text
                    TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setVisibility(View.VISIBLE);

// Inflate our custom view
                    View snackView = layoutInflater.inflate(R.layout.allprgsnackerlayout, null);
// Configure the view0
                        TextView btnshortcut = (TextView) snackView.findViewById(R.id.btnaddmainmenu);
                        btnshortcut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.setVisMainMenu(mainmenuList.get(groupPosition).getitem(childPosition).getId(), 1);
                                MainActivity.changedata = true;
                                Toast.makeText(context, "اضافه شد به منوی اصلی",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    TextView btndel = (TextView) snackView.findViewById(R.id.btndelinall);
                    btndel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertMessage(groupPosition, childPosition);
                        }
                    });

                    TextView btnedit = (TextView) snackView.findViewById(R.id.btneditinall);
                    btnedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(context, addaction.class);
                            intent.putExtra("ID",mainmenuList.get(groupPosition).getitem(childPosition).getId() );
                            ((FragmentActivity)context).startActivityForResult(intent, MainActivity.rqt_Lamp);
                        }
                    });
// Add the view to the Snackbar's layout
                    layout.addView(snackView, 0);
// Show the Snackbar
                    snackbar.show();*/
                    final Dialog dialog=new Dialog(context, R.style.DialogSlideAnim);//new ContextThemeWrapper(getBaseContext(), R.style.DialogSlideAnim));
                    dialog.getWindow().setGravity(Gravity.BOTTOM);

//                dialog.getWindow().getAttributes().windowAnimations =  R.style.DialogSlideAnim;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(7700000));//android.graphics.Color.TRANSPARENT));
                    dialog.setContentView(R.layout.popupmenu);
//                    dialog.setTitle(null);

                    ListView listview= (ListView) dialog.findViewById(R.id.listmenu);
                    String[] values=new String[]{"اضافه به منوی اصلی", "ویرایش", "حذف"};
                    int[] img= new int[]{R.mipmap.ic_shortcutmnu, R.mipmap.ic_edit, R.mipmap.ic_delete};

//                ArrayAdapter<String> aa= new ArrayAdapter<String>(context, R.layout.popupitem, R.id.popuptext, values);
//                ArrayAdapter<String> aa= new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, values);
                    pjadpopup aa= new pjadpopup((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE), values, img);
                    listview.setAdapter(aa);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position){
                                case 0:
                                    db.setVisMainMenu(mainmenuList.get(groupPosition).getitem(childPosition).getId(),
                                            mainmenuList.get(groupPosition).getitem(childPosition).getSaveme(), 1,
                                            mainmenuList.get(groupPosition).getitem(childPosition).getServer());
                                    MainActivity.changedata = true;
                                    Toast.makeText(context, "اضافه شد به منوی اصلی",
                                            Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    if(mainmenuList.get(groupPosition).getitem(childPosition).getVaz()!=script.ScriptYes) {
                                        Intent intent = new Intent(context, addaction.class);
                                        intent.putExtra("ID", mainmenuList.get(groupPosition).getitem(childPosition).getId());
                                        ((FragmentActivity) context).startActivityForResult(intent, MainActivity.rqt_Lamp);
                                    }else
                                    {
                                        Intent intent = new Intent(context, addscript.class);
                                        intent.putExtra("ID", mainmenuList.get(groupPosition).getitem(childPosition).getId());
                                        intent.putExtra("name", mainmenuList.get(groupPosition).getitem(childPosition).getName());
                                        ((FragmentActivity) context).startActivityForResult(intent, MainActivity.rqt_script);
                                    }
                                    break;
                                case 2:
                                    alertMessage(groupPosition, childPosition);
                                    break;
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });

        rowview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mainmenuList.get(groupPosition).getitem(childPosition).getVaz()) {
                    case script.ScriptToggle:
                        MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                                mainmenuList.get(groupPosition).getitem(childPosition).getRemoteKey(), "-1"), finalRowview,
                                mainmenuList.get(groupPosition).getitem(childPosition).getId(), 0);
                        break;
                    case script.ScriptYes:
                        if (MainActivity.scriptIsRuning.isrunning(mainmenuList.get(groupPosition).getitem(childPosition).getId()))
                            Toast.makeText(context,"سناریوی انتخابی در حال اجرا می باشد", Toast.LENGTH_SHORT).show();
                        else{
                            scriptdb dbs= new scriptdb(context);

                            MainActivity.scriptIsRuning.add(mainmenuList.get(groupPosition).getitem(childPosition).getId(),
                                    dbs.getscriptvalues(mainmenuList.get(groupPosition).getitem(childPosition).getId(), null));
                        }
                        break;
                    case script.ScriptTv:
                        context.startActivity(new Intent(context, TVRemote.class));
                        break;
                }
            }
        });
             return rowview;
//        }
//        return null;
    }
    public void alertMessage(final int grp, final int chp) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        db.deleterecord(mainmenuList.get(grp).getitem(chp).getId(),
                                mainmenuList.get(grp).getitem(chp).getSaveme(),
                                mainmenuList.get(grp).getitem(chp).getServer());
                        mainmenuList.get(grp).removeitem(chp-1);
                        notifyDataSetChanged();
                        MainActivity.changedata=true;
                        Log.d(MainActivity.Tag, "دیتا حذف شد");
                        Toast.makeText(context, "حذف شد",
                                Toast.LENGTH_LONG).show();
                        break;

/*                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(context, "No Clicked",
                                Toast.LENGTH_LONG).show();
                        break;
  */
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("آیا از حذف مطمئن هستید؟")
                .setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

}
