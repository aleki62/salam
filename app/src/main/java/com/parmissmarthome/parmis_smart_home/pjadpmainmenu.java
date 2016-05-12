package com.parmissmarthome.parmis_smart_home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parmissmarthome.parmis_smart_home.db.mainmenudb;
import com.parmissmarthome.parmis_smart_home.db.scriptdb;

/**
 * Created by Ya-Mahdi on 22/11/2014.
 */
public class pjadpmainmenu extends BaseAdapter {
    Context mcontext;
    Cursor mcursor;
    LayoutInflater inflater;

    private int mItemHeight = 0, mItemWidth=0;
    private int mNumColumns = 0;
    private RelativeLayout.LayoutParams mImageViewLayoutParams;// - See more at: http://techiedreams.com/android-custom-gridview-scalable-auto-adjusting-col-width/#sthash.BGfgC7eL.dpuf
    RelativeLayout.LayoutParams mBtnparams;

    public pjadpmainmenu(Context ct, Cursor cs){
        mcontext=ct;
        mcursor=cs;
        inflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mBtnparams= new RelativeLayout.LayoutParams(mItemWidth/2, mItemHeight);
    }
    @Override
    public int getCount() {
        return mcursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public void setItemHeight(int height, int wid) {
        if (height == mItemHeight && wid==mItemWidth) {
            return;
        }
        mItemHeight = height;
        mItemWidth  = wid;
//        mImageViewLayoutParams = new RelativeLayout.LayoutParams(mItemWidth, mItemHeight); //RelativeLayout.LayoutParams.MATCH_PARENT
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight);
        mBtnparams= new RelativeLayout.LayoutParams(mItemWidth/2, mItemWidth);
        notifyDataSetChanged();
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setNumColumns(int mNumColumns) {
        this.mNumColumns = mNumColumns;
    }

    static class ViewHolder {
        public TextView title;
        public ImageView img;
        public LinearLayout llb;
        public LinearLayout btnon;
        public LinearLayout btnoff;
        public ImageView imgbtnon;
        public ImageView imgbtnoff;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        View rowview=convertView;
        if (rowview==null){
            vh=new ViewHolder();
//            LayoutInflater inflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview= inflater.inflate(R.layout.photo_item, null);
            vh.img= (ImageView) rowview.findViewById(R.id.mainitemimg);
//            vh.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vh.title= (TextView) rowview.findViewById(R.id.mainitemtxt);
            vh.btnon= (LinearLayout) rowview.findViewById(R.id.mmbtnlOn);
            vh.btnoff= (LinearLayout) rowview.findViewById(R.id.mmbtnlOff);
            vh.llb= (LinearLayout) rowview.findViewById(R.id.llphotoitemBotton);
            vh.imgbtnoff= (ImageView) rowview.findViewById(R.id.imglightoff);
            vh.imgbtnon= (ImageView) rowview.findViewById(R.id.imglighton);

                    rowview.setTag(vh);
        }
        else vh=(ViewHolder)rowview.getTag() ;

        if(mcursor.moveToPosition(position)) {
            vh.img.setLayoutParams(mImageViewLayoutParams);
            mBtnparams= new RelativeLayout.LayoutParams(mItemWidth/2, mItemHeight- vh.llb.getHeight());
//            mBtnparams.height= mBtnparams.height- vh.title.getHeight();
            vh.btnon.setLayoutParams(mBtnparams);

//            vh.btnon.setLayoutParams();
            if (vh.img.getLayoutParams().height != mItemHeight) {
                vh.img.setLayoutParams(mImageViewLayoutParams);
//                vh.btnon.setLayoutParams(mBtnparams);
            }

            final View finalRowview1 = rowview;
            vh.btnon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mcursor.moveToPosition(position);
                    String cmd= String.format("*SNDRF*%s*%s#",
                            String.valueOf(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteCode)) +
                                    Integer.valueOf(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey)))),
                            "128");
                    MainActivity.sendtocenterrf(cmd, finalRowview1, mcursor.getLong(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)), 128);
//                    Log.e(MainActivity.Tag, "cmd is: "+cmd);
                    v.startAnimation(AnimationUtils.loadAnimation(mcontext, R.anim.anim_scale_btn));
