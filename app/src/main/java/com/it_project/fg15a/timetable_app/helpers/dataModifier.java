package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class dataModifier {

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

    // This method returns the processed data of a timetable html file
    public Map<String, String[]> modifyContent (String p_sWebsiteContent) {
        // map to store all processed data
        // Structure: {Column Index_Row Index, [(0), (1), (2), (3), (4)]}
        // Example -> Time: {0_1, [(0)Time from, (1)Time to]}
        // Example -> Hour: {1_1, [(2)Subject, (3)Teacher, (4)Room]}
        Map<String, String[]> mData = new LinkedHashMap<>();

        // turn p_sWebsiteContent into a HTML document
        Document docPage = Jsoup.parse(p_sWebsiteContent);

        // select all rows which are direct children of tbody of the table, max count: 21
        Elements eRows = docPage.select("table[border='3'] > tbody > tr");

        // iterate through each row (= HTML tr tag) of the table
        for (Element eRow : eRows) {
            // index of tr tag in tbody
            int iRowIndex = eRow.elementSiblingIndex();

            // select all td tags of tr tag, max count: 7
            Elements eColumns = eRow.children();

            // iterate through each definition (= HTML td tag) of the actual row
            for (Element eColumn : eColumns) {
                // index of td tag in tr tag
                int iColumnIndex = eColumn.elementSiblingIndex();

                // initialize string array for data
                String[] sData = new String[5];

                // if column index is zero then there are time values
                if (iColumnIndex == 0 && iRowIndex != 0) {
                    sData[0] = "sFrom: " + eColumn.select("> table > tbody > tr > td + td").text();
                    sData[1] = "sTo: " + eColumn.select("> table > tbody > tr + tr > td").text();

                    // add data with unique index to map
                    mData.put(String.valueOf(iColumnIndex) + "_" + String.valueOf(iRowIndex) , sData);
                }
                // if no time or day values then subject, teacher and room values
                // TODO: Add check for hex code of color red
                // TODO: Add check for half blocks
                // TODO: Add check for redundant values
                // TODO: Add check for empty values
                // TODO: Add check for course blocks (= tds with 20% width)
                else if (iColumnIndex != 0 && iRowIndex != 0) {
                    // select all tr tags of the table of the td tag
                    Elements eRemainingStrings = eColumn.select("> table > tbody > tr");

                    for (Element eRemainingString : eRemainingStrings) {
                        // index of tr tag of table in td tag
                        int iRemainingStringIndex = eRemainingString.elementSiblingIndex();

                        switch (iRemainingStringIndex) {
                            case 0:
                                // set subject
                                sData[2] = "sSubject: " + eRemainingString.text();
                                break;
                            case 1:
                                // set teacher
                                sData[3] = "sTeacher: " + eRemainingString.text();
                                break;
                            case 2:
                                // set room
                                sData[4] = "sRoom: " + eRemainingString.text();
                                break;
                            default:
                                break;
                        }
                    }

                    // add data with unique index to map
                    mData.put(String.valueOf(iColumnIndex) + "_" + String.valueOf(iRowIndex) , sData);
                }
            }
        }

        // return map with processed data
        return mData;
    }
}
