package Helpings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ExceptionHandling.RowNumberNotExistException;



public class List {

	private HashMap<Integer, ArrayList<String>> values = new HashMap<Integer, ArrayList<String>>();
	public int rowNumber = -1;

	public List() {
	}

	public void put(ArrayList<String> values) {

		if (rowNumber == -1)
			this.values.put(++rowNumber, values);
		else
			this.values.put(++rowNumber, values);
	}

	public void replace(int index, ArrayList<String> vals) {

		if (index > rowNumber)
			throw new RowNumberNotExistException("[Custom] Index not found");

		values.replace(index, vals);

	}

	public ArrayList<String> get(int rowNumber) {

		if (this.rowNumber < rowNumber)
			throw new NullPointerException("Row Number " + rowNumber + " not found!");

		return values.get(rowNumber);
	}

	/**
	 * 
	 * @param uniqueCol
	 * 
	 *            This method removes duplicate rows based on unique Column Index
	 * 
	 * 
	 */

	public void removeDuplicate(int uniqueCol) {

		HashMap<Integer, ArrayList<String>> values = new HashMap<Integer, ArrayList<String>>();
		int rowNumber = -1;
		String preValue = "";
		for (Integer rowindex : this.values.keySet()) {
			ArrayList<String> row = this.values.get(rowindex);

			String value = row.get(uniqueCol);
			if (!value.equalsIgnoreCase(preValue)) {
				values.put(++rowNumber, row);
				preValue = value;
			}
		}

		this.rowNumber = rowNumber;
		this.values = values;
	}

	/**
	 * 
	 * @param uniqueColID
	 * @param filterID
	 * @param secfilterIDs
	 * 
	 *            This method remove duplicate using UCID, primary Filter ID and
	 *            filter ID combinations
	 * 
	 *            if not required, input filter ID as -1 and secfilterIDs as null
	 * 
	 * 
	 * @return
	 * 
	 */
	public void removeDuplicate(int uniqueColID, int filterID, ArrayList<Integer> secfilterIDs) {

		ArrayList<List> lists = new ArrayList<List>();
		// primaryFilter
		if (filterID != -1) {
			lists = filter(filterID);
		}
		// secFilter
		if (secfilterIDs != null) {
			for (int filter : secfilterIDs)
				lists = filter(filter, lists);
		}

		HashMap<Integer, ArrayList<String>> values = new HashMap<Integer, ArrayList<String>>();
		int rowNumber = -1;

		for (List fetchlist : lists) {
			String preVal = "";

			for (int i = 0; i < fetchlist.size(); i++) {

				ArrayList<String> fetchRow = fetchlist.get(i);

				String value = fetchRow.get(uniqueColID);
				if (!value.equalsIgnoreCase(preVal)) {
					values.put(++rowNumber, fetchRow);
					preVal = value;
				}
			}
		}
		this.rowNumber = rowNumber;
		this.values = values;

	}

	public int size() {
		return rowNumber + 1;
	}

	/**
	 * 
	 * @param filterID
	 * @param list
	 * 
	 *            This method filter out same rows using filter ID and arraylist of
	 *            List which has to be filter out and returns as Arraylist of List
	 * 
	 * 
	 * @return
	 */

	public ArrayList<List> filter(int filterID, ArrayList<List> list) {

		ArrayList<List> lists = new ArrayList<List>();

		for (List listIterate : list) {

			String preValue = null;
			List fetchlist = new List();
			ArrayList<String> row = new ArrayList<String>();

			for (int i = 0; i < listIterate.size(); i++) {
				row = listIterate.get(i);

				String val = row.get(filterID);
				// one time
				if (preValue == null)
					preValue = val;

				if (val.equalsIgnoreCase(preValue))
					fetchlist.put(row);
				else {
					preValue = val;
					lists.add(fetchlist);
					fetchlist = new List();
					fetchlist.put(row);
				}
			}

			if (!row.isEmpty())
				lists.add(fetchlist);
		}

		return lists;
	}

	/**
	 * 
	 * @param filterID
	 * 
	 *            This method filter out List using filter ID and return Arraylist
	 *            of List Type
	 * 
	 * 
	 * @return
	 */

	public ArrayList<List> filter(int filterID) {

		ArrayList<List> lists = new ArrayList<List>();
		String preValue = null;
		List list = new List();
		ArrayList<String> row = new ArrayList<String>();
		for (int i = 0; i < values.size(); i++) {
			row = values.get(i);

			String val = row.get(filterID);
			// one time
			if (preValue == null)
				preValue = val;

			if (val.equalsIgnoreCase(preValue))
				list.put(row);
			else {
				preValue = val;
				lists.add(list);
				list = new List();
				list.put(row);
			}
		}

		if (!row.isEmpty())
			lists.add(list);

		return lists;
	}

	public String toString() {

		System.out.println("-----------------------------------------------------");

		for (int i = 0; i < values.size(); i++) {

			System.out.print("Row:" + i + "\t");
			System.out.print(values.get(i));
			System.out.println();

		}

		System.out.println("----------------------------------------------------");
		return "";
	}

//	public static void main(String[] args) {
//
//		List list = new List();
//		list.put(new ArrayList<String>(Arrays.asList("Buenos Aires", "Córdoba", "La Plata")));
//		list.put(new ArrayList<String>(Arrays.asList("Buenos", "doba", "Plata")));
//		list.put(new ArrayList<String>(Arrays.asList("Peurto", "Combodia", "La Vespa")));
//
//		list.toString();
//
//	}

}
