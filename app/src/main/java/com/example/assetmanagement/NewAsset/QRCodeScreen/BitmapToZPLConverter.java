package com.example.assetmanagement.NewAsset.QRCodeScreen;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import java.io.IOException;

public class BitmapToZPLConverter {

    public static String convertBitmapToZPL(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        StringBuilder zplData = new StringBuilder("^XA\n");

        for (int y = 0; y < height; y += 8) {
            StringBuilder rowData = new StringBuilder("^GFA," + (width / 8) + ",");
            for (int x = 0; x < width; x++) {
                int byteValue = 0;
                for (int bit = 0; bit < 8; bit++) {
                    if ((y + bit) < height) {
                        int pixel = bitmap.getPixel(x, y + bit);
                        if (Color.red(pixel) < 128) {
                            byteValue |= (1 << (7 - bit));
                        }
                    }
                }
                rowData.append(String.format("%02X", byteValue));
            }
            zplData.append(rowData).append("\n");
        }
        zplData.append("^XZ");
        return zplData.toString();
    }

    public static void sendBitmapToPrinter(ZebraPrinter printer, Bitmap bitmap) {
        if (printer == null) return;
        try {
            String zplCommand = convertBitmapToZPL(bitmap);
            printer.sendCommand(zplCommand);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }
}

