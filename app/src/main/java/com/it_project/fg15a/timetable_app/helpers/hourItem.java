package com.it_project.fg15a.timetable_app.helpers;

public class hourItem {
    private String sTimeFrom;
    private String sTimeTo;
    private String sSubject;
    private String sTeacher;
    private String sRoom;
    private String sEntryType;

    // Constructor
    // TODO: Add the note part
    public hourItem(String p_sTimeFrom, String p_sTimeTo, String[] p_sData) {
        this.sTimeFrom = p_sTimeFrom;
        this.sTimeTo = p_sTimeTo;
        this.sSubject = p_sData[2];
        this.sTeacher = p_sData[3];
        this.sRoom = p_sData[4];
        this.sEntryType = p_sData[5];
    }

    // Get methods
    public String getTimeFrom() {return this.sTimeFrom;}
    public String getTimeTo() {return this.sTimeTo;}
    public String getSubject() {return this.sSubject;}
    public String getTeacher() {return  this.sTeacher;}
    public String getRoom() {return this.sRoom;}
    public String getEntryType() {return this.sEntryType;}

    // Set methods
    public void setTimeFrom(String p_sTimeFrom) {this.sTimeFrom = p_sTimeFrom;}
    public void setTimeTo(String p_sTimeTo) {this.sTimeTo = p_sTimeTo;}
    public void setSubject(String p_sSubject) {this.sSubject = p_sSubject;}
    public void setTeacher(String p_sTeacher) {this.sTeacher = p_sTeacher;}
    public void setRoom(String p_sRoom) {this.sRoom = p_sRoom;}
    public void setEntryType(String p_sEntryType) {this.sEntryType = p_sEntryType;}
}
