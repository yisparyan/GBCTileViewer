package com.isparyan.gbctileviewer;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;


/**
 * Created by YURIY on 9/26/2015.
 *
 * This file is responsible for logic and controlling how HexTextRegion should display
 *
 * It is made up of an HBox that contains a HexTextRegion and a Scrollbar
 */
public class HexEditorSkin extends SkinBase<HexEditor> implements Skin<HexEditor>
{
    /*Constants*/
    final static double PREF_WIDTH = 700;
    final static double PREF_HEIGHT = 500;

    /* The Nodes*/
    HBox hbox;
    HexTextRegion hexBox;
    ScrollBar scrollBar;

    long currentLine;
    boolean scrollbarListenerDisable;

    public HexEditorSkin(HexEditor control)
    {
        super(control);
        init();
        initGraphics();
        registerListeners();
        addEventHandlers();
    }
    private void moveDown()
    {
        moveAndDraw(1);
    }
    private void moveUp()
    {
        if(currentLine != 0)
            moveAndDraw(-1);
    }
    private void moveAndDraw(int numOfLinesToMove)
    {
        if(getSkinnable().isFileLoaded()) {
            //System.out.println("Current Line: " + currentLine);
            int ROWS_IN_HEXBOX = hexBox.getNumVisibleRows();

            if (currentLine + numOfLinesToMove >= 0) {
                ByteReadData byteData = getSkinnable().getData(HexTextRegion.BYTES_PER_LINE * ROWS_IN_HEXBOX, HexTextRegion.BYTES_PER_LINE * numOfLinesToMove);
                if(byteData != null) {
                    currentLine += numOfLinesToMove;
                    hexBox.drawHex(byteData.bytes, (int) currentLine, byteData.bytesRead);
                    scrollbarListenerDisable = true;
                    scrollBar.setValue(scrollBar.getValue() + numOfLinesToMove);
                    scrollbarListenerDisable = false;
                }

            }
            System.out.println("Current Line: " + currentLine);
        }
    }
    private void set(long jumpToLine)
    {
        System.out.println("set "+jumpToLine);
        int ROWS_IN_HEXBOX = hexBox.getNumVisibleRows();
        ByteReadData byteData = getSkinnable().getDataAtLoc(HexTextRegion.BYTES_PER_LINE * ROWS_IN_HEXBOX, HexTextRegion.BYTES_PER_LINE * jumpToLine);

        if(byteData != null) {
            currentLine = jumpToLine;
            hexBox.drawHex(byteData.bytes, (int)currentLine, byteData.bytesRead);

            scrollbarListenerDisable = true;
            scrollBar.setValue(jumpToLine);
            scrollbarListenerDisable = false;
        }

    }
    private void redraw()
    {

    }



    private void init()
    {
        getSkinnable().setMaxSize(650, Double.MAX_VALUE);   //Set to fill parent
        getSkinnable().setPrefSize(PREF_WIDTH, PREF_HEIGHT);
        scrollbarListenerDisable = false;
        currentLine = 0;
    }
    private void initGraphics()
    {
        hbox = new HBox();

        hexBox = new HexTextRegion();

        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);

        if(getSkinnable().isFileLoaded()) {
            scrollBar.setMax(((double) getSkinnable().getFileLength() / HexTextRegion.BYTES_PER_LINE) - 10);
            scrollBar.setVisibleAmount(hexBox.getNumVisibleRows());
        }
        else {
            scrollBar.setMax(0);
        }

        scrollBar.setUnitIncrement(1);
        scrollBar.setMinWidth(20);

        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().addAll(hexBox, scrollBar);
        getChildren().add(hbox);

        moveAndDraw(0);    //initial draw
    }
    private void setScrollBarSettings() //Called when new file is loaded
    {
        double max = (double) getSkinnable().getFileLength() / HexTextRegion.BYTES_PER_LINE;
       if( getSkinnable().getFileLength() % HexTextRegion.BYTES_PER_LINE == 0) max--;   //When leftover bytes don't draw onto next line
        scrollBar.setMax(max);
        scrollBar.setVisibleAmount(hexBox.getNumVisibleRows());
    }
    private void registerListeners()
    {
        HexEditor skinnable = getSkinnable();

        skinnable.widthProperty().addListener((observable, oldValue, newValue) -> resizedWidth(newValue.doubleValue()));
        skinnable.heightProperty().addListener((observable, oldValue, newValue) -> resizedHeight(newValue.doubleValue()));
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!scrollbarListenerDisable) {
                    set(newValue.longValue());
                    //System.out.println(newValue.intValue()-oldValue.intValue());
                }

            }
        });

        skinnable.fileLoadedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("BOOLEAN CHANGED: " + newValue);
                if(newValue == true) {
                    setScrollBarSettings();
                    set(0);
                }
            }
        });

    }
    private void resizedWidth(double width)
    {
        hexBox.setWidth(width - scrollBar.getWidth());
        moveAndDraw(0);
        System.out.println("New Width: "+ (width-scrollBar.getWidth()) );
    }
    private void resizedHeight(double height)
    {
        hexBox.setHeight(height);
        moveAndDraw(0);
        moveAndDraw(0); //You have to call it twice (I don't know why this happens)
        scrollBar.setVisibleAmount(hexBox.getNumVisibleRows());
        System.out.println("New Height: " + height);
    }
    private void addEventHandlers()
    {
        HexEditor skinnable = getSkinnable();

        skinnable.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP) {
                    moveUp();
                }
                else if(event.getCode() == KeyCode.DOWN) {
                    moveDown();
                }
            }
        });

        skinnable.addEventHandler(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                System.out.println(-event.getDeltaY()/event.getMultiplierY());
                int moveAmount = (int)(event.getDeltaY()/event.getMultiplierY());
                moveAndDraw(-moveAmount);
            }
        });



    }

}
