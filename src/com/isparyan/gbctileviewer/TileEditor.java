package com.isparyan.gbctileviewer;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Created by YURIY on 2/8/2016.
 *
 * This class will interface with Hexeditor in order to get the file byte info. It will then pass this on to its skin
 * class which will handle the logic for displaying information. Which will also contain a TileRendererRegion for
 * rendering the tiles to the screen.
 *
 */
public class TileEditor extends Control
{
    HexEditor hexEditor; //Our reference to the hexEditor

    public TileEditor(HexEditor hexEditor)
    {
        this.hexEditor = hexEditor;
    }

    public HexEditor getHexEditor(){return hexEditor;}


    @Override
    protected Skin createDefaultSkin()
    {
        return new TileEditorSkin(this);
    }
}
