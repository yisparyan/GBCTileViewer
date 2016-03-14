package com.isparyan.gbctileviewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
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
    private long currentPos;
    private int bytesRead;

    public TileEditor(HexEditor hexViewer)
    {
        this.hexViewer = hexViewer;
        fileLoadedProperty = new SimpleBooleanProperty(false);
    }

    public HexEditor getHexEditor(){
        return hexViewer;
    }


    //offset is num of bytes to offset the reader from current pos
    public ByteReadData getData(int numBytes, int bytesOffset)
    {
        if(reader == null) return null;

        try{
            byte[] data = new byte[numBytes];
            currentPos = reader.getFilePointer();

            reader.seek(currentPos+bytesOffset);                    //Jump to loc to read from

            bytesRead = reader.read(data);
            if(bytesRead > 0)                                       //Read data
            {
                reader.seek(currentPos+bytesOffset);                //Reset location back

                currentPos = reader.getFilePointer(); System.out.println("Current line Pos: "+currentPos/16); //DEBUGGING PURPOSES
                return new ByteReadData(data, bytesRead, currentPos);
            }
            else { reader.seek(currentPos); }                       //Reset to original loc
            System.out.println("Not reading bytes");
        }
        catch (IOException e){
            System.out.println("Error reading data from file.");
            e.printStackTrace();
        }

        return null;
    }
    public ByteReadData getDataAtLoc(int numBytes, long pos)
    {
        if(reader == null) return null;

        try{
            byte[] data = new byte[numBytes];
            reader.seek(pos);
            bytesRead = reader.read(data);
            if(bytesRead > 0)
            {
                reader.seek(pos);
                return new ByteReadData(data, bytesRead, pos);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
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
    public ReadOnlyBooleanProperty fileLoadedProperty()
    {
        return fileLoadedProperty;
    }
}
