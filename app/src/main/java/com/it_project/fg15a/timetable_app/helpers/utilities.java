package com.it_project.fg15a.timetable_app.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class utilities {

    // function to check internet access
    // Origin:
    // https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
    public boolean isOnline(Context context){
        // get connectivity service
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // check if there is a active network
        return cm.getActiveNetworkInfo() != null &&
                                            cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * This method creates a new file for the specified object to store it internally.
     * @param p_cContext The actual context
     * @param p_sClass The chosen class
     * @param p_sWeek The chosen week
     * @param p_oContent The object that should be stored
     * @param p_sObjectType The type of the object that should be stored, e.g. "map", "html", etc.
     */
    public void putObjectToInternalStorage (Context p_cContext, String p_sClass, String p_sWeek, Object p_oContent,
                                            String p_sObjectType) {
        // create new file to store object in internal storage
        File fiThis = new File(p_cContext.getFilesDir(), "offline_" + p_sObjectType + "_" + p_sClass + "_" + p_sWeek);

        // delete the file if it already exists
        if (fiThis.exists()) fiThis.delete();

        // declare and initialize stream
        ObjectOutputStream oosThis = null;

        try {
            // tell stream where to write the content
            oosThis = new ObjectOutputStream(new FileOutputStream(fiThis));
            // write the given object into the file
            oosThis.writeObject(p_oContent);
            // flush the stream
            oosThis.flush();
        } catch (IOException e) {
            // throw catched exception
            e.printStackTrace();
        } finally {
            // when the stream is not null
            if (oosThis != null) {
                try {
                    // close the stream
                    oosThis.close();
                } catch (IOException e) {
                    // throw catched exception
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method reads the file with the specified parameters from the internal storage and returns it as object.
     * @param p_cContext The actual context
     * @param p_sClass The chosen class
     * @param p_sWeek The chosen week
     * @param p_sObjectType The type of the object that should be stored, e.g. "map", "html", etc.
     * @return The object if the file exists, null if the file doesn't exist
     */
    public Object getObjectFromInternalStorage (Context p_cContext, String p_sClass, String p_sWeek,
                                                String p_sObjectType) {
        File fiThis = new File(p_cContext.getFilesDir(), "offline_" + p_sObjectType + "_" + p_sClass + "_" + p_sWeek);

        Object oThis = new Object();

        ObjectInputStream oisThis = null;

        try {
            oisThis = new ObjectInputStream(new FileInputStream(fiThis));
            oThis = oisThis.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oisThis != null) {
                try {
                    oisThis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                oThis = null;
            }
        }

        return oThis;
    }

    /**
     * This method deletes all unnecessary files from the internal storage based on the lowest selectable week.
     * @param p_cContext The actual context, used to retrieve the file list
     * @param p_iBorderWeek Lowest selectable week
     */
    public void deleteObsoleteObjectFilesFromInternalStorage (Context p_cContext, int p_iBorderWeek) {
        // iterate through the list of file names
        for (String sFileName : p_cContext.fileList()) {
            // get the week of the file name
            int iFileWeek = Integer.valueOf(sFileName.substring(sFileName.lastIndexOf("_") + 1));

            // if the week from the file name is lower than the lowest selectable week
            if (iFileWeek < p_iBorderWeek) {
                // delete the file, as it is not selectable anymore
                p_cContext.deleteFile(sFileName);
            }
        }
    }
}
