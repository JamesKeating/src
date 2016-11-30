import sun.swing.BakedArrayList;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by siavj on 10/11/2016.
 */
public class Cell {

    private String cellValue;
    private String blankCell = " ";
    private int cellBorder;
    private Boolean isPlayable = false;

    public Cell(){
        this.cellValue = blankCell;
        this.cellBorder = -1;
    }

    public Cell(Cell another){
        this.cellValue = another.toString();
        this.cellBorder = another.getCellBorder();
        this.isPlayable = another.isPlayable;
    }

    public Boolean getPlayable() {
        return this.isPlayable;
    }

    public void setPlayable(Boolean playable) {
        this.isPlayable = playable;
    }


    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public int getCellBorder() {
        return cellBorder;
    }

    public void setCellBorder(int cellBorder) {
        this.cellBorder = cellBorder;
    }

    public String toString(){
        return this.cellValue;
    }
}
