package droplauncher.mvc.view;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Container class for easier GridPane manipulation.
 */
public class CustomGridPane {

  private GridPane gridPane;
  private int column;
  private int row;
  private ArrayList<Node> nodes;

  public CustomGridPane() {
    this.gridPane = new GridPane();
    this.gridPane.setPadding(new Insets(0, 0, 0, 0));
    this.gridPane.setHgap(0);
    this.gridPane.setVgap(0);
    this.column = 0;
    this.row = 0;
    this.nodes = new ArrayList<>();
  }

  public GridPane get() {
    return this.gridPane;
  }

  /**
   * Packs all Node objects into the class GridPane object.
   * Use {@link #get()} to retrieve it.
   */
  public void pack() {
    for (Node node : nodes) {
      this.gridPane.getChildren().add(node);
    }
  }

  public int getColumn() {
    return this.column;
  }

  public int getRow() {
    return this.row;
  }

  public void setGaps(int hGap, int vGap) {
    this.gridPane.setHgap(hGap);
    this.gridPane.setVgap(vGap);
  }

  /**
   * Adds the specified node to the class GridPane object.
   *
   * @param node specified node to add
   * @param nextRow whether to set the cursor to the next row after adding
   */
  public void add(Node node, boolean nextRow) {
    GridPane.setConstraints(node, this.column, this.row);
    this.nodes.add(node);
    nextColumn();
    if (nextRow) {
      nextRow();
    }
  }

  public void add(Node node) {
    add(node, false);
  }

  /**
   * Sets the cursor to the specified coordinates.
   *
   * @param column column
   * @param row row
   */
  public void setCursor(int column, int row) {
    this.column = column;
    this.row = row;
  }

  /**
   * Sets the cursor to the next column.
   */
  public void nextColumn() {
    this.column++;
  }

  /**
   * Sets the cursor to the next row, first column.
   */
  public void nextRow() {
    this.column = 0;
    this.row++;
  }

}