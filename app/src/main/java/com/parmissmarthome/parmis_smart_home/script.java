package com.parmissmarthome.parmis_smart_home;

/**
 * Created by YA-MAHDI on 07/11/2015.
 */
public class script {
    long id, actionid;
    int saveme;
    private String name;
    int state;
    public final static int ScriptON=1,
            ScriptOFF=0,
            ScriptToggle=2,
            ScriptTime=3,
            ScriptYes=4,
            ScriptCurtianToggle=5,
            ScriptCurtianAndroid=6,
            ScriptTv=7;
    public String server;

    public script(long id, long actionid, String name, int state, int saveme, String server){
        this.id=id;
        this.actionid= actionid;
        this.name=name;
        this.state=state;
        this.saveme= saveme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
