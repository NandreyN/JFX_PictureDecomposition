package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageBlock {
    private int row;
    private int col;

    private final int correctCol;
    private final int correctRow;

    private WritableImage image;

    public ImageBlock(int correctCol, int correctRow, WritableImage img) {
        this.correctCol = correctCol;
        this.correctRow = correctRow;
        this.image = img;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public WritableImage getImage() {
        return image;
    }

    public int getCorrectRow() {
        return correctRow;
    }

    public int getCorrectCol() {
        return correctCol;
    }

    @Override
    public String toString() {
        return "Original : (" + correctRow + "," + correctCol + "), Current : (" + row + "," + col + ")";
    }
}
