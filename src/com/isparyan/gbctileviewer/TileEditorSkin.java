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
 * Created by YURIY on 2/8/2016.
 */
public class TileEditorSkin extends SkinBase<TileEditor> implements Skin<TileEditor>
{
    final static double PREF_WIDTH = 700;
    final static double PREF_HEIGHT = 500;

    private ByteReadData data;
    private TileRendererRegion tileRenderer;
    private ScrollBar scrollBar;
    private HBox hbox;
    private long currentLine;

    private boolean scrollbarListenerDisable;

    public TileEditorSkin(TileEditor control)
    {
        super(control);
        init();
        initGraphics();
        registerListeners();
        addEventHandlers();
    }
    private void init()
    {
        currentLine = 0;
        data = new ByteReadData();
        getSkinnable().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);   //Set to fill parent
        getSkinnable().setPrefSize(PREF_WIDTH, PREF_HEIGHT);
    }
    private void initGraphics()
    {
        hbox = new HBox();

        tileRenderer = new TileRendererRegion();
        getChildren().add(tileRenderer);

        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);

        if(getSkinnable().isFileLoaded()) {
            setScrollBarSettings();
        }
        else {
            scrollBar.setMax(0);
        }

        scrollBar.setUnitIncrement(1);
        scrollBar.setMinWidth(20);

        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().addAll(tileRenderer, scrollBar);
        getChildren().add(hbox);
    }
    private void registerListeners() {
        TileEditor skinnable = getSkinnable();
        HexEditor hexViewer = skinnable.getHexEditor();

        hexViewer.dataChangedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue == false) {
                    hexViewer.resetDataChanged();
                    //tileRenderer.drawPicture(hexViewer.getStoredData());
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
                    //moveAndDraw(0);
                }
            }
        });

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!scrollbarListenerDisable) {
                    set(newValue.longValue());
                    //moveAndDraw(newValue.intValue()-oldValue.intValue());
                    //System.out.println(newValue.intValue()-oldValue.intValue());
                }

            }
        });


        skinnable.widthProperty().addListener((observable, oldValue, newValue) -> resizedWidth(newValue.doubleValue()));
        skinnable.heightProperty().addListener((observable, oldValue, newValue) -> resizedHeight(newValue.doubleValue()));

    }



    private void resizedWidth(double width)
    {
        System.out.println("TileE width defined: "+width);
        tileRenderer.changeWidth(width-scrollBar.getWidth());
    }

    private void resizedHeight(double height)
    {
        tileRenderer.changeHeight(height);
    }

    private void addEventHandlers()
    {
        TileEditor skinnable = getSkinnable();

        skinnable.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP) {
                    //moveUp();
                    moveAndDraw(-1);
                }
                else if(event.getCode() == KeyCode.DOWN) {
                    //moveDown();
                    moveAndDraw(1);
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


    private void moveAndDraw(int numOfLinesToMove)
    {
        System.out.println("IM HERE");
        if(getSkinnable().isFileLoaded()) {
            int rows_in_tile_renderer= tileRenderer.getNumVisibleRows();
            int tiles_in_row = tileRenderer.getNumTilesInRow();

            if (currentLine + numOfLinesToMove >= 0) {
                ByteReadData byteData = getSkinnable().getData(tiles_in_row * rows_in_tile_renderer * TileRendererRegion.BYTES_PER_TILE,
                        TileRendererRegion.BYTES_PER_TILE * rows_in_tile_renderer * numOfLinesToMove);

                if(byteData != null) {
                    currentLine += numOfLinesToMove;
                    tileRenderer.drawPicture(byteData);
                    System.out.println("BYTES READ: "+byteData.bytesRead);

                    scrollbarListenerDisable = true;
                    scrollBar.setValue(currentLine);
                    scrollbarListenerDisable = false;
                }

            }
            System.out.println("Current Line: " + currentLine);
        }
    }


    private void set(long jumpToLine)
    {
        System.out.println("TileEditor set "+jumpToLine);
        int tiles_in_row = tileRenderer.getNumTilesInRow();
        int rows_in_tile_renderer= tileRenderer.getNumVisibleRows();

        ByteReadData byteData = getSkinnable().getDataAtLoc(tiles_in_row * rows_in_tile_renderer * TileRendererRegion.BYTES_PER_TILE,
                TileRendererRegion.BYTES_PER_TILE * rows_in_tile_renderer * jumpToLine);

        if(byteData != null) {
            currentLine = jumpToLine;
            tileRenderer.drawPicture(byteData);

            scrollbarListenerDisable = true;
            scrollBar.setValue(jumpToLine);
            scrollbarListenerDisable = false;
        }

    }


    private void setScrollBarSettings() //Called when new file is loaded
    {
        int rows_in_tile_renderer= tileRenderer.getNumVisibleRows();
        int tiles_in_row = tileRenderer.getNumTilesInRow();

        double max = (double) getSkinnable().getFileLength() / (TileRendererRegion.BYTES_PER_TILE * rows_in_tile_renderer);
        //if( getSkinnable().getFileLength() % HexTextRegion.BYTES_PER_LINE == 0) max--;   //When leftover bytes don't draw onto next line
        scrollBar.setMax(max);
        scrollBar.setVisibleAmount(tileRenderer.getNumVisibleRows());
    }

}
