package com.parmissmarthome.parmis_smart_home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by YA-MAHDI on 07/11/2015.
 */
public class pjadpscriptadd extends BaseAdapter{
    private ArrayList<script> list;
    Context context;

    public void setList(ArrayList<script> list) {
        this.list = list;
    }
    public void addtolist(script scrpt){
        list.add(scrpt);
    }

    public pjadpscriptadd(ArrayList<script> l, Context context){
        list=new ArrayList<script>();
        list=l;
        this.context=context;
    }

    @Override
    public int getCount() {
        if (list==null)
            return 0;
        else
            return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (list.get(position).id);
    }
    static class ViewHolder {
        public TextView title;
        public ImageView img;
        public ImageView imglamp;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        View rowview=convertView;
        if (rowview==null){
            vh=new ViewHolder();
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview= inflater.inflate(R.layout.script_item, parent, false);
            vh.img= (ImageView) rowview.findViewById(R.id.imgscriptshow);
            vh.imglamp= (ImageView) rowview.findViewById(R.id.imgsriptitem);
            vh.title= (TextView) rowview.findViewById(R.id.txtscriptshow);

            rowview.setTag(vh);
        }
        else vh=(ViewHolder)rowview.getTag() ;

//        if(list.size()<position) {
            if (position==0)
                vh.img.setVisibility(View.INVISIBLE);
            else
                vh.img.setVisibility(View.VISIBLE);
//9362476733
//            File f= new File(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image)));
//            Log.d(MainActivity.Tag, mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image)));
//            vh.img.setImageURI(Uri.parse(mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_Image))));
//            if(f.exists()) {
//                Log.d(MainActivity.Tag,"فایل هس");
//                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());

//                vh.img.setImageBitmap(b);
//            } else {
//                vh.img.setImageResource(R.drawable.khab3);
//                Log.d(MainActivity.Tag, "فایل وجود ندارد");
//            }
//        Log.e(MainActivity.Tag, list.get(position).getName()+" test");
            vh.title.setText(list.get(position).getName());
            switch (list.get(position).state){
                case script.ScriptOFF:
                    vh.imglamp.setImageResource(R.mipmap.btn_lamp_off_enabled);
                    vh.imglamp.setVisibility(View.VISIBLE);
                    break;
                case script.ScriptON:
                    vh.imglamp.setImageResource(R.mipmap.btn_lamp_on_enabled);
                    vh.imglamp.setVisibility(View.VISIBLE);
                    break;
                default:
                    vh.imglamp.setVisibility(View.INVISIBLE);
            }
//        }
        return rowview;
    }
}
