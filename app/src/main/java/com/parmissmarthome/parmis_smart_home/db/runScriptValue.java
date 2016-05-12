package com.parmissmarthome.parmis_smart_home.db;

/**
 * Created by YA-MAHDI on 18/01/2016.
 */
public class runScriptValue {
    String command;

    int delay;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public runScriptValue(String command, int delay) {
        this.command = command;
        this.delay = delay;
    }
}
