import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Homework 4: application class.
 * 
 * @author Kyle Jeffries
 * @version 1
 */
public class HW4 extends Application {
	private final static String TITLE = "HW4: kjeff";
	private final static String HELP = "Homework 4 version 1.";
	private MenuBar menuBar = null;
	private Menu fileMenu = null;
	private Menu editMenu = null;
	private Menu helpMenu = null;
	private MenuItem openMenuItem = null;
	private MenuItem closeMenuItem = null;
	private MenuItem saveMenuItem = null;
	private MenuItem quitMenuItem = null;
	private MenuItem copyMenuItem = null;
	private MenuItem pasteMenuItem = null;
	private MenuItem aboutMenuItem = null;
	private HW4View view = null;
	private HW4ViewGUI viewGUI = null;
	private HW4Model model = null;
	private Stage mainStage = null;
	private File file = null;
	private String clipboard = null;

	/**
	 * runs and sets up initial view
	 * 
	 * @param stage
	 *            to start out as
	 */
	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		menuBar = new MenuBar();
		fileMenu = new Menu("File");
		fileMenu.setStyle("-fx-fill: black;");
		openMenuItem = new MenuItem("Open");
		openMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Data File");
				file = fileChooser.showOpenDialog(mainStage);
				if (file != null) {
					try {

						// Create input stream (a BufferdReader object) from the
						// file.
						BufferedReader inputStream = new BufferedReader(
								new FileReader(file));

						// Read the first line to get the column names.
						String line = null;
						if ((line = inputStream.readLine()) != null) {
							Scanner scanner = new Scanner(line);
							scanner.useDelimiter(",");
							while (scanner.hasNext()) {
								model.addColumn(scanner.next());
							}
							scanner.close();
						}

						// Read the remaining lines to get the data.
						while ((line = inputStream.readLine()) != null) {
							ArrayList<String> tmpVector = new ArrayList<String>();
							Scanner scanner = new Scanner(line);
							scanner.useDelimiter(",");
							while (scanner.hasNext()) {
								tmpVector.add(scanner.next());
							}
							model.addRow(tmpVector);
							scanner.close();
						}

						// Close the input stream.
						inputStream.close();
						viewGUI.update();
						view.update();
						mainStage.setTitle(TITLE + ": " + file.getName());
					} catch (IOException ex) {
						System.err.println(ex);
					}
				}
			}
		});
		closeMenuItem = new MenuItem("Close");
		closeMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
		closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// TO DO: Complete File->Close
				viewGUI.clear();
				model.clearAllData();
				view.clear();

			}
		});
		saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		saveMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// Save
				try {
					// Create output stream (a BufferdReader object) from the
					// file.
					BufferedWriter outputStream = new BufferedWriter(
							new FileWriter(file));
					// Write the first line to store the column names.
					int columnCount = model.getColumns().size();
					int rowCount = model.getData().size();
					if (columnCount > 0) {
						for (int i = 0; i < columnCount - 1; i++) {
							outputStream.write(model.getColumn(i) + ",");
						}
						outputStream.write(model.getColumn(columnCount - 1)
								+ "\n");
					}
					for (int i = 0; i < rowCount; i++) {
						outputStream.write(model.getData().get(i).get(0)
								.getValue().toString()
								+ ", ");
						outputStream.write(model.getData().get(i).get(1)
								.getValue().toString()
								+ "\n");
					}
					outputStream.close();
				} catch (IOException ex) {
					System.err.println(ex);
				}
			}
		});
		quitMenuItem = new MenuItem("Quit");
		quitMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
		quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.getItems().addAll(openMenuItem, closeMenuItem, saveMenuItem,
				quitMenuItem);

		editMenu = new Menu("Edit");
		copyMenuItem = new MenuItem("Copy");
		copyMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
		copyMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// Copy
				int row = viewGUI.getEditingRow();
				int col = viewGUI.getEditingColumn();
				if (row >= 0 && col >= 0) {
					// grab the value at the row, col and store into clipboard
					clipboard = model.getData().get(row).get(col).getValue();
				} else {
					clipboard = null;
				}
			}
		});
		pasteMenuItem = new MenuItem("Paste");
		pasteMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
		pasteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// Paste
				int row = viewGUI.getEditingRow();
				int col = viewGUI.getEditingColumn();
				if (row >= 0 && col >= 0) {
					// change the value in the model
					model.getData().get(row).get(col).setValue(clipboard);
					// update the viewGUI
					viewGUI.update();
				}
			}
		});
		editMenu.getItems().addAll(copyMenuItem, pasteMenuItem);

		helpMenu = new Menu("Help");
		aboutMenuItem = new MenuItem("About Homework 4");
		aboutMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
		aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * Dialog
			 * 
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				final Stage stage = new Stage();
				Text message = new Text(HELP);
				Button button = new Button("OK");
				button.setOnAction(new EventHandler<ActionEvent>() {
					/**
					 * @param e
					 *            action that occurs
					 */
					@Override
					public void handle(ActionEvent e) {
						stage.close();
					}
				});
				BorderPane.setAlignment(button, Pos.BOTTOM_CENTER);
				BorderPane borderPane = new BorderPane();
				borderPane.setCenter(message);
				borderPane.setBottom(button);
				Scene page2 = new Scene(borderPane, 200, 100);
				stage.setScene(page2);
				stage.show();
			}
		});
		helpMenu.getItems().addAll(aboutMenuItem);

		menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

		stage.setTitle(TITLE);
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 800, 400);

		model = new HW4Model();
		view = new HW4View(model);
		viewGUI = new HW4ViewGUI(model);


		SplitPane content = new SplitPane();
		content.setDividerPosition(1, 400);
		content.setOrientation(Orientation.HORIZONTAL);
		content.getItems().addAll(viewGUI, view);
		root.setTop(menuBar);
		root.setCenter(content);
		stage.setScene(scene);
		scene.getStylesheets().add("HW4.css");
		stage.show();
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}