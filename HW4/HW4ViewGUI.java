import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

/**
 * Homework 4: table view class.
 * 
 * @author Kyle Jeffries @ version 1
 */
public class HW4ViewGUI extends BorderPane {
	private TableView<ArrayList<StringProperty>> tableView = null;
	private HW4Model model;
	private boolean update = true;
	public BooleanProperty checkIfModelUpdated = new SimpleBooleanProperty(); 

	public HW4ViewGUI(HW4Model m) {
		super();
		model = m;
		tableView = new TableView<ArrayList<StringProperty>>();
		tableView.setEditable(true);
		tableView.getSelectionModel().getSelectedIndices()
				.addListener(new ListChangeListener<Integer>() {
					/**
					 * @param e action that occurs
					 */
					@Override
					public void onChanged(
							ListChangeListener.Change<? extends Integer> e) {
						if (update) {
							model.setSelectedIndices(tableView
									.getSelectionModel().getSelectedIndices());
						}
					}
				});
		model.getSelectedIndices().addListener(
				new ListChangeListener<Integer>() {
					@Override
					public void onChanged(
							ListChangeListener.Change<? extends Integer> e) {
						update = false;
						if (!(model.getSelectedIndices().isEmpty())) {
							for (int i : model.getSelectedIndices()) {
								tableView.getSelectionModel().select(i);
							}
							update = true;
						} else {
							tableView.getSelectionModel().clearSelection();
							update = true;
						}
					}
				});
		tableView.setPrefWidth(400);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setCenter(tableView);
	}

	/**
	 * update the viewGUI
	 */
	public void update() {
		TableColumn<ArrayList<StringProperty>,String> col = null;
		ObservableList<String> l = model.getColumns();
		if (l != null) {
			for (int i = 0; i < l.size(); i++) {
				String s = l.get(i);
				col = new TableColumn<ArrayList<StringProperty>,String>(s);
				// TODO: set the column width
				col.setMinWidth(197);
				col.setCellFactory(TextFieldTableCell.<ArrayList<StringProperty>>forTableColumn());
				//col.setOn
				col.setId(Integer.toString(i));
				col.setCellValueFactory(new Callback<CellDataFeatures<ArrayList<StringProperty>, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<ArrayList<StringProperty>, String> v) {
						checkIfModelUpdated.set(true);
						return v.getValue().get(Integer.parseInt(v.getTableColumn().getId()));
					}
				});
				tableView.getColumns().add(col);
			}
		}
		tableView.setItems(model.getData());
	}

	/**
	 * clears all of the data in the viewGUI
	 */
	public void clear() {
		tableView.getColumns().clear();
		tableView.getItems().clear();
	}

	/**
	 * get the column number that is being edited
	 * 
	 * @return int column number
	 */
	public int getEditingColumn() {
		return tableView.getEditingCell().getColumn();
	}

	// ----------------------------------------------------------
	/**
	 * get the row number that is being edited
	 * 
	 * @return int row number
	 */
	public int getEditingRow() {
		return tableView.getEditingCell().getRow();
	}

}
