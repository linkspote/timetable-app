package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class dataModifier {

    // TODO: create function that returns an array of strings with a specified type of data (teacher, subject, room) for a specific class
// function needs class, week
    public String[] modifyData(String p_sWebsite) {
        Map<String, String> mHours = new LinkedHashMap<>();

        Document docWebsite = Jsoup.parse(p_sWebsite); // transform source code to HTML document

        Elements eRows = docWebsite.select("body.tt table[border='3'] > tbody > tr:has(td)"); // select all direct child rows of the tables body

        for (Element eRow : eRows) {
            if ((eRow.text() != null || !eRow.text().isEmpty()) && eRow.hasText()) { // exclude all empty rows
                int iRowIndex = eRow.elementSiblingIndex();
                Elements eCells = eRow.select("> td"); // select all direct child cell of this row

                for (Element eCell : eCells) {
                    if ((eCell.text() != null || !eCell.text().isEmpty()) && eCell.hasText()) {
                        int iRowCellIndex = eCell.elementSiblingIndex();

                        if (eCell.elementSiblingIndex() == 0) {
                            mHours.put(iRowIndex + "_" + iRowCellIndex, "Zeit:" + eCell.text());

                        } else {
                            if (eRow.elementSiblingIndex() == 0) {
                                mHours.put(iRowIndex + "_" + iRowCellIndex, "Tag:" + eCell.text());

                            } else {
                                mHours.put(iRowIndex + "_" + iRowCellIndex, eCell.text());

                            }
                        }
                    }
                }
            }
        }
        return mHours.values().toArray(new String[0]);
    }
}
