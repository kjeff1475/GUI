import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Homework 4: chart class.
 * 
 * @author Kyle Jeffries
 * @version 1
 */
public class HW4View extends BorderPane {
	private ToolBar toolbar = null;
	private ComboBox<String> comboBox = null;
	private ColorPicker backgroundButton = null;
	private ColorPicker foregroundButton = null;
	private ColorPicker selectionButton = null;
	private Color backgroundColor = Color.WHITE;
	private Color foregroundColor = Color.BLUE;
	private Color selectionColor = Color.RED;
	private ScatterChart<Number, Number> chart = null;
	private NumberAxis xAxis = null;
	private NumberAxis yAxis = null;
	private Polygon rubberband = null;
	private boolean added = false;
	private double sceneX = -1;
	private double sceneY = -1;
	private double oldX = 1;
	private double oldY = 1;
	private ObservableList<Integer> selectedIndices = null;

	private HW4Model model = null;

	public HW4View(HW4Model m) {
		super();
		ObservableList<String> options = FXCollections.observableArrayList("1",
				"2", "3", "4", "5", "6", "7", "8", "9", "10");
		selectedIndices = FXCollections.observableArrayList();
		model = m;
		rubberband = new Polygon();
		comboBox = new ComboBox<String>();
		comboBox.setItems(options);
		comboBox.getSelectionModel().select(4);
		comboBox.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				if (chart.getData() != null && chart.getData().size() > 0) {
					double scale = (comboBox.getSelectionModel()
							.getSelectedIndex() + 1) / 5.0;
					XYChart.Series<Number, Number> series = chart.getData()
							.get(0);
					for (int i = 0; i < series.getData().size(); i++) {
						series.getData().get(i).getNode().setScaleX(scale);
						series.getData().get(i).getNode().setScaleY(scale);
					}
				}
			}
		});

		backgroundButton = new ColorPicker();
		backgroundButton.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// TO DO: complete the background color.
				backgroundColor = backgroundButton.getValue();
				chart.setStyle("-fx-background-color: #"
						+ backgroundColor.toString().substring(2, 8) + ";");
			}
		});
		backgroundButton.setValue(backgroundColor);
		foregroundButton = new ColorPicker();
		foregroundButton.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// TO DO: set the foreground color.
				foregroundColor = foregroundButton.getValue();
				if (chart.getData() != null) {
					XYChart.Series<Number, Number> series = chart.getData()
							.get(0);
					for (int k = 0; k < series.getData().size(); k++) {
						series.getData()
								.get(k)
								.getNode()
								.setStyle(
										"-fx-background-color: #"
												+ foregroundColor.toString()
														.substring(2, 8) + ";");
					}
				}
			}
		});
		foregroundButton.setValue(foregroundColor);
		selectionButton = new ColorPicker();
		selectionButton.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * @param e
			 *            action that occurs
			 */
			@Override
			public void handle(ActionEvent e) {
				// TO DO: set the selection color.
				selectionColor = selectionButton.getValue();
				updateSelection();
			}
		});
		selectionButton.setValue(selectionColor);
		toolbar = new ToolBar(comboBox, backgroundButton, foregroundButton,
				selectionButton);
		setTop(toolbar);
		xAxis = new NumberAxis(-1, 1, 1);
		yAxis = new NumberAxis(-1, 1, 1);
		chart = new ScatterChart<Number, Number>(xAxis, yAxis);
		chart.setHorizontalZeroLineVisible(false);
		chart.setVerticalZeroLineVisible(false);
		setOnMousePressed(new EventHandler<MouseEvent>() {
			/**
			 * @param e
			 *            mouse click that occurs
			 */
			@Override
			public void handle(MouseEvent e) {
				added = false;
				// TO DO: do the rubberband selection (currently hardcoded)
				if (e.getButton() == MouseButton.PRIMARY) {
					added = false;
					oldX = e.getX();
					oldY = e.getY();
					sceneX = e.getSceneX();
					sceneY = e.getSceneY();
				} else {
					model.clearSelectedIndices();
				}
			}
		});
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			/**
			 * @param e
			 *            mouse click that occurs
			 */
			@Override
			public void handle(MouseEvent e) {
				// TO DO: do the rubberband selection (currently hardcoded)
				if (e.getButton() == MouseButton.PRIMARY) {
					if (!added) {
						model.clearSelectedIndices();
						rubberband.getPoints().clear();
						rubberband.getPoints().addAll(
								new Double[] { oldX, oldY, e.getX(), oldY,
										e.getX(), e.getY(), oldX, e.getY() });
						rubberband.setFill(null);
						rubberband.setStroke(selectionColor);
						getChildren().add(rubberband);
						XYChart.Series<Number, Number> series = chart.getData()
								.get(0);

						for (int i = 0; i < series.getData().size(); i++) {
							Node node = series.getData()
									.get(series.getData().size() - 1 - i)
									.getNode();
							if (rubberband.localToScene(
									rubberband.getBoundsInLocal()).intersects(
									node.localToScene(node.getBoundsInLocal()))) {
								System.out.println("hi");
								selectedIndices.add(i);
							}
						}
						model.setSelectedIndices(selectedIndices);
						added = true;
					} else {
						rubberband.getPoints().clear();
						rubberband.getPoints().addAll(
								new Double[] { oldX, oldY, e.getX(), oldY,
										e.getX(), e.getY(), oldX, e.getY() });
						rubberband.setFill(null);
						rubberband.setStroke(selectionColor);
					}
				}
			}
		});
		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			/**
			 * @param e mouse click that occurs
			 */
			@Override
			public void handle(MouseEvent e) {
				// TO DO: do the rubberband selection (currently hardcoded)
				if (e.getButton() == MouseButton.PRIMARY) {
					getChildren().remove(rubberband);
					updateSelection(sceneX, sceneY, e.getSceneX(),
							e.getSceneY());
					added = false;
				}

				if (e.getButton() == MouseButton.SECONDARY) {
					model.getSelectedIndices().clear();
				}
			}
		});
		xAxis.setLabel("x-coordinate");
		yAxis.setLabel("y-coordinate");
		chart.setTitle("Data Points");
		setCenter(chart);

		if (model.getSelectedIndices() != null) {
			model.getSelectedIndices().addListener(
					new ListChangeListener<Integer>() {

						/**
						 * @param e
						 *            listener that is triggered
						 */
						@Override
						public void onChanged(
								ListChangeListener.Change<? extends Integer> e) {
							updateSelection();
						}
					});
		}

	}

	/**
	 * update the data
	 */
	public void update() {
		updateData();
	}

	/**
	 *  update the data and set the padding, axis, and ticks 
	 */
	@SuppressWarnings("unchecked")
	public void updateData() {
		if (chart.getData() == null) {
			return;
		}
		try {
			chart.getData().remove(0);
		} catch (IndexOutOfBoundsException e) {

		}
		double upperX = model.getUpperBound(0);
		double lowerX = model.getLowerBound(0);

		double paddingX = ((upperX - lowerX) / 8);

		xAxis.setUpperBound(upperX + paddingX);
		xAxis.setLowerBound(lowerX - paddingX);

		xAxis.setTickUnit(((upperX + paddingX) - (lowerX - paddingX)) / 10);
		xAxis.setLabel(model.getColumn(0));

		double upperY = model.getUpperBound(1);
		double lowerY = model.getUpperBound(1);

		double paddingY = ((upperY - lowerY) / 8);

		yAxis.setUpperBound(upperY + paddingY);
		yAxis.setLowerBound(lowerY - paddingY);
		yAxis.setTickUnit(((upperY + paddingY) - (lowerY - paddingY)) / 10);
		yAxis.setLabel(model.getColumn(1));

		XYChart.Series<Number, Number> series = model.getSeries(0, 1);
		if (series != null && series.getData().size() > 0) {
			chart.getData().addAll(series);
			for (int i = 0; i < series.getData().size(); i++) {
				series.getData()
						.get(i)
						.getNode()
						.setStyle(
								"-fx-background-color: #"
										+ foregroundColor.toString().substring(
												2, 8) + ";");
			}
		}
	}

	/**
	 * update the screen
	 */
	public void updateSelection() {
		if (chart.getData().size() == 0) {
			return;
		}
		XYChart.Series<Number, Number> series = chart.getData().get(0);
		if (series != null) {
			System.out.println(model.getSelectedIndices());
			for (int i = 0; i < series.getData().size(); i++) {
				if (model.getSelectedIndices().contains(i)) {
					System.out.println(i);
					series.getData()
							.get(series.getData().size() - 1 - i)
							.getNode()
							.setStyle(
									"-fx-background-color: #"
											+ selectionColor.toString()
													.substring(2, 8) + ";");
				} else {
					series.getData()
							.get(series.getData().size() - 1 - i)
							.getNode()
							.setStyle(
									"-fx-background-color: #"
											+ foregroundColor.toString()
													.substring(2, 8) + ";");
				}
			}
		}
	}

	/**
	 * update the screen given the parameters
	 * 
	 * @param x1
	 *            sceneX
	 * @param y1
	 *            sceneY
	 * @param x2
	 *            x scene after the mouse click
	 * @param y2
	 *            y scene after the mouse click
	 */
	public void updateSelection(double x1, double y1, double x2, double y2) {
		double xmin = x1 <= x2 ? x1 : x2;
		double xmax = x1 > x2 ? x1 : x2;
		double ymin = y1 <= y2 ? y1 : y2;
		double ymax = y1 > y2 ? y1 : y2;
		BoundingBox bb = new BoundingBox(xmin, ymin, xmax - xmin, ymax - ymin);
		XYChart.Series<Number, Number> series = chart.getData().get(0);
		if (series != null) {
			for (int k = 0; k < series.getData().size(); k++) {
				Node n = series.getData().get(series.getData().size() - 1 - k)
						.getNode();
				if (n.intersects(n.sceneToLocal(bb))) {
					model.getSelectedIndices().add(k);
				}
			}
		}
	}

	/**
	 * clear the data on the chart
	 */
	public void clear() {
		chart.getData().clear();
		
		xAxis.setUpperBound(1);
        xAxis.setLowerBound(-1);
        xAxis.setTickUnit(1);
        xAxis.setLabel("x-coordinate");

        yAxis.setUpperBound(1);
        yAxis.setLowerBound(-1);
        yAxis.setTickUnit(1);
        yAxis.setLabel("y-coordinate");
	}

}
