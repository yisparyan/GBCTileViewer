package com.isparyan.gbctileviewer;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Controller
{
    private File romFile = null;

    @FXML
    private Parent root;

    @FXML
    private Label bottomLabel;

    @FXML
    private TextArea hexTextArea;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;

    private Service<Void> loadThread;

    public void fileMenuOpenFile()
    {
        System.out.println("Open File");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose GBA rom file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GBC", "*.gbc")
        );

        romFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if(romFile != null) {
            long romFileBytesLength = romFile.length();
            String fileaddr = romFile.getAbsolutePath();
            bottomLabel.setText("File Path: " + fileaddr + " \t File size: " + romFileBytesLength + " bytes");

            //hexTextArea config
            hexTextArea.setWrapText(true);
            hexTextArea.setFont(Font.font("monospace", 18));

            //Start a new thread for loading file
            loadThread = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            //Open file input
                            try {
                                String output = "";
                                FileInputStream romReader = new FileInputStream(romFile);
                                //hexTextArea.clear();
                                int iterations = 0;
                                byte[] b = new byte[10000];
                                int test;
                                while( (test = romReader.read(b)) >= 0) {
                                    System.out.println(test);

                                    /*
                                    for(int i = 0; i< b.length; i++) {
                                        //if(b[i] < 16) output += "0";
                                        output += b[i];
                                        output += " ";
                                    }
                                    */
                                    output += bytesToHex(b);
                                    /*
                                    if(b < 16) output+="0";         //temporary fix for when bytes are less than 16
                                    output += Integer.toHexString(b);
                                    i++;
                                    updateProgress(i, romFileBytesLength);
                                    if(i % 16 == 0) {
                                        output += "\n";
                                    }
                                    else {
                                        output += " ";
                                    }
                                    */
                                    iterations++;
                                    updateMessage(output);
                                    updateProgress(iterations, romFileBytesLength/b.length);

                                }
                                romReader.close();
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                }
            };

            loadThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println("Sucess");
                    hexTextArea.setText(loadThread.getMessage());

                    progressBar.progressProperty().unbind();
                    progressIndicator.progressProperty().unbind();
                }
            });

            progressBar.progressProperty().bind(loadThread.progressProperty());
            progressIndicator.progressProperty().bind((loadThread.progressProperty()));


            loadThread.start();
        }
        else {
            bottomLabel.setText("File not opened.");
        }
    }

    public void fileMenuClose()
    {
        bottomLabel.setText("Closing...");
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void helpMenuAbout() throws IOException
    {
        Stage stage = new Stage();
        Parent newRoot = FXMLLoader.load(getClass().getResource("about.fxml"));

        stage.setScene(new Scene(newRoot));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(root.getScene().getWindow());
        stage.setResizable(false);
        stage.showAndWait();
    }

    //http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
