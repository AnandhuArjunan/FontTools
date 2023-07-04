package com.anandhuarjunan.fonttools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.zeroturnaround.zip.*;

import com.weihq.opentype4j.GlyphData;
import com.weihq.opentype4j.render.ImageFormat;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
public class MainController implements Initializable {
	
	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	static {
	   LOGGER.setUseParentHandlers(false);
	 }

    @FXML
    private TextField fontLoc;

    @FXML
    private Button fontBrowse;

    @FXML
    private RadioButton rdNumbers;

    @FXML
    private RadioButton rdCapital;

    @FXML
    private RadioButton rdSmall;

    @FXML
    private RadioButton rdTypeUrOwn;

    @FXML
    private TextField urOwnTextFld;

    @FXML
    private RadioButton ouputSvg;

    @FXML
    private RadioButton outputPng;

    @FXML
    private TextField imgWidth;

    @FXML
    private TextField imgHeight;

    @FXML
    private ColorPicker fontColor;

    @FXML
    private TextField outputFolderLoc;

    @FXML
    private RadioButton expAsSeparate;

    @FXML
    private RadioButton expAsZip;

    @FXML
    private HBox startAction;

    @FXML
    private TextArea logTextArea;

    @FXML
    private FlowPane imagePreview;
    
    @FXML
    private Button outputFileBrowse;
    
    private  File inputfile = null;
    
    private  File outputDirectory = null;
    
    private FontHelper fontHelper = new FontHelper();


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initiateLogger();
		chooseFileDirectoryAction();
		inputFilterAction();
		chooseOutputFolderAction();
		outputFormatAction();
		startButtonAction();
		exportTypeAction();
	}

	private void exportTypeAction() {
		ToggleGroup group = new ToggleGroup();
		expAsSeparate.setToggleGroup(group);
		expAsZip.setToggleGroup(group);
		expAsSeparate.setSelected(true);
	}

	private void initiateLogger() {
		LogHandler handler = new LogHandler(logTextArea);
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
	}

	private void startButtonAction() {
		
		startAction.setOnMouseClicked(ev->{
			if(Objects.isNull(inputfile) || Objects.isNull(outputDirectory) || urOwnTextFld.getText().isEmpty()) {
				Alert a = new Alert(AlertType.NONE);
				a.setTitle("Info");
				a.setContentText("Please check if all the inputs are provided");
				a.setAlertType(AlertType.INFORMATION);
				a.show();
			}else {
				try {
			        LOGGER.info("Loading Font ......");
					List<GlyphData>  glyphDatas = fontHelper.getCharactersAsGlyph(inputfile, urOwnTextFld.getText());
			        LOGGER.info("Successfully Loaded Font ......");
			        imagePreview.getChildren().clear();
			        
			        List<ZipEntrySource> files = new ArrayList<>();
			        glyphDatas.forEach(g->{
			        	byte[] imgBytes;
						try {
							double wdth = Double.parseDouble(imgWidth.getText());
							double height = Double.parseDouble(imgHeight.getText());
							
							imgBytes =  g.getPath(Util.toHexString(fontColor.getValue()),wdth,height).toImageBytes(ImageFormat.PNG);
							
							if(expAsSeparate.isSelected()) {
								if(ouputSvg.isSelected()) {
									g.getPath(Util.toHexString(fontColor.getValue()),wdth,height).toSVG(new File(outputDirectory, g.getName()+".svg").getAbsolutePath());
								}else if(outputPng.isSelected()) {
									g.getPath(Util.toHexString(fontColor.getValue()),wdth,height).toImage(new File(outputDirectory, g.getName()+".png"), ImageFormat.PNG);
								}
								
							}else if(expAsZip.isSelected()) {
								if(ouputSvg.isSelected()) {
									files.add(new ByteSource(g.getName()+".svg", g.getPath(Util.toHexString(fontColor.getValue()),wdth,height).toSVG().getBytes()));

								}else if(outputPng.isSelected()) {
									files.add(new ByteSource(g.getName()+".png", imgBytes));

								}
								
							}
							

							
							//Common preview.
				        	imagePreview.getChildren().add(new ImageView(new Image(new ByteArrayInputStream(imgBytes))));
						} catch (IOException e) {
					        LOGGER.severe(e.getMessage());

						}
			        });
			        File zip = new File(outputDirectory, inputfile.getName()+".zip");
			        ZipUtil.createEmpty(zip);
			        ZipEntrySource[]arr = new ZipEntrySource [files.size()];

			        //Converting List to Array
			        files.toArray(arr);
					  ZipUtil.addEntries(zip, arr);

			        


				}catch(Exception e) {
			        LOGGER.severe(e.getMessage());

				}
			}
		});
	}

	private void outputFormatAction() {
		ToggleGroup group = new ToggleGroup();
		ouputSvg.setToggleGroup(group);
		outputPng.setToggleGroup(group);
		outputPng.setSelected(true);
	}

	private void inputFilterAction() {
		ToggleGroup group = new ToggleGroup();
		rdNumbers.setToggleGroup(group);
		rdCapital.setToggleGroup(group);
		rdSmall.setToggleGroup(group);
		rdTypeUrOwn.setToggleGroup(group);
		rdNumbers.setSelected(true);
		urOwnTextFld.setText("0123456789");
		rdTypeUrOwn.selectedProperty().addListener((a,b,c)->{
			urOwnTextFld.setText("");
			urOwnTextFld.setEditable(c);
		});
		rdNumbers.selectedProperty().addListener((a,b,c)->
			urOwnTextFld.setText("0123456789")
			);
		rdCapital.selectedProperty().addListener((a,b,c)->
			urOwnTextFld.setText("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
			);
		rdSmall.selectedProperty().addListener((a,b,c)->
			urOwnTextFld.setText("abcdefghijklmnopqrstuvwxyz")
			);
	}

	private void chooseFileDirectoryAction() {
        FileChooser fileChooser = new FileChooser();
        fontBrowse.setOnAction(ev->{
        	 inputfile = fileChooser.showOpenDialog(fontBrowse.getScene().getWindow());
    		 if (inputfile != null) {
    	            fontLoc.setText(inputfile.getName());

             }
        });
		
		
	}
	
	private void chooseOutputFolderAction() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("/"));

        outputFileBrowse.setOnAction(e -> {
            outputDirectory = directoryChooser.showDialog(outputFileBrowse.getScene().getWindow());
            outputFolderLoc.setText(outputDirectory.getName());
        });
	}

}
