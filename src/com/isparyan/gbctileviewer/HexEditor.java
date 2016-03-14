package com.isparyan.gbctileviewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by YURIY on 9/23/2015.
 *
 * The purpose of this class is to set up a link with the file using a
 * RandomAccessFile. This class is also responsible for keeping the position
 * we are reading from within the file. It will also return the data to the
 * skin class which is responsible for displaying it.
 *
 */
public class HexEditor extends Control
{
    private RandomAccessFile reader;
    private long currentPos;
    private SimpleBooleanProperty fileLoadedProperty;
    private byte[] data;
    private int bytesRead;

    private SimpleBooleanProperty dataChangedProperty;  //Used to interface with TileEditor so that it can be notified of changes

    public HexEditor()
    {
        this(null);
    }
    public HexEditor(File file)
    {
        data = null;
        fileLoadedProperty = new SimpleBooleanProperty(false);
        dataChangedProperty = new SimpleBooleanProperty(false);
        getStyleClass().add("hex-editor");
        currentPos = 0;
        if(file != null) {
            setFile(file);
        }
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

    //offset is num of bytes to offset the reader from current pos
    public ByteReadData getData(int numBytes, int bytesOffset)
    {
        if(reader == null) return null;

        try{
            //TODO: Change data to be fixed-size and rewrite instead of creating new and leaving stuff for GC
            data = new byte[numBytes];
            currentPos = reader.getFilePointer();

            reader.seek(currentPos+bytesOffset);                    //Jump to loc to read from
            //TODO: Store the number of bytes read for scrolling issue //TODO:Check if correct!
            bytesRead = reader.read(data);
            if(bytesRead > 0)                                       //Read data
            {
                reader.seek(currentPos+bytesOffset);                //Reset location back
                dataChangedProperty.set(true);                      //Report to TileEditor data changed

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
    public ByteReadData getStoredData(){return new ByteReadData(data, bytesRead, currentPos);}

    public ReadOnlyBooleanProperty dataChangedProperty() {
        return dataChangedProperty;
    }
    public void resetDataChanged() {
        dataChangedProperty.set(false);
    }

    public boolean isFileLoaded()
    {
        return fileLoadedProperty.get();
    }
    public ReadOnlyBooleanProperty fileLoadedProperty()
    {
        return fileLoadedProperty;
    }
    @Override
    protected Skin createDefaultSkin()
    {
        return new HexEditorSkin(this);
    }
    @Override
    public String getUserAgentStylesheet()
    {
        return getClass().getResource("hexeditor.css").toExternalForm();
    }


}
