package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Controller {
    private enum Extensions {JPG, PNG}

    ;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 3;

    @FXML
    private ImageView originalScene;
    @FXML
    private GridPane grid;
    @FXML
    private AnchorPane ap;

    private ImagePermutationModel permutationModel;
    private Stage st;
    private DropShadow redBoder;
    private DropShadow greenBorder;

    public Controller() {
        redBoder = new DropShadow();
        greenBorder = new DropShadow();
        initBorder(Color.RED, redBoder);
        initBorder(Color.GREEN, greenBorder);
    }

    private void initBorder(javafx.scene.paint.Color col, DropShadow border) {
        border.setOffsetX(0f);
        border.setColor(col);
        border.setWidth(10);
        border.setHeight(10);
        border.setBlurType(BlurType.GAUSSIAN);
        border.setSpread(10);
    }

    @FXML
    public void initialize() throws FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        chooseFile();
        initGrid();
        fillGrid();
    }

    public DropShadow getRedBoder() {
        return redBoder;
    }

    public DropShadow getGreenBorder() {
        return greenBorder;
    }

    public void setStage(Stage st) {
        this.st = st;
    }

    public GridPane getGrid() {
        return grid;
    }

    private void chooseFile() throws FileNotFoundException, UnsupportedEncodingException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fileChooser.showOpenDialog(st);
        if (file != null) {
            if (!checkImageExtension(file.getName()))
                throw new UnsupportedEncodingException("Unsopported format");

            originalScene.imageProperty().setValue(new javafx.scene.image.Image("file:" + file.getAbsolutePath()));
        } else {
            throw new FileNotFoundException("File not found");
        }
    }

    private boolean checkImageExtension(String fileName) {
        for (Extensions ex : Extensions.values()) {
            if (!fileName.endsWith(ex.name().toLowerCase()))
                return true;
        }
        return false;
    }

    private void initGrid() {
        for (int i = 0; i < WIDTH; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setFillWidth(true);
            grid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < HEIGHT; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setFillHeight(true);
            grid.getRowConstraints().add(rowConst);
        }
    }

    public boolean isSuccess() {
        return permutationModel.isSuccess();
    }

    public void swap(int row1, int col1, int row2, int col2) {
        permutationModel.swap(row1, col1, row2, col2);
    }

    private void fillGrid() throws InterruptedException {
        permutationModel = new ImagePermutationModel(originalScene.getImage(), WIDTH, HEIGHT, grid.getPrefWidth(),
                grid.getPrefHeight());
        int requiredWidth = (int) (grid.getPrefWidth() / WIDTH);
        int requiredHeight = (int) (grid.getPrefHeight() / HEIGHT);

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                ImageView imView = new ImageView(permutationModel.getCell(i, j, requiredWidth, requiredHeight));
                imView.resize(requiredWidth, requiredHeight);
                imView.setFitWidth(requiredWidth);
                imView.setFitHeight(requiredHeight);

                imView.setOnDragDetected(event -> {
                    /* drag was detected, start a drag-and-drop gesture*/
                    /* allow any transfer mode */
                    Dragboard db = imView.startDragAndDrop(TransferMode.ANY);
                    db.setDragView(new Image("file:pics/transparent.png"), 1500, 1500);
                    /* Put an image on a dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(imView.getImage());
                    db.setContent(content);

                    event.consume();
                });

                imView.setOnDragOver(event -> {
                    /* data is dragged over the target */
                    /* accept it only if it is not dragged from the same node
                     * and if it has a string data */
                    if (event.getGestureSource() != imView &&
                            event.getDragboard().hasImage()) {
                        /* allow for moving */
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                imView.setOnDragEntered(event -> {
                    /* the drag-and-drop gesture entered the target */
                    /* show to the user that it is an actual gesture target */
                    if (event.getGestureSource() != imView &&
                            event.getDragboard().hasImage()) {
                    }

                    event.consume();
                });

                imView.setOnDragExited(event -> {
                    /* mouse moved away, remove the graphical cues */
                    //target.setFill(Color.BLACK);
                    event.consume();
                });

                imView.setOnDragDropped(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        /* data dropped */
                        /* if there is a image data on dragboard, read it and use it */
                        Dragboard db = event.getDragboard();
                        boolean success = false;
                        if (db.hasImage()) {
                            Image i = imView.getImage();
                            imView.setImage(db.getImage());

                            ClipboardContent content = new ClipboardContent();
                            content.putImage(i);
                            content.putString("" + GridPane.getRowIndex(imView) + " " + GridPane.getColumnIndex(imView));
                            db.setContent(content);

                            success = true;
                        }
                        /* let the source know whether the string was successfully
                         * transferred and used */
                        event.setDropCompleted(success);
                        event.consume();
                    }
                });

                imView.setOnDragDone(new EventHandler<DragEvent>() {
                    public void checkResult() {
                        boolean isComplete = isSuccess();
                        if (isComplete) {
                            getGrid().setEffect(getGreenBorder());
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Info");
                            alert.setHeaderText("You restored picture");
                            //alert.setContentText("I have a great message for you!");
                            alert.showAndWait();
                        } else {
                            getGrid().setEffect(getRedBoder());
                        }
                    }

                    public void commit(int row1, int col1, int row2, int col2) {
                        swap(row1, col1, row2, col2);
                        checkResult();
                    }

                    public void handle(DragEvent event) {
                        /* the drag and drop gesture ended */
                        /* if the data was successfully moved, clear it */
                        if (event.getTransferMode() == TransferMode.MOVE) {
                            imView.setImage(event.getDragboard().getImage());
                            String content = event.getDragboard().getString();
                            int row1 = GridPane.getRowIndex(imView);
                            int col1 = GridPane.getColumnIndex(imView);

                            int row2 = Integer.parseInt(content.split("\\s+")[0]);
                            int col2 = Integer.parseInt(content.split("\\s+")[1]);
                            event.consume();
                            commit(row1, col1, row2, col2);
                            return;
                        }
                        event.consume();
                    }
                });

                grid.add(imView, j, i);
            }
        }
    }
}
