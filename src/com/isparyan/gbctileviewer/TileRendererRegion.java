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
    double pixelSize;   //What are the pixel dimensions, of one tile pixel
    int tilesWidth;     //How many tiles are we drawing on the width of the canvas
    int tilesHeight;    //How many tiles are we drawing on the height of the canvas
    int[] pixels;

    private final static Paint color0 =  Paint.valueOf("000000");
    private final static Paint color1 =  Paint.valueOf("666666");
    private final static Paint color2 =  Paint.valueOf("AAAAAA");
    private final static Paint color3 =  Paint.valueOf("FFFFFF");

    private final static int PIXELS_LENGTH_PER_TILE = 8;               //pixel length in square tile

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
                int next = rand.nextInt(4);
                switch (next){
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

    private void bytesToPixels()
    {

    }

    private void clearCanvas()
    {
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        //gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
    }
    private void calculatePixelSize()
    {
        //TODO
        pixelSize = 16;

        pixels = new int[PIXELS_LENGTH_PER_TILE * tilesWidth * PIXELS_LENGTH_PER_TILE * tilesHeight];
    }

}
