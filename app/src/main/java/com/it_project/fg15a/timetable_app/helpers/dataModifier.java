package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class dataModifier {

// TODO: create function that returns an array of strings with a specified type of data (teacher, subject, room) for a specific class
// function needs class, week
    public void modifyData (String p_sWebsite) {
        Document docWebsite = Jsoup.parse(p_sWebsite); // transform source code to HTML document
    }
}
