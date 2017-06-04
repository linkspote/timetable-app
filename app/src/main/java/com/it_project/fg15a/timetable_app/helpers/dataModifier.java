package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class dataModifier {

    public String getWebContent(String p_sUrl) {
        String sWebContent = "", sLine;

        InputStream isWebsite = null;
        try {
            URLConnection uconWebsite = new URL(p_sUrl).openConnection();
            uconWebsite.connect();
            isWebsite = uconWebsite.getInputStream();
            BufferedReader brWebsite = new BufferedReader(
                    new InputStreamReader(isWebsite, "UTF-8"));

            while ((sLine = brWebsite.readLine()) != null)
                sWebContent += sLine;

            brWebsite.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isWebsite != null) {
                try {
                    isWebsite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sWebContent;
    }

    // This method returns the preprocessed data of a timetable html file
    public Map<String, String[]> preModifyContent(String p_sWebsiteContent) {
        // map to store all processed data
        // Structure: {Column Index_Row Index, [(0), (1), (2), (3), (4)]}
        // Example -> Time: {0_1, [(0)Time from, (1)Time to]}
        // Example -> Hour: {1_1, [(2)Subject, (3)Teacher, (4)Room]}
        Map<String, String[]> mData = new LinkedHashMap<>();

        // list to store all column indexes at which all columns of the following rows need to be
        // moved to a new index
        List<Integer> lMoveColumns = new ArrayList<>();

        // turn p_sWebsiteContent into a HTML document
        Document docPage = Jsoup.parse(p_sWebsiteContent);

        // select all rows which are direct children of tbody of the table, max count: 21
        Elements eRows = docPage.select("table[border='3'] > tbody > tr:has(tr)");

        // iterate through each row (= HTML tr tag) of the table
        for (Element eRow : eRows) {
            // index of tr tag in tbody
            int iRowIndex = eRow.elementSiblingIndex();

            // if row index is not zero, then it's real content
            if (iRowIndex != 0) {
                // select all td tags of tr tag, max count: 7
                Elements eColumns = eRow.children();

                // bool to check for replacement lessons, half blocks and other special things
                boolean bWatchOut = (eColumns.size() < 7 && eColumns.size() > 2);

                // int to check the hour
                String sHour = "";

                // iterate through each definition (= HTML td tag) of the actual row
                for (Element eColumn : eColumns) {
                    // index of td tag in tr tag
                    int iColumnIndex = eColumn.elementSiblingIndex();

                    // initialize string array for data
                    // sData[0/1] => time from/to
                    // sData[2-4] => subject, teacher, room
                    // sData[5] => 1 - complete day, 2 - half block,
                    //             3 - replacement lessons/omission, 0 - default entry
                    // sData[6] => value of hour
                    String[] sData = new String[7];

                    // if column index is zero then there are time values
                    if (iColumnIndex == 0) {
                        sData[0] = eColumn.select("> table > tbody > tr > td + td").text();
                        sData[1] = eColumn.select("> table > tbody > tr + tr > td").text();
                        sData[5] = "t";
                        sData[6] = eColumn.select("> table > tbody > tr > td[rowspan='2']").text();
                        sHour = sData[6];

                        // add data with unique index to map
                        mData.put(String.valueOf(iColumnIndex) + "_" + String.valueOf(iRowIndex), sData);
                    }
                    // if no time or day values then subject, teacher and room values
                    // TODO: Add check for replacement lessons (hex code of color red and tds with rowspan = 2)
                    // TODO: Add check for half blocks (can be delayed)
                    // TODO: Add check for redundant values
                    // TODO: Add check for empty values
                    // TODO: Add check for course blocks (= tds with 20% width, can be delayed)
                    // TODO: Add check for complete days (= tds with rowspan = 20)
                    else {
                        // select all tr tags of the table of the td tag
                        Elements eRemainingStrings = eColumn.select("> table > tbody > tr");

                        if (eColumn.attr("rowspan").equals("20")) { // complete day
                            sData[5] = "1";
                            lMoveColumns.add(iColumnIndex);
                        } else if (eColumn.attr("rowspan").equals("2")) { // half block
                            if (eColumn.getElementsByAttributeValue("color", "#FF0000").size() != 0) { // replacement lesson/omission
                                sData[5] = "3";
                            } else { // default half block
                                sData[5] = "2";
                            }
                        } else { // default branch
                            sData[5] = "0";
                        }

                        for (Element eRemainingString : eRemainingStrings) {
                            // index of tr tag of table in td tag
                            int iRemainingStringIndex = eRemainingString.elementSiblingIndex();

                            switch (iRemainingStringIndex) {
                                case 0:
                                    // set subject
                                    sData[2] = eRemainingString.text();
                                    break;
                                case 1:
                                    // set teacher
                                    sData[3] = eRemainingString.text();
                                    break;
                                case 2:
                                    // set room
                                    sData[4] = eRemainingString.text();
                                    break;
                                default:
                                    break;
                            }
                        }

                        sData[6] = sHour;

                        // add data with unique index to map
                        mData.put(String.valueOf(iColumnIndex) + "_" + String.valueOf(iRowIndex), sData);
                    }
                }
            }
        }

        // return map with processed data
        return postModifyContent(mData, lMoveColumns);
    }

    // This method returns the final post processed data of a timetable html file
    public Map<String, String[]> postModifyContent (Map<String, String[]> p_mData,
                                                    List<Integer> p_lMoveColumns) {
        // map to store post processed data
        // Structure: {Column Index_Row Index, [(0), (1), (2), (3), (4)]}
        // Example -> Time: {0_1, [(0)Time from, (1)Time to]}
        // Example -> Hour: {1_1, [(2)Subject, (3)Teacher, (4)Room]}
        Map<String, String[]> mFinalData = new LinkedHashMap<>();

        // Step one: add all time values
        for (Map.Entry<String, String[]> meData : p_mData.entrySet()) {
            // if key starts with column index zero, then it's the time column
            if (meData.getKey().startsWith("0")) {
                // put time value in final map
                mFinalData.put(meData.getKey(), meData.getValue());
            }
        }

        // Step two: add all complete days
        if (p_lMoveColumns.size() > 0) { // if list is empty there are no complete days
            for (Map.Entry<String, String[]> meData : p_mData.entrySet()) {
                // when type value stands for complete day and row index is 1
                if (meData.getValue()[5].equals("1") && meData.getKey().substring(2).equals("1")) {
                    // get column index of the day
                    int iColumnIndex = Integer.valueOf(meData.getKey().substring(0, 1));

                    for (int i = 1; i <= 19; i = i + 2) {
                        // add values to all 10 hours
                        mFinalData.put(iColumnIndex + "_" + i, meData.getValue());
                    }
                }
            }
        }

        // Step three: add every left entries
        for (Map.Entry<String, String[]> meData : p_mData.entrySet()) {
            // iterate only over entries which don't have time values
            if (!meData.getKey().startsWith("0")) {
                int iColumnIndex = Integer.valueOf(meData.getKey().substring(0, 1));
                int iRowIndex = Integer.valueOf(meData.getKey().substring(2));

                // special week, at least one complete day entry
                if (p_lMoveColumns.size() > 0) {

                }
                // normal week
                else {
                    // get values of the actual hour
                    String[] sData = meData.getValue();

                    // half block or replacement lesson/omission, both types consist of 2 parts
                    if (meData.getValue()[5].equals("2") || meData.getValue()[5].equals("3")) {
                        // if odd hour value
                        if (Integer.valueOf(meData.getValue()[6]) % 2 != 0) {
                            // add 1 to actual hour for check if second part of this one exists
                            sData[6] = String.valueOf(Integer.valueOf(meData.getValue()[6]) + 1);

                            // if the original map contains the following entry and the final map
                            // doesn't contain the actual one
                            if (p_mData.containsValue(sData) &&
                                    !mFinalData.containsKey(meData.getKey())) {
                                // add actual and following hour to final map
                                mFinalData.put(meData.getKey(), meData.getValue());
                                mFinalData.put(iColumnIndex + "_" + (iRowIndex + 2), sData);
                            }
                            // if the final map doesn't contain the actual hour
                            else if (!mFinalData.containsKey(meData.getKey())) {
                                // add actual hour to final map
                                mFinalData.put(meData.getKey(), meData.getValue());
                            }
                        }
                        // if it's not an odd hour value it's even
                        else {
                            // subtract 1 from actual hour to check if first part of this one exists
                            sData[6] = String.valueOf(Integer.valueOf(meData.getValue()[6]) - 1);

                            // if the original map contains the previous entry and the final map
                            // doesn't contain the actual one
                            if (p_mData.containsValue(sData) &&
                                    !mFinalData.containsKey(meData.getKey())) {
                                // add previous and actual hour to final map
                                mFinalData.put(iColumnIndex + "_" + (iRowIndex - 2), sData);
                                mFinalData.put(meData.getKey(), meData.getValue());
                            }
                            // if the final map doesn't contain the actual hour
                            else if (!mFinalData.containsKey(meData.getKey())) {
                                // add actual hour to final map
                                mFinalData.put(meData.getKey(), meData.getValue());
                            }
                        }
                    }
                    // default entry, consists only of one part
                    else {
                        // add 1 to actual hour for creating the second part
                        sData[6] = String.valueOf(Integer.valueOf(meData.getValue()[6]) + 1);

                        // add first and second part of actual hour to final map
                        mFinalData.put(meData.getKey(), meData.getValue());
                        mFinalData.put(iColumnIndex + "_" + (iRowIndex + 2), sData);
                    }
                }
            }
        }

        // return the final processed map
        return mFinalData;
    }
}
