package com.anandhuarjunan.fonttools.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.zeroturnaround.zip.*;

import com.anandhuarjunan.fonttools.helper.FontHelper;
import com.anandhuarjunan.fonttools.helper.LogHandler;
import com.anandhuarjunan.fonttools.utils.JFXUtil;
import com.anandhuarjunan.fonttools.utils.RuntimeUtil;
import com.weihq.opentype4j.GlyphData;
import com.weihq.opentype4j.render.ImageFormat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.util.*;

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
    
    @FXML
    private ProgressBar ramProgressBar;
    
    @FXML
    private HBox showPreviewBtn;
    
    @FXML
    private Text memAvlStatus;
    
    @FXML
    private HBox fontPreviePgrsPane;

    @FXML
    private ProgressIndicator fntPrwProgress;
    
    private  File inputfile = null;
    
    private  File outputDirectory = null;
    
    private FontHelper fontHelper = new FontHelper();
    
    private Timer ramTimer = new Timer();
    
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    private List<GlyphData>  glyphDatas = null;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		loadRAMStatus();
		initiateLogger();
		chooseFileDirectoryAction();
		inputFilterAction();
		chooseOutputFolderAction();
		outputFormatAction();
		startButtonAction();
		exportTypeAction();
		showPreviewBtnAction();
		}
		


	private void showPreviewBtnAction() {
		fontPreviePgrsPane.getChildren().remove(fntPrwProgress);
		showPreviewBtn.setOnMouseClicked(ev->{
	    if(!urOwnTextFld.getText().isEmpty() || !Objects.isNull(inputfile)) {
				Runnable r = ()->{
					try {
						glyphDatas = null;
						Platform.runLater(()->fontPreviePgrsPane.getChildren().add(fntPrwProgress));
						glyphDatas =  fontHelper.getCharactersAsGlyph(inputfile, urOwnTextFld.getText());
						double wdth = Double.parseDouble(imgWidth.getText());
						double height = Double.parseDouble(imgHeight.getText());
						generateFontPreview(glyphDatas, wdth, height);
						Platform.runLater(()->fontPreviePgrsPane.getChildren().remove(fntPrwProgress));
				} catch (IOException e) {
					LOGGER.severe("Preview Failure :( ");
					LOGGER.severe(e.getMessage());
				}
				};
				executorService.execute(r);
	    }else {
	    	JFXUtil.showInfoAlert("Please chech the inputs..", "Please chech the inputs..");
	    }
		});
		
	}



	private void loadRAMStatus() {
	
		
		TimerTask task = new TimerTask() {
			  @Override
			  public void run() {
				  double percentage = RuntimeUtil.getPercentageUsed();
				  String usedInMB = RuntimeUtil.getUsedMemoryInMiB();
				  String maximumInMB = RuntimeUtil.getMaxMemoryInMiB();
				  
					Platform.runLater(()->{
						ramProgressBar.setProgress(percentage);
						memAvlStatus.setText("Used / Maximum - "+usedInMB +"/"+ maximumInMB);

					});
			  }
			};

			ramTimer.schedule(task, 0l, 1000l);
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
		
		System.out.println(Thread.currentThread().getName());

		
		startAction.setOnMouseClicked(ev->{
			
			Runnable r = ()->{

			
			
			
			if(Objects.isNull(inputfile) || Objects.isNull(outputDirectory) || urOwnTextFld.getText().isEmpty()) {
				
			}else {
				try {
			        LOGGER.info("Loading Font ......");
					System.out.println(Thread.currentThread().getName());

			        LOGGER.info("Successfully Loaded Font ......");
			        imagePreview.getChildren().clear();
			        
			        List<ZipEntrySource> files = new ArrayList<>();
			        glyphDatas.forEach(g->{
			        	byte[] imgBytes;
						try {
							double wdth = Double.parseDouble(imgWidth.getText());
							double height = Double.parseDouble(imgHeight.getText());
							imgBytes = g.getPath(JFXUtil.toHexString(fontColor.getValue()),wdth,height).toImageBytes(ImageFormat.PNG);

							
							if(expAsSeparate.isSelected()) {
								if(ouputSvg.isSelected()) {
									g.getPath(JFXUtil.toHexString(fontColor.getValue()),wdth,height).toSVG(new File(outputDirectory, g.getName()+".svg").getAbsolutePath());
								}else if(outputPng.isSelected()) {
									g.getPath(JFXUtil.toHexString(fontColor.getValue()),wdth,height).toImage(new File(outputDirectory, g.getName()+".png"), ImageFormat.PNG);
								}
								
							}else if(expAsZip.isSelected()) {
								if(ouputSvg.isSelected()) {
									files.add(new ByteSource(g.getName()+".svg", g.getPath(JFXUtil.toHexString(fontColor.getValue()),wdth,height).toSVG().getBytes()));

								}else if(outputPng.isSelected()) {
									files.add(new ByteSource(g.getName()+".png", imgBytes));

								}
								
							}
							

							
							//Common preview.
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
			
			};
			executorService.submit(r);

		});
		
		
		

	}
	
	
	private void generateFontPreview(List<GlyphData>  glyphDatas, double width, double height) {
		Platform.runLater(()->imagePreview.getChildren().clear());

		glyphDatas.forEach(g->{
			byte[] imgBytes;
			try {
				imgBytes = g.getPath(JFXUtil.toHexString(fontColor.getValue()),width,height).toImageBytes(ImageFormat.PNG);
				Platform.runLater(()-> {
					imagePreview.getChildren().add(new ImageView(new Image(new ByteArrayInputStream(imgBytes))));

				});

			} catch (IOException e) {
				e.printStackTrace();
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
