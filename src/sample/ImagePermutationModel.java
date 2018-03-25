package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class ImagePermutationModel {
    private List<List<ImageBlock>> grid;
    private double colWidth, colHeight;

    public ImagePermutationModel(Image original, int width, int height, double parentWidth, double parentHeight) throws InterruptedException {
        colWidth = parentWidth / width;
        colHeight = parentHeight / height;

        double heightC = original.getHeight() / parentHeight;
        double widthC = original.getWidth() / parentWidth;

        grid = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            grid.add(new ArrayList<>());
        }

        PixelReader reader = original.getPixelReader();
        double prevX = 0;
        double prevY = 0;
        for (int i = 0; i < height; i++) {
            grid.set(i, new ArrayList<>(width));
            for (int j = 0; j < width; j++) {
                grid.get(i).add(new ImageBlock(j, i, new WritableImage(reader, (int) (prevX),
                        (int) (prevY), (int) (colWidth * widthC), (int) (colHeight * heightC))));
                prevX += colWidth * widthC;
            }
            prevY += colHeight * heightC;
            prevX = 0;
        }
        shuffle(height, width);
    }

    private void shuffle(int height, int width) {
        List<ImageBlock> src = new ArrayList<>();
        grid.forEach(src::addAll);
        Collections.shuffle(src);
        grid.forEach(List::clear);

        int idx = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ImageBlock ib = src.get(idx);
                ib.setCol(j);
                ib.setRow(i);
                grid.get(i).add(ib);
                idx++;
            }
        }
    }

    public Image getCell(int row, int col, int width, int height) {
        WritableImage image = grid.get(row).get(col).getImage();
        return image;
    }

    public boolean isSuccess() {
        AtomicBoolean res = new AtomicBoolean(true);
        grid.forEach(row -> {
            row.forEach(x -> {
                if (x.getCol() != x.getCorrectCol() || x.getRow() != x.getCorrectRow())
                    res.set(false);
            });
        });
        return res.get();
    }

    public void swap(int row1, int col1, int row2, int col2) {
        ImageBlock bl = grid.get(row1).get(col1);
        grid.get(row1).set(col1, grid.get(row2).get(col2));
        grid.get(row2).set(col2, bl);

        setNewIdx(grid.get(row1).get(col1), row1, col1);
        setNewIdx(grid.get(row2).get(col2), row2, col2);
    }

    public void setNewIdx(ImageBlock b, int newRow, int newCol) {
        b.setRow(newRow);
        b.setCol(newCol);
    }

    private void print() {
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(0).size(); j++) {
                System.out.println(grid.get(i).get(j));
            }
        }
    }
}
