package com.isparyan.gbctileviewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

/**
 * Created by YURIY on 2/8/2016.
 */
public class TileEditorSkin extends SkinBase<TileEditor> implements Skin<TileEditor>
{
    final static double PREF_WIDTH = 700;
    final static double PREF_HEIGHT = 500;

    private ByteReadData data;

    public TileEditorSkin(TileEditor control)
    {
        super(control);
        init();
        initGraphics();

    }
    private void init()
    {
        data = new ByteReadData();
        getSkinnable().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);   //Set to fill parent
        getSkinnable().setPrefSize(PREF_WIDTH, PREF_HEIGHT);
    }
    private void initGraphics()
    {
        TileRendererRegion tileRenderer = new TileRendererRegion();
        getChildren().add(tileRenderer);

    }
    private void registerListeners() {
        HexEditor hexEditor = getSkinnable().getHexEditor();
        hexEditor.dataChangedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue == false) {
                    hexEditor.resetDataChanged();
                    data = hexEditor.getStoredData();
                }
            }
        });


    }





}
