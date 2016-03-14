package com.isparyan.gbctileviewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

/**
 * Created by YURIY on 2/8/2016.
 */
public class TileEditorSkin extends SkinBase<TileEditor> implements Skin<TileEditor>
{
    final static double PREF_WIDTH = 700;
    final static double PREF_HEIGHT = 500;

    private ByteReadData data;
    TileRendererRegion tileRenderer;
    ScrollBar scrollBar;
    HBox hbox;

    public TileEditorSkin(TileEditor control)
    {
        super(control);
        init();
        initGraphics();
        registerListeners();
    }
    private void init()
    {
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
            scrollBar.setMax(((double) getSkinnable().getFileLength() / HexTextRegion.BYTES_PER_LINE) - 10);
            scrollBar.setVisibleAmount(tileRenderer.getNumVisibleRows());
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
                    tileRenderer.drawPicture(hexViewer.getStoredData());
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





}