//                    Toast.makeText(mcontext, "لامپ روشن", Toast.LENGTH_LONG).show();
                }
            });
            vh.btnoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mcursor.moveToPosition(position);
                    MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                            String.valueOf(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteCode)) +
                                    Integer.valueOf(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey)))),
                            "0"), finalRowview1,
                            mcursor.getLong(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)), 0);
                    v.startAnimation(AnimationUtils.loadAnimation(mcontext, R.anim.anim_scale_btn));
                }
            });
            vh.btnon.setVisibility(View.INVISIBLE);
            vh.btnoff.setVisibility(View.INVISIBLE);
            switch (mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_Vaz))) {
                case ((script.ScriptYes)):
                    vh.img.setImageResource(R.mipmap.ic_script);
                    break;
                case script.ScriptOFF:  // تعریف ریموت توسط اندروید
                    vh.imgbtnoff.setImageResource(R.mipmap.lightoff);
                    vh.imgbtnon.setImageResource(R.mipmap.lighton);
                    vh.btnon.setVisibility(View.VISIBLE);
                    vh.btnoff.setVisibility(View.VISIBLE);
                    if (mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))!=null)
                        vh.img.setImageURI(Uri.parse(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))));
                        /*vh.img.setImageBitmap(MainActivity.combineImages(BitmapFactory.decodeFile(
                                mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))),
                                BitmapFactory.decodeResource(mcontext.getResources(),R.drawable.shortcut)));*/
                    else
                        vh.img.setImageResource(R.mipmap.ic_defaultlamp);
                        /*vh.img.setImageBitmap(MainActivity.combineImages(
                                        BitmapFactory.decodeResource(mcontext.getResources(),R.mipmap.ic_defaultlamp),
                                        BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.shortcut)));*/
                    break;
                case script.ScriptCurtianAndroid:
                    vh.imgbtnoff.setImageResource(R.mipmap.ic_curtian_close);
                    vh.imgbtnon.setImageResource(R.mipmap.ic_curtian_open);
                    vh.btnon.setVisibility(View.VISIBLE);
                    vh.btnoff.setVisibility(View.VISIBLE);
                    if (mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))!="")
                        vh.img.setImageURI(Uri.parse(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))));
//                        vh.img.setImageBitmap(MainActivity.setPic(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image)), mItemHeight, mItemWidth, null));
                    else
                        vh.img.setImageResource(R.mipmap.ic_defaultlamp);
                    break;
                case script.ScriptToggle:
                    if (mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))!="")
                        vh.img.setImageURI(Uri.parse(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))));
                    else
                        vh.img.setImageResource(R.mipmap.ic_defaultlamp);
//                    vh.btnon.setVisibility(View.VISIBLE);
//                    vh.btnoff.setVisibility(View.VISIBLE);
                    break;
                case script.ScriptTv:
                    if (mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))!="")
                        vh.img.setImageURI(Uri.parse(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))));
                    else
                        vh.img.setImageResource(R.mipmap.ic_tv);
            }

            vh.title.setText(String.format("%s(%s)", mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Name)), mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Group))));
            rowview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (MainActivity.mTcpClient!=null) {
                    mcursor.moveToPosition(position);
                    switch (mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_Vaz))) {
                        case ((script.ScriptYes)):
                            if (MainActivity.scriptIsRuning.isrunning(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)))) {
                                Snackbar.make(finalRowview1, "سناریوی انتخابی در حال اجرا می باشد", Snackbar.LENGTH_SHORT).show();
                            } else {
                                scriptdb dbs = new scriptdb(mcontext);

                                MainActivity.scriptIsRuning.add(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)),
                                        dbs.getscriptvalues(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)), null));
                            }
                            break;
                        case script.ScriptToggle:
                            MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                                    mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey)), "-1"), finalRowview1,
                                    mcursor.getLong(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)), 0);
                            break;
                        case script.ScriptTv:
                            mcontext.startActivity(new Intent(mcontext, TVRemote.class));
                            break;
                    }
                    /*}else
                        Snackbar.make(finalRowview1, "اتصال با واسط اندروید برقرار نیست", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                }
            });
            rowview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    Snackbar.make(finalRowview1, " نیست", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    if (mcursor.moveToPosition(position))
                        alertMessage(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)),
                                mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_SaveMe)),
                                mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Server)));
                    return true;
                }
            });
        }
        return rowview;
    }
    private void alertMessage(final long id, final int saveme, final String server) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        mainmenudb db=new mainmenudb(mcontext);
                        db.setVisMainMenu(id, saveme, 0, server);
//                        MainActivity.changedata=true;
                        mcursor= db.query(" t1." + mainmenudb.MainMenu_VisMain + "<>0", null);
//                        adp.mcursor=maincursor;
                        notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setMessage("آیا از حذف این گزینه مطمئن هستید؟")
                .setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();
    }

}
