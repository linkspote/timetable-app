package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class dataModifier {

// TODO: create function that returns an array of strings with a specified type of data (teacher, subject, room) for a specific class
// function needs class, week
    public void modifyData (String p_sWebsite, String[] p_sArrHours) {
        Document docWebsite = Jsoup.parse(p_sWebsite); // transform source code to HTML document

        Elements eTimetable = docWebsite.select("body.tt table[border='3'] tbody");
        Elements eRows = eTimetable.select("tr:has(td)");

        for (Element eRow : eRows) {
            Elements eCells = eRow.select("td");

            for (Element eCell : eCells) {
                p_sArrHours[eCell.siblingIndex()] = eCell.text();
            }
        }
    }
}
