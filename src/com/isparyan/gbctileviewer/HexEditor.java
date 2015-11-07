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
    private long pos;
    private SimpleBooleanProperty fileLoadedProperty;
    private byte[] data;

    public HexEditor()
    {
        this(null);
    }
    public HexEditor(File file)
    {
        data = null;
        fileLoadedProperty = new SimpleBooleanProperty(false);
        getStyleClass().add("hex-editor");
        pos = 0;
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
    public byte[] getData(int numBytes, int bytesOffset)
    {
        if(reader == null) return null;

        try{
            byte[] bytesRead = new byte[numBytes];
            long currentPos = reader.getFilePointer();
            System.out.println("Current line Pos: "+currentPos/16);
            reader.seek(currentPos+bytesOffset);
            if(reader.read(bytesRead) > 15)
            {
                reader.seek(currentPos+bytesOffset);
                return bytesRead;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
    public byte[] getDataAtLoc(int numBytes, long pos)
    {
        if(reader == null) return null;

        try{
            byte[] bytesRead = new byte[numBytes];
            reader.seek(pos);
            if(reader.read(bytesRead) > 15)
            {
                reader.seek(pos);
                return bytesRead;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
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
