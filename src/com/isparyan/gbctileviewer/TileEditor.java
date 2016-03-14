package com.isparyan.gbctileviewer;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
    HexEditor hexViewer; //Our reference to the hexEditor
    private SimpleBooleanProperty fileLoadedProperty;
    private RandomAccessFile reader;

    public TileEditor(HexEditor hexViewer)
    {
        this.hexViewer = hexViewer;
        fileLoadedProperty = new SimpleBooleanProperty(false);
    }

    public HexEditor getHexEditor(){
        return hexViewer;
    }


    public boolean setFile(File file)
    {
        fileLoadedProperty.set(false);
        try{
            if(reader != null) reader.close();
            reader = new RandomAccessFile(file, "r");
            fileLoadedProperty.set(true);
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    public long getFileLength()
    {
        try {
            return reader.length();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    protected Skin createDefaultSkin()
    {
        return new TileEditorSkin(this);
    }

    public boolean isFileLoaded()
    {
        return fileLoadedProperty.get();
    }
}
