package com.parmissmarthome.parmis_smart_home.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YA-MAHDI on 02/08/2015.
 */
public class allprogram {
    private String name;


    private long id;
    private List<mainmenuitem> list;

    public allprogram(String name, long id) {
        this.name = name;
        this.id=id;
        list= new ArrayList<mainmenuitem>();
    }
    public void addlist(mainmenuitem item){
        list.add(item);
    }
    public mainmenuitem getitem(int index){
        return list.get(index);
    }
    public void removeitem(int inddex){
        list.remove(inddex);
    }
    public int getcount(){
        return list.size();
    }

    public String getName() {
        return name;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
