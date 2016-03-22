package com.isparyan.gbctileviewer;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

import java.util.Random;

/**
 * Created by YURIY on 10/28/2015.
 */
public class TileRendererRegion extends Region
{
    Canvas canvas;
    GraphicsContext gc;
    private double pixelSize;   //What are the pixel dimensions, of one tile pixel
    private int tilesWidth;     //How many tiles are we drawing on the width of the canvas
    private int tilesHeight;    //How many tiles are we drawing on the height of the canvas
    int[] pixels;

    private final static Paint color0 =  Paint.valueOf("000000");
    private final static Paint color1 =  Paint.valueOf("666666");
    private final static Paint color2 =  Paint.valueOf("AAAAAA");
    private final static Paint color3 =  Paint.valueOf("FFFFFF");

    final static int TILE_PIXELS_LENGTH = 8;               //pixel length in square tile
    final static int BYTES_PER_TILE = 16;

    public TileRendererRegion() { this(700, 600); }
    public TileRendererRegion(double width, double height)
    {
        super();

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Paint.valueOf("black"));
        calculatePixelSize();

        getChildren().add(canvas);

        drawPicture();
    }

    public void drawPicture()
    {
        clearCanvas();
        //int numRows = data.length/16;
        int numRows = 20;
        for(int i = 0; i < numRows; i++) {

            for(int j = 0; j < numRows; j++) {
                Random rand = new Random();
                int setColor = rand.nextInt(4);
                switch (setColor){
                    case 0:
                        gc.setFill(color0);
                        break;
                    case 1:
                        gc.setFill(color1);
                        break;
                    case 2:
                        gc.setFill(color2);
                        break;
                    case 3:
                        gc.setFill(color3);
                        break;
                }
                gc.fillRect(j*pixelSize, i*pixelSize, pixelSize, pixelSize);
            }
        }
    }
    public void drawPicture(ByteReadData byteReadData) {
        //TODO change the draw code to lower the amount of draw calls (This is horribly inefficient right now!)
        clearCanvas();
        int numTiles = byteReadData.bytesRead/HexTextRegion.BYTES_PER_LINE;

        System.out.println("Number of tiles to be printed: "+ numTiles);

        int currentRow = -1;                 //What row to draw our tile, incremented right away
        for(int i = 0; i < numTiles; i++) {
            if(i%tilesWidth == 0) {
                currentRow++;
            }

            for(int j = 0; j < HexTextRegion.BYTES_PER_LINE; j+=2) { //iterates through every two byte pairs which make a 8 pixel line


                for(int c = 0; c < 8; c++) {                        //draws 8 horizontal pixels in a tile
                    int hiBit = (byteReadData.bytes[i*HexTextRegion.BYTES_PER_LINE+j+1] >> (7-c)) & 1;
                    int loBit = (byteReadData.bytes[i*HexTextRegion.BYTES_PER_LINE+j] >> (7-c)) & 1;
                    int setColor = (hiBit << 1) | loBit;

                    switch (setColor){
                        case 0:
                            gc.setFill(color0);
                            break;
                        case 1:
                            gc.setFill(color1);
                            break;
                        case 2:
                            gc.setFill(color2);
                            break;
                        case 3:
                            gc.setFill(color3);
                            break;
                    }
                    gc.fillRect(8*pixelSize*(i%tilesWidth) + c*pixelSize, currentRow*8*pixelSize + j/2*pixelSize, pixelSize, pixelSize);

                }
            }


        }

    }

    private int[] bytesToPixelsColor(byte[] bytes)
    {



        return null;
    }

    private void clearCanvas()
    {
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }
    private void calculatePixelSize()
    {
        //TODO
        pixelSize = 4;
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        System.out.println("WIDTH: "+width);
        tilesWidth = (int) (width/ (pixelSize*TILE_PIXELS_LENGTH) );
        System.out.println("tilesWIdth: "+tilesWidth);

        tilesHeight = (int) (height/ (pixelSize*TILE_PIXELS_LENGTH));

        //tilesWidth = 16;
        //tilesHeight = 40;
        //pixels = new int[PIXELS_LENGTH_PER_TILE * tilesWidth * PIXELS_LENGTH_PER_TILE * tilesHeight];
    }
    public int getNumVisibleRows() { return tilesHeight;}
    public int getNumTilesInRow() { return tilesWidth; }


    public void changeWidth(double width)
    {
        canvas.setWidth(width);
        calculatePixelSize();
    }
    public void changeHeight(double height)
    {
        canvas.setHeight(height);
        calculatePixelSize();
    }
}
