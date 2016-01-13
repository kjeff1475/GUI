import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;


/**
 * Homework 4: model class.
 * @author Kyle Jeffries
 * @version 1
 */
public class HW4Model {
	private ObservableList<String> columns = null;
	private ObservableList<ArrayList<StringProperty>> data = null;
	private ObservableList<Integer> selectedIndices = null;

	/**
	 * constructor for HW4 Model
	 */
	public HW4Model() {
		columns = FXCollections.observableArrayList();
		data = FXCollections.observableArrayList();
		selectedIndices = FXCollections.observableArrayList();
	}

	/**
	 * add column and give it name s
	 * @param s string you want to add as a columnn
	 */
	public void addColumn(String s) 
	{
		columns.add(s);
	}

	/**
	 * get the column at index i
	 * @param i the index of the column you want to get
	 * @return the column at i
	 */
	public String getColumn(int i) 
	{
		return columns.get(i);
	}

	/**
	 * get the list of columns
	 * @return the column names
	 */
	public ObservableList<String> getColumns() 
	{
		return columns;
	}

	/**
	 * add a row to the arrayList r
	 * @param r arrayList that you want to add a row to
	 */
	public void addRow(ArrayList<String> r) 
	{
		ArrayList<StringProperty> row = new ArrayList<StringProperty>();
		for(String s: r)
		{
			row.add(new SimpleStringProperty(s));
		}
		data.add(row);
	}

	/**
	 * return the data of the given observableList
	 * @return the data
	 */
	public ObservableList<ArrayList<StringProperty>> getData() 
	{
		return data;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public XYChart.Series<Number,Number> getSeries(int x, int y) {
		XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
		for (ArrayList<StringProperty> r : data) {
			if (r.size() > x && x >= 0 && r.size() > y && y >= 0) {
				series.getData().add(0, new XYChart.Data<Number,Number>(Float.parseFloat(r.get(x).get()), Float.parseFloat(r.get(y).get())));
			}
		}
		return series;
	}

	/**
	 * get the lowerbound for a column c 
	 * @param c column that you want the lower bound of
	 * @return the lowerbound
	 */
	public double getLowerBound(int c) {
// TO DO: return the lower bound (minimum value) for column c.
		 double min = Double.MAX_VALUE;
	        for (ArrayList<StringProperty> r : data)
	        {
	            if (r.size() < c || c < 0)
	                return -1;
	            if (min > Double.parseDouble(r.get(c).get()))
	            {
	                min = Double.parseDouble(r.get(c).get());
	            }
	        }
	        return min;
	}

	/**
	 * get the upperbound for a column c
	 * @param c column that you want the upper bound of
	 * @return the upperbound
	 */
	public double getUpperBound(int c) {
		 double max = Double.MIN_VALUE;
		 for (ArrayList<StringProperty> r : data)
		 	{
	            if (r.size() < c || c < 0)
	                return 1;
	            if (max < Double.parseDouble(r.get(c).get()))
	            {
	                max = Double.parseDouble(r.get(c).get());
	            }
		 	}
	    return max;
	}

	/**
	 * clear the selected indices and add the new ones, i
	 * @param i indices you want to add 
	 */
	public void setSelectedIndices(ObservableList<Integer> i) {
		// TO DO: update list.
		selectedIndices.clear();
		selectedIndices.addAll(i);
	}

	/**
	 * getter method for selectedIndices
	 * @return selected indices
	 */
	public ObservableList<Integer> getSelectedIndices() {
// TO DO: return selected indices.
		return selectedIndices;
	}
	
	/**
	 * clear the selected indices
	 */
	public void clearSelectedIndices()
	{
		selectedIndices.clear();
	}
	
	/**
	 * clear all the data in the model
	 */
	public void clearAllData()
	{
		columns.clear();
		data.clear();
		selectedIndices.clear();
	}

}
