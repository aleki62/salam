package com.parmissmarthome.parmis_smart_home.db;

/**
 * Created by Ya-Mahdi on 24/11/2014.
 */
public class mainmenuitem {
    private String name;
    private String imgpath;
    private long id;
    private String groupkey;
    private String RemoteKey;
    private int RemoteCode;
    private int vaz;
    private int vismain;
    private int col;
    private int row;

    private int saveme;

    private long parent;

    private String server;

    public mainmenuitem(String name, String img, long id, String group, String remotekey, int remoteCode, int vaz,
                        int vismain, int col, int row, long prnt, int saveme, String server) {
        this.name = name;
        this.imgpath = img;
        this.id = id;
        this.groupkey = group;
        RemoteKey = remotekey;
        RemoteCode = remoteCode;
        this.vaz = vaz;
        this.vismain = vismain;
        this.col=col;
        this.row=row;
        parent=prnt;
        this.saveme= saveme;
        this.server=server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return imgpath;
    }

    public void setSrc(String src) {
        this.imgpath = src;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup() {
        return groupkey;
    }

    public void setGroup(String group) {
        this.groupkey = group;
    }

    public String getRemoteKey() { return RemoteKey; }

    public void setRemoteKey(String remoteKey) {RemoteKey = remoteKey;}

    public int getRemoteCode() { return RemoteCode; }

    public void setRemoteCode(int remoteCode) {RemoteCode = remoteCode;}

    public int getVaz() {
        return vaz;
    }

    public void setVaz(int vaz) {
        this.vaz = vaz;
    }

    public int getVismain() {
        return vismain;
    }

    public void setVismain(int vismain) {
        this.vismain = vismain;
    }
    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getSaveme() {
        return saveme;
    }

    public void setSaveme(int saveme) {
        this.saveme = saveme;
    }

    public String getServer() {return server;}

    public void setServer(String server) {this.server = server;}

}
