package com.it_project.fg15a.timetable_app.helpers;

public class hourItem {
    private String sTimeFrom;
    private String sTimeTo;
    private String sSubject;
    private String sTeacher;
    private String sRoom;

    // Constructor
    // TODO: Add the note part
    public hourItem(String p_sTimeFrom, String p_sTimeTo, String p_sSubject, String p_sTeacher,
                    String p_sRoom) {
        this.sTimeFrom = p_sTimeFrom;
        this.sTimeTo = p_sTimeTo;
        this.sSubject = p_sSubject;
        this.sTeacher = p_sTeacher;
        this.sRoom = p_sRoom;
    }

    // Get methods
    public String getTimeFrom() {return this.sTimeFrom;}
    public String getTimeTo() {return this.sTimeTo;}
    public String getSubject() {return this.sSubject;}
    public String getTeacher() {return  this.sTeacher;}
    public String getRoom() {return this.sRoom;}

    // Set methods
    public void setTimeFrom(String p_sTimeFrom) {this.sTimeFrom = p_sTimeFrom;}
    public void setTimeTo(String p_sTimeTo) {this.sTimeTo = p_sTimeTo;}
    public void setSubject(String p_sSubject) {this.sSubject = p_sSubject;}
    public void setTeacher(String p_sTeacher) {this.sTeacher = p_sTeacher;}
    public void setRoom(String p_sRoom) {this.sRoom = p_sRoom;}
}
