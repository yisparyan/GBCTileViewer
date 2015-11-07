package com.isparyan.gbctileviewer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {


    private BorderPane rootMain;

    private HexEditor hexViewer;
    private Label bottomInfoLabel;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        rootMain = new BorderPane();

        ////////////////////
        /////* Center */////
        SplitPane splitPane = new SplitPane();

        String home = System.getProperty("user.home");
        System.out.println(home);
        //Add Hex Viewer
        //hexViewer = new HexEditor(new File(home+"\\Desktop\\mario.gbc"));
        hexViewer = new HexEditor();

        splitPane.getItems().add(hexViewer);
        splitPane.setDividerPosition(0, 1);
        //Add Empty Stuff
        Region nothing = new Region();
        splitPane.getItems().add(nothing);
        /////* End Center */////
        ////////////////////////

        ////////////////////
        /////* Bottom */////
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(4));
        bottomBar.setAlignment(Pos.CENTER);
        //Add info text
        bottomInfoLabel = new Label("No file opened.");
        Region emptySpace = new Region();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(100);
        ProgressIndicator progressIndicator = new ProgressIndicator();

        bottomBar.getChildren().addAll(bottomInfoLabel, emptySpace, progressBar);
        bottomBar.setHgrow(emptySpace, Priority.ALWAYS);
        /////* End Bottom */////
        ////////////////////////

        /////////////////
        /////* Top */////
        VBox topVBox = new VBox();

        /* File Menu */
        final Menu menuFile = new Menu("File");

        final MenuItem menuItemFileOpen = new MenuItem("Open");
        menuItemFileOpen.setOnAction(event -> fileMenuOpen());

        final MenuItem menuItemFileSave = new MenuItem("Save");

        final MenuItem menuItemFileSaveAs = new MenuItem("Save as...");

        final MenuItem menuItemFileClose = new MenuItem("Close Application");
        menuItemFileClose.setOnAction(event -> fileMenuExitApplication());

        menuFile.getItems().addAll(menuItemFileOpen, menuItemFileSave, menuItemFileSaveAs, menuItemFileClose);
        /* End File Menu */

        /* Options Menu */
        final Menu menuOptions = new Menu("Options");
        final Menu menuOptionsLineDisplay= new Menu("Line Display");
        final MenuItem menuItemBytes8 = new MenuItem("8 Bytes");
        final MenuItem menuItemBytes16 = new MenuItem("16 Bytes");
        final MenuItem menuItemBytes32 = new MenuItem("32 Bytes");
        menuOptionsLineDisplay.getItems().addAll(menuItemBytes8,menuItemBytes16,menuItemBytes32);

        final Menu menuOptionsFontSize = new Menu("Font Size");
        final MenuItem menuItemFont10 = new MenuItem("10");
        final MenuItem menuItemFont12 = new MenuItem("12");
        final MenuItem menuItemFont14 = new MenuItem("14");
        final MenuItem menuItemFont16 = new MenuItem("16");
        final MenuItem menuItemFont18 = new MenuItem("18");
        menuOptionsFontSize.getItems().addAll(menuItemFont10,menuItemFont12,menuItemFont14,menuItemFont16,menuItemFont18);

        menuOptions.getItems().addAll(menuOptionsLineDisplay, menuOptionsFontSize);
        /* End Options Menu */

        /* Help Menu */
        final Menu menuHelp = new Menu("Help");
        final MenuItem menuItemHelpAbout = new MenuItem("About");
        menuHelp.getItems().addAll(menuItemHelpAbout);
        /* End Help Menu */

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile, menuOptions, menuHelp);
        menuBar.setUseSystemMenuBar(true);

        Label topInfo = new Label("Top Info text bar with extra information that should go here. Not sure what to put here tbh.");
        topVBox.getChildren().addAll(menuBar, topInfo);
        /////* End Top *//////
        //////////////////////

        /////* Set Stage */////
        rootMain.setTop(topVBox);
        rootMain.setCenter(splitPane);
        rootMain.setBottom(bottomBar);

        //Set stage and show
        primaryStage.setTitle("Hex Viewer Test Application");

        primaryStage.getIcons().add(new Image("gbc.png"));
        primaryStage.setScene(new Scene(rootMain, 1100, 678));
        primaryStage.show();

    }
    private void fileMenuOpen()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose GBA rom file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GBC", "*.gbc")
        );
        File romFile = fileChooser.showOpenDialog(rootMain.getScene().getWindow());
        if(romFile != null && hexViewer.setFile(romFile)) {
            bottomInfoLabel.setText("Opened file: " + romFile.getAbsolutePath() + "\t File size: " +hexViewer.getFileLength() + " bytes.");
        }
        else {
            bottomInfoLabel.setText("Error: File not opened.");
        }

    }
    private void fileMenuExitApplication()
    {
        bottomInfoLabel.setText("Closing...");
        Stage stage = (Stage) rootMain.getScene().getWindow();
        stage.close();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
