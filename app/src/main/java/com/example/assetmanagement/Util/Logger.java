package com.example.assetmanagement.Util;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    public static void writeLog(Context context, String logMessage) {
        try {
            // Get app-specific external storage directory
            File logDir = new File(context.getExternalFilesDir(null), "logs");
            if (!logDir.exists()) logDir.mkdirs(); // Create directory if not exists

            File logFile = new File(logDir, "printer_logs.txt");

            // Append log to file
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logMessage).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
