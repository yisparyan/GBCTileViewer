package com.isparyan.gbctileviewer;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.Arrays;

/**
 * Created by YURIY on 10/3/2015.
 *
 * This class should just be responsible for drawing the hex
 * while the hexeditor will be responsible for keeping the file
 * and telling this class what to draw, as well as implementing controls
 */
public class HexTextRegion extends Region
{
    Canvas canvas;
    GraphicsContext gc;
    int fontSize;
    int numRows;

    final static int FONT_SPACING = 4;
    public final static int BYTES_PER_LINE = 16;

    public HexTextRegion()
    {
        this(700,600, 18);
    }
    public HexTextRegion(byte[] dataToDisplay)
    {
        this(700, 500, 18);
        drawHex(dataToDisplay);
    }
    public HexTextRegion(double width, double height, int fontsize)
    {
        super();
        fontSize = fontsize;

        canvas = new Canvas(width, height);

        gc = canvas.getGraphicsContext2D();
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Paint.valueOf("black"));
        gc.setFont(Font.font("monospace", fontSize));

        getChildren().add(canvas);
        //drawJunk();
    }
    public void setWidth(double width)
    {
        canvas.setWidth(width);
    }
    public void setHeight(double height)
    {
        canvas.setHeight(height);
    }
    public int getNumVisibleRows()
    {
        calcNumRows();
        return numRows;
    }

    private void calcNumRows()
    {
        numRows = (int)canvas.getHeight()/(fontSize+FONT_SPACING);
    }
    private void drawJunk()
    {
        int rows = (int)canvas.getHeight()/(fontSize + FONT_SPACING);
        for(int i = 0; i < rows; i++) {
            gc.fillText("Line " + i + " A4 DE F7 7F 00 C3 08", FONT_SPACING, (fontSize + FONT_SPACING)*i);
        }
        gc.fillText("1234567890123456789012345678901234512345678901234567890125", FONT_SPACING, (fontSize + FONT_SPACING) * rows);
    }

    //This function assumes that the length of byte[] hex is 16 * numRows it supports
    public void drawHex(byte[] hex, int firstLineNumber)
    {
        clearCanvas();
        calcNumRows();
        gc.fillText("Address:0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F", FONT_SPACING, 0);
        for(int i = 0; i < numRows; i++) {
            String currentLine = Integer.toHexString((firstLineNumber+i)*16) + ":\t" + bytesToHex(Arrays.copyOfRange(hex, i * 16, 16 + i * 16));
            gc.fillText(currentLine, FONT_SPACING, (fontSize + FONT_SPACING)*(i+1));
        }
    }
    public void drawHex(byte[] hex) { drawHex(hex, 0); }
    private void clearCanvas()
    {
        gc.setFill(Paint.valueOf("white"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Paint.valueOf("DDDDDD"));
        gc.fillRect(0, 0, canvas.getWidth(), fontSize + FONT_SPACING);

        gc.setFill(Paint.valueOf("EEEEEE"));
        gc.fillRect(0, fontSize+FONT_SPACING, fontSize*5, canvas.getHeight());

        gc.setFill(Paint.valueOf("black"));
    }

    //http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    /*
    hex = hexStringToByteArray(
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "3b169f2846904276d7db14d26a5d8f0c" +
                "302872e5d594928c9ab4fdac85881e55" +
                "54bfcebeffbbc12a1516bc630f1fdcdf" +
                "5e5dfccdfee359037e32a54601e6e480" +
                "81a97f959836767b489b7293efc26219" +
                "ed8723f5");


    //http://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    */
}
