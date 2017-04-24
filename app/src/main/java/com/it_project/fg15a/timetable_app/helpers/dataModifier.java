package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class dataModifier {

    // TODO: Completely rework this method, it has to use a sorted array and should seperate:
    // TODO: times, subjects, teachers and rooms
    @Deprecated
    public String[] modifyData(String p_sWebsite) {
        Map<String, String> mHours = new LinkedHashMap<>();

        Document docWebsite = Jsoup.parse(p_sWebsite); // transform source code to HTML document

        // select all direct child rows of the tables body
        Elements eRows = docWebsite.select("body.tt table[border='3'] > tbody > tr");

        for (Element eRow : eRows) {
            // exclude all empty rows
            if ((eRow.text() != null || !eRow.text().isEmpty()) && eRow.hasText()) {
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

    // This method shall return the processed data of a timetable html file
    // TODO: Include check for color and empty values
    public String[] modifyContent (String p_sWebsiteContent) {
        // map to store all processed data
        // final structure should be
        // 0, Times
        // 1, Data of monday
        // ..., Data of day x
        // 6, Data of saturday
        Map<String, String> mData = new LinkedHashMap<>();

        // turn p_sWebsiteContent into a HTML document
        Document docPage = Jsoup.parse(p_sWebsiteContent);

        // select all rows which are direct children of tbody of the table, max count: 21
        Elements eRows = docPage.select("table[border='3'] > tbody > tr");

        for (Element eRow : eRows) {
            // index of tr tag in tbody
            int iRowIndex = eRow.elementSiblingIndex();

            // select all td tags of tr tag, max count: 7
            Elements eDefinitions = eRow.children();

            for (Element eDefinition : eDefinitions) {
                // index of td tag in tr tag
                int iDefinitionIndex = eDefinition.elementSiblingIndex();

                // if column index is zero then it's a time value
                if (iDefinitionIndex == 0) {
                    String sFrom = "sFrom: " + eDefinition.select("> table > tbody > tr > td + td")
                            .text();
                    mData.put(iRowIndex + "_" + iDefinitionIndex, sFrom);
                    String sTo = "sTo: " + eDefinition.select("> table > tbody > tr + tr > td")
                            .text();
                    mData.put(iRowIndex + "_" + iDefinitionIndex, sTo);
                }
                // if row index is zero then it's the name of a day
                else if (iRowIndex == 0) {
                    String sDay = "sDay: " + eDefinition.text();
                    mData.put(iRowIndex + "_" + iDefinitionIndex, sDay);
                }
                // if it's not a time or a day then it contains subject, teacher and room
                else {
                    // select all tr tags of the table of the td tag
                    Elements eRemainingStrings = eDefinition.select("> table > tbody > tr");

                    for (Element eRemainingString : eRemainingStrings) {
                        // index of tr tag of table in td tag
                        int iRemainingStringIndex = eRemainingString.elementSiblingIndex();

                        switch (iRemainingStringIndex) {
                            case 0:
                                // get subject and put it in map
                                String sSubject = "sSubject: " + eRemainingString.text();
                                mData.put(iRowIndex + "_" + iDefinitionIndex + "_" +
                                        iRemainingStringIndex, sSubject);
                                break;
                            case 1:
                                // get teacher and put it in map
                                String sTeacher = "sTeacher: " + eRemainingString.text();
                                mData.put(iRowIndex + "_" + iDefinitionIndex + "_" +
                                        iRemainingStringIndex, sTeacher);
                                break;
                            case 2:
                                // get room and put it in map
                                String sRoom = "sRoom: " + eRemainingString.text();
                                mData.put(iRowIndex + "_" + iDefinitionIndex + "_" +
                                        iRemainingStringIndex, sRoom);
                                break;
                            default:
                                break;

                        }
                    }
                }
            }
        }

        // return map as array to be able to be put in ListView
        return mData.values().toArray(new String[0]);
    }
}
