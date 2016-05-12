package com.parmissmarthome.parmis_smart_home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by YA-MAHDI on 09/04/2016.
 */
public class pjadpopup extends BaseAdapter {
    private final int[] image;
    LayoutInflater inflater;
    String[] caption;

    public pjadpopup(LayoutInflater inf, String[] values, int[] img) {
        this.inflater = inf;
        caption= values;
        image= img;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder{
        public TextView textView;
        public ImageView imageView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        View view= convertView;
        if (view==null){
            vh= new ViewHolder();
            view= inflater.inflate(R.layout.popupitem, null);
            vh.textView= (TextView) view.findViewById(R.id.popuptext);
            vh.imageView= (ImageView) view.findViewById(R.id.popupimage);
            view.setTag(vh);
        } else vh= (ViewHolder) view.getTag();

        vh.imageView.setImageResource(image[position]);
        vh.textView.setText(caption[position]);


        return view;
    }
}
