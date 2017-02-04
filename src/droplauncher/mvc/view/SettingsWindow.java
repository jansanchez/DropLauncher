package droplauncher.mvc.view;

import adakite.ini.INI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.util.Constants;
import droplauncher.util.SettingsKey;
import droplauncher.util.Util;
import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {

  private static final int PADDING = 20;
  private static final int TOP_PADDING = PADDING;
  private static final int BOTTOM_PADDING = PADDING;
  private static final int LEFT_PADDING = PADDING;
  private static final int RIGHT_PADDING = PADDING;
  private static final int GAP = 10;

  private Stage stage;
  private Scene scene;

  private CheckBox chkKeepClientWindow;
  private Label lblChangeStarcraftExe;
  private Label lblChangeStarcraftExeText;
  private Button btnChangeStarcraftExe;
  private Label lblChangeJavaExe;
  private Label lblChangeJavaExeText;
  private Button btnChangeJavaExe;

  private INI ini;

  private SettingsWindow() {}

  public SettingsWindow(INI ini) {
    this.ini = ini;
  }

  public SettingsWindow showAndWait() {
    this.chkKeepClientWindow = new CheckBox("Show log window for executable bot clients (requires program restart)");
    if (this.ini.hasValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.SHOW_LOG_WINDOW.toString())) {
      if (this.ini.getValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.SHOW_LOG_WINDOW.toString()).equalsIgnoreCase(Boolean.TRUE.toString())) {
        this.chkKeepClientWindow.setSelected(true);
      } else {
        this.chkKeepClientWindow.setSelected(false);
      }
    }
    this.chkKeepClientWindow.setOnAction(e -> {
      String val;
      if (this.chkKeepClientWindow.isSelected()) {
        val = Boolean.TRUE.toString();
      } else {
        val = Boolean.FALSE.toString();
      }
      this.ini.set(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.SHOW_LOG_WINDOW.toString(), val);
    });

    this.lblChangeStarcraftExe = new Label("StarCraft.exe:");
    this.btnChangeStarcraftExe = new Button("...");
    this.lblChangeStarcraftExeText = new Label(this.ini.getValue(BWHeadless.BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString()));

    this.lblChangeJavaExe = new Label("Java.exe:");
    this.btnChangeJavaExe = new Button("...");
    this.lblChangeJavaExeText = new Label(this.ini.getValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString()));

    this.btnChangeStarcraftExe.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("StarCraft.exe", "StarCraft.exe"));
      fc.setTitle("Select StarCraft.exe ...");
      String userDirectory = Util.getUserHomeDirectory();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File file = fc.showOpenDialog(this.stage);
      if (file != null) {
        this.ini.set(BWHeadless.BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString(), file.getAbsolutePath());
        this.lblChangeStarcraftExeText.setText(this.ini.getValue(BWHeadless.BWHEADLESS_INI_SECTION, SettingsKey.STARCRAFT_EXE.toString()));
      }
    });
    this.btnChangeJavaExe.setOnAction(e -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("java.exe", "java.exe"));
      fc.setTitle("Select java.exe ...");
      String userDirectory = Util.getUserHomeDirectory();
      if (userDirectory != null) {
        fc.setInitialDirectory(new File(userDirectory));
      }
      File file = fc.showOpenDialog(this.stage);
      if (file != null) {
        this.ini.set(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString(), file.getAbsolutePath());
        this.lblChangeJavaExeText.setText(this.ini.getValue(Constants.DROPLAUNCHER_INI_SECTION, SettingsKey.JAVA_EXE.toString()));
      }
    });

    CustomGridPane fileSelectPane = new CustomGridPane();
    fileSelectPane.add(this.lblChangeStarcraftExe);
    fileSelectPane.add(this.lblChangeStarcraftExeText);
    fileSelectPane.add(this.btnChangeStarcraftExe, true);
    fileSelectPane.add(this.lblChangeJavaExe);
    fileSelectPane.add(this.lblChangeJavaExeText);
    fileSelectPane.add(this.btnChangeJavaExe, true);
    fileSelectPane.setGaps(GAP, GAP);
    fileSelectPane.pack();

    CustomGridPane mainGridPane = new CustomGridPane();
    mainGridPane.add(fileSelectPane.get(), true);
    mainGridPane.add(new Separator(), true);
    mainGridPane.add(this.chkKeepClientWindow, true);
    mainGridPane.setGaps(GAP, GAP);
    mainGridPane.get().setPadding(new Insets(TOP_PADDING, LEFT_PADDING, BOTTOM_PADDING, RIGHT_PADDING));
    mainGridPane.pack();

    this.scene = new Scene(mainGridPane.get());

    this.stage = new Stage();
    this.stage.setTitle("Settings");
    this.stage.initModality(Modality.APPLICATION_MODAL);
    this.stage.setResizable(false);
    this.stage.setScene(this.scene);
    this.stage.showAndWait();

    return this;
  }

}