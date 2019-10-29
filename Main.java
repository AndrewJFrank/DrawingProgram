import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Main extends Application
{
    private static Stage window;
    private static boolean control = false;
    private static boolean z = false;
    private static boolean esc = false;
    //settings
    private static boolean spaceMode = false; //false
    private static boolean noLine = false; //false
    private static int height = 1021;
    private static int width = 1811;
    private static ImageView image = new ImageView();
    private static boolean usingImage = false;
    private static boolean startLine = true;
    private static int lastX = -1;
    private static int lastY = -1;
    private static Line line = new Line();
    private static ArrayList<ArrayList<Shape>> undoLog = new ArrayList<>();
    private static ArrayList<Shape> strokeLog = new ArrayList<>();
    //settings end
    //private static Shape cursorShape;
    public static void main(String[] args)
    {
        launch(args);
    }
    public void start(Stage window)
    {
        this.window = window;
        display();
    }
    public static void display()
    {

        BorderPane layout = new BorderPane();
        VBox settings = new VBox(10);
        settings.setPadding(new Insets(10));
        settings.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        Text shape = new Text("Shape:");
        ChoiceBox<String> shape2 = new ChoiceBox<>();
        shape2.getItems().addAll("Circle", "Square", "Line");
        shape2.setValue("Circle");
        Text size = new Text("Size:");
        TextField size2 = new TextField("10");
        size2.setMaxWidth(45);
        Text color = new Text("Color:");
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setMaxWidth(45);
        Text transparency = new Text("Transparency:");
        Slider transparency2 = new Slider(0, 1, 1);
        transparency2.setMaxWidth(89);
        transparency2.setShowTickLabels(true);
        transparency2.setShowTickMarks(true);
        transparency2.setMajorTickUnit(.5);


        //TextField transparency2 = new TextField("1");
        //transparency2.setMaxWidth(40);

        Button undo = new Button("Undo");

        Text importImage = new Text("Import Image:");
        TextField importImage2 = new TextField();
        importImage2.setMaxWidth(89);
        Button importImage3 = new Button("Import");
        Button clearImage = new Button("Clear Image");

        Button clear = new Button("Clear");

        settings.getChildren().addAll(shape, shape2, size, size2, color, colorPicker, /*color2, rgb, r, r2, g, g2, b, b2, */transparency, transparency2, undo, importImage, importImage2, importImage3, clearImage, clear);

        Group mainLayout = new Group();

        layout.setCenter(mainLayout);
        layout.setLeft(settings);
        mainLayout.getChildren().add(new Rectangle(width,height,Color.WHITE));

        importImage3.setOnAction(e ->
        {
            try
            {
                //this works http://gomighty.com/wp-content/themes/gomighty/lib/goal_images/files/loan-oak-tree.jpg
                //http://www.thedesignwork.com/wp-content/uploads/2010/08/Night-Paris41.jpg
                //http://static.adweek.com/adweek.com-prod/wp-content/uploads/2017/12/papa-john-ceo-2017-page.jpg
                if(!importImage2.getText().equals(""))
                {
                    image = new ImageView(importImage2.getText());
                }
                else
                {
                    FileChooser fileChooser = new FileChooser();
                    try
                    {
                        Image pic = new Image(new FileInputStream("" + fileChooser.showOpenDialog(window).getAbsolutePath() + ""));
                        image = new ImageView(pic);
                    }
                    catch(FileNotFoundException err)
                    {

                    }
                }
                image.setX(0);
                image.setY(0);
                boolean heightAdg = false;
                boolean widthAdg = false;
                if(image.getImage().getHeight() > 1021)
                {
                    image.setFitHeight(1021);
                    image.setPreserveRatio(true);
                    height = (int)image.getFitHeight();
                    heightAdg = true;
                }
                else if(image.getImage().getWidth() > 1811)
                {
                    image.setFitWidth(1811);
                    image.setPreserveRatio(true);
                    width = (int)image.getFitWidth();
                    widthAdg = true;
                }
                if(heightAdg && !widthAdg)
                {
                    width = (int)((image.getFitHeight() / image.getImage().getHeight()) * image.getImage().getWidth());
                }
                else if(heightAdg && widthAdg || widthAdg)
                {

                }
                else
                {
                    width = (int)image.getImage().getWidth();
                }
                if(!heightAdg && widthAdg)
                {
                    height = (int)((image.getFitWidth() / image.getImage().getWidth()) * image.getImage().getHeight());
                }
                else if(heightAdg && widthAdg || heightAdg)
                {

                }
                else
                {
                    height = (int)image.getImage().getHeight() + 3;
                }
                mainLayout.getChildren().clear();
                mainLayout.getChildren().add(image);
                usingImage = true;
            }
            catch(IllegalArgumentException err)
            {

            }
        });

        clearImage.setOnAction(e ->
        {
            usingImage = false;
            importImage2.setText("");
            height = 1021;
            width = 1811;
            mainLayout.getChildren().clear();
            mainLayout.getChildren().add(new Rectangle(width,height,Color.WHITE));
            undoLog = new ArrayList<>();
        });

        clear.setOnAction(e ->
        {
            mainLayout.getChildren().clear();
            if(usingImage)
            {
                mainLayout.getChildren().add(image);
            }
            else
            {
                mainLayout.getChildren().add(new Rectangle(width,height,Color.WHITE));
            }
            undoLog = new ArrayList<>();
        });

        undo.setOnAction(e ->
        {
            if(undoLog.size() > 0)
            {
                for(int x = undoLog.get(undoLog.size() - 1).size() - 1; x >= 0; x--)
                {
                    mainLayout.getChildren().remove(undoLog.get(undoLog.size() - 1).get(x));
                }
                undoLog.remove(undoLog.size() - 1);
            }

        });

        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(e ->
        {
            switch (e.getCode())
            {
                case CONTROL: control = true; break;
                case Z: z = true; break;
                case ESCAPE: esc = true; break;
            }
        });
        scene.setOnKeyReleased(e ->
        {
            switch (e.getCode())
            {
                case CONTROL: control = false; break;
                case Z: z = false; break;
                case ESCAPE: esc = false; break;
            }
        });
        window.setScene(scene);
        window.setMaximized(true);
        window.setTitle("Drawing Program - Andrew Frank");
        window.show();
        mainLayout.setOnMouseDragged(e ->
        {
            if(shape2.getValue().equals("Circle"))
            {
                Line line = new Line();
                Circle cursorShape = new Circle();
                try
                {
                    cursorShape = new Circle(Integer.parseInt(size2.getText()));
                }
                catch(NumberFormatException err)
                {

                }
                cursorShape.setFill(colorPicker.getValue());
                cursorShape.setOpacity(transparency2.getValue());
                line.setStroke(cursorShape.getFill());
                line.setOpacity(transparency2.getValue());
                try
                {
                    if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(0 + Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(0 + Integer.parseInt(size2.getText()));
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(0 + Integer.parseInt(size2.getText()));
                            line.setEndY(0 + Integer.parseInt(size2.getText()));

                        }
                        lastX = 0 + Integer.parseInt(size2.getText());
                        lastY = 0 + Integer.parseInt(size2.getText());
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                    {
                        cursorShape.setCenterX(width - Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(height - Integer.parseInt(size2.getText()) + -3);
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(width - Integer.parseInt(size2.getText()));
                            line.setEndY(height - Integer.parseInt(size2.getText()) + -3);

                        }
                        lastX = width - Integer.parseInt(size2.getText());
                        lastY = height - Integer.parseInt(size2.getText()) + -3;
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                    {
                        cursorShape.setCenterX(Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(height - Integer.parseInt(size2.getText()) + -3);
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(Integer.parseInt(size2.getText()));
                            line.setEndY(height - Integer.parseInt(size2.getText()) + -3);

                        }
                        lastX = Integer.parseInt(size2.getText());
                        lastY = height - Integer.parseInt(size2.getText()) + -3;
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(width - Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(Integer.parseInt(size2.getText()));
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(width - Integer.parseInt(size2.getText()));
                            line.setEndY(Integer.parseInt(size2.getText()));

                        }
                        lastX = width - Integer.parseInt(size2.getText());
                        lastY = Integer.parseInt(size2.getText());
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(width - Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(e.getY());
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(width - Integer.parseInt(size2.getText()));
                            line.setEndY(e.getY());

                        }
                        lastX = width - Integer.parseInt(size2.getText());
                        lastY = (int)e.getY();
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(0 + Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(e.getY());
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(0 + Integer.parseInt(size2.getText()));
                            line.setEndY(e.getY());

                        }
                        lastX = 0 + Integer.parseInt(size2.getText());
                        lastY = (int)e.getY();
                    }
                    else if(e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                    {
                        cursorShape.setCenterX(e.getX());
                        cursorShape.setCenterY(height - Integer.parseInt(size2.getText()) + -3);
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(e.getX());
                            line.setEndY(height - Integer.parseInt(size2.getText()) + -3);

                        }
                        lastX = (int)e.getX();
                        lastY = height - Integer.parseInt(size2.getText()) + -3;
                    }
                    else if(e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(e.getX());
                        cursorShape.setCenterY(0 + Integer.parseInt(size2.getText()));
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(e.getX());
                            line.setEndY(0 + Integer.parseInt(size2.getText()));

                        }
                        lastX = (int)e.getX();
                        lastY = 0 + Integer.parseInt(size2.getText());
                    }
                    else
                    {
                        cursorShape.setCenterX(e.getX());
                        cursorShape.setCenterY(e.getY());
                        if(lastX == -1 && lastY == -1)
                        {

                        }
                        else
                        {
                            line.setStartX(lastX);
                            line.setStartY(lastY);
                            line.setEndX(e.getX());
                            line.setEndY(e.getY());

                        }
                        lastX = (int)e.getX();
                        lastY = (int)e.getY();
                    }
                    line.setStrokeWidth(2 * Integer.parseInt(size2.getText()));
                    line.setStrokeLineCap(StrokeLineCap.ROUND);
                    line.setSmooth(true);
                }
                catch(NumberFormatException err)
                {

                }
                if(Math.sqrt(Math.pow(line.getStartX() - line.getEndX(), 2) + Math.pow(line.getStartY() - line.getEndY(), 2)) >= .5 * Integer.parseInt(size2.getText()))
                {
                    if(noLine)
                    {
                        mainLayout.getChildren().add(cursorShape);
                        strokeLog.add(cursorShape);
                    }
                    else
                    {
                        mainLayout.getChildren().add(line);
                        strokeLog.add(line);
                    }
                }
                else
                {
                    mainLayout.getChildren().add(cursorShape);
                    strokeLog.add(cursorShape);
                }
            }
            else if(shape2.getValue().equals("Square"))
            {
                Line line = new Line();
                Rectangle cursorShape = new Rectangle();
                try
                {
                    cursorShape = new Rectangle(2 * Integer.parseInt(size2.getText()), 2 * Integer.parseInt(size2.getText()));
                }
                catch(NumberFormatException err)
                {

                }
                cursorShape.setFill(colorPicker.getValue());
                cursorShape.setOpacity(transparency2.getValue());
                line.setStroke(cursorShape.getFill());
                line.setOpacity(transparency2.getValue());
                try
                {
                    if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(0);
                        cursorShape.setY(0);
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) - 3 && !spaceMode)
                    {
                        cursorShape.setX(width - (2 * Integer.parseInt(size2.getText())));
                        cursorShape.setY(height - (2 * Integer.parseInt(size2.getText())) - 3);
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(width - (2 * Integer.parseInt(size2.getText())));
                        cursorShape.setY(0);
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) - 3 && !spaceMode)
                    {
                        cursorShape.setX(0);
                        cursorShape.setY(height - (2 * Integer.parseInt(size2.getText())) - 3);
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(0);
                        cursorShape.setY(e.getY() - Integer.parseInt(size2.getText()));
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(width - (2 * Integer.parseInt(size2.getText())));
                        cursorShape.setY(e.getY() - Integer.parseInt(size2.getText()));
                    }
                    else if(e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(e.getX() - Integer.parseInt(size2.getText()));
                        cursorShape.setY(0);
                    }
                    else if(e.getY() > height - Integer.parseInt(size2.getText()) - 3 && !spaceMode)
                    {
                        cursorShape.setX(e.getX() - Integer.parseInt(size2.getText()));
                        cursorShape.setY(height - (2 * Integer.parseInt(size2.getText())) - 3);
                    }
                    else
                    {
                        cursorShape.setX(e.getX() - Integer.parseInt(size2.getText()));
                        cursorShape.setY(e.getY() - Integer.parseInt(size2.getText()));
                    }
                    if(lastX == -1 && lastY == -1)
                    {

                    }
                    else
                    {
                        line.setStartX(lastX);
                        line.setStartY(lastY);
                        line.setEndX(cursorShape.getX() + Integer.parseInt(size2.getText()));
                        line.setEndY(cursorShape.getY() + Integer.parseInt(size2.getText()));
                    }
                    lastX = (int)cursorShape.getX() + Integer.parseInt(size2.getText());
                    lastY = (int)cursorShape.getY() + Integer.parseInt(size2.getText());
                }
                catch(NumberFormatException err)
                {

                }
                line.setStrokeWidth(2 * Integer.parseInt(size2.getText()));
                line.setStrokeLineCap(StrokeLineCap.BUTT);
                if(Math.sqrt(Math.pow(line.getStartX() - line.getEndX(), 2) + Math.pow(line.getStartY() - line.getEndY(), 2)) >= Integer.parseInt(size2.getText()))
                {
                    if(noLine)
                    {
                        mainLayout.getChildren().add(cursorShape);
                        strokeLog.add(cursorShape);
                    }
                    else
                    {
                        mainLayout.getChildren().add(line);
                        strokeLog.add(line);
                        mainLayout.getChildren().add(cursorShape);
                        strokeLog.add(cursorShape);
                    }
                }
                else
                {
                    mainLayout.getChildren().add(cursorShape);
                    strokeLog.add(cursorShape);
                }
            }
            else
            {
                line = new Line();
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                line.setStroke(colorPicker.getValue());
                line.setOpacity(transparency2.getValue());
                line.setStrokeWidth(2 * Integer.parseInt(size2.getText()));
                line.setStartX(lastX);
                line.setStartY(lastY);


                int x;
                int y;
                if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (0 + Integer.parseInt(size2.getText()));
                    y = (0 + Integer.parseInt(size2.getText()));
                }
                else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                {
                    x = (width - Integer.parseInt(size2.getText()));
                    y = (height - Integer.parseInt(size2.getText()) + -3);
                }
                else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                {
                    x = (Integer.parseInt(size2.getText()));
                    y = (height - Integer.parseInt(size2.getText()) + -3);
                }
                else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (width - Integer.parseInt(size2.getText()));
                    y = (Integer.parseInt(size2.getText()));
                }
                else if(e.getX() > width - Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (width - Integer.parseInt(size2.getText()));
                    y = (int)(e.getY());
                }
                else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (0 + Integer.parseInt(size2.getText()));
                    y = (int)(e.getY());
                }
                else if(e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                {
                    x = (int)(e.getX());
                    y = (height - Integer.parseInt(size2.getText()) + -3);
                }
                else if(e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (int)(e.getX());
                    y = (0 + Integer.parseInt(size2.getText()));
                }
                else
                {
                    x = (int)(e.getX());
                    y = (int)(e.getY());
                }



                line.setEndX(x);
                line.setEndY(y);
                mainLayout.getChildren().add(line);
                strokeLog.add(line);
                if(strokeLog.size() > 1)
                {
                    mainLayout.getChildren().remove(strokeLog.get(0));
                    strokeLog.remove(0);
                }
            }
        });
        /*
        mainLayout.setOnMouseDragExited(e ->
        {
            undoLog.add(strokeLog);
            strokeLog = new ArrayList<>();
            lastX = -1;
            lastY = -1;
            System.out.println("x:" + lastX + " y: " + lastY);
        });*/
        mainLayout.setOnMousePressed(e ->
        {
            if(shape2.getValue().equals("Circle"))
            {
                Circle cursorShape = new Circle();
                try
                {
                    cursorShape = new Circle(Integer.parseInt(size2.getText()));
                }
                catch(NumberFormatException err)
                {

                }
                cursorShape.setFill(colorPicker.getValue());
                cursorShape.setOpacity(transparency2.getValue());
                mainLayout.getChildren().add(cursorShape);
                strokeLog.add(cursorShape);
                try
                {
                    if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(0 + Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(0 + Integer.parseInt(size2.getText()));
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                    {
                        cursorShape.setCenterX(width - Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(height - Integer.parseInt(size2.getText()) + -3);
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                    {
                        cursorShape.setCenterX(Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(height - Integer.parseInt(size2.getText()) + -3);
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(width - Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(Integer.parseInt(size2.getText()));
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(width - Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(e.getY());
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(0 + Integer.parseInt(size2.getText()));
                        cursorShape.setCenterY(e.getY());
                    }
                    else if(e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                    {
                        cursorShape.setCenterX(e.getX());
                        cursorShape.setCenterY(height - Integer.parseInt(size2.getText()) + -3);
                    }
                    else if(e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setCenterX(e.getX());
                        cursorShape.setCenterY(0 + Integer.parseInt(size2.getText()));
                    }
                    else
                    {
                        cursorShape.setCenterX(e.getX());
                        cursorShape.setCenterY(e.getY());
                    }
                }
                catch(NumberFormatException err)
                {

                }
                lastX = (int)cursorShape.getCenterX();
                lastY = (int)cursorShape.getCenterY();
            }
            else if(shape2.getValue().equals("Square"))
            {
                Rectangle cursorShape = new Rectangle();
                try
                {
                    cursorShape = new Rectangle(2 * Integer.parseInt(size2.getText()), 2 * Integer.parseInt(size2.getText()));
                }
                catch(NumberFormatException err)
                {

                }
                cursorShape.setFill(colorPicker.getValue());
                cursorShape.setOpacity(transparency2.getValue());
                mainLayout.getChildren().add(cursorShape);
                strokeLog.add(cursorShape);
                try
                {
                    if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(0);
                        cursorShape.setY(0);
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) - 3 && !spaceMode)
                    {
                        cursorShape.setX(width - (2 * Integer.parseInt(size2.getText())));
                        cursorShape.setY(height - (2 * Integer.parseInt(size2.getText())) - 3);
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(width - (2 * Integer.parseInt(size2.getText())));
                        cursorShape.setY(0);
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) - 3 && !spaceMode)
                    {
                        cursorShape.setX(0);
                        cursorShape.setY(height - (2 * Integer.parseInt(size2.getText())) - 3);
                    }
                    else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(0);
                        cursorShape.setY(e.getY() - Integer.parseInt(size2.getText()));
                    }
                    else if(e.getX() > width - Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(width - (2 * Integer.parseInt(size2.getText())));
                        cursorShape.setY(e.getY() - Integer.parseInt(size2.getText()));
                    }
                    else if(e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                    {
                        cursorShape.setX(e.getX() - Integer.parseInt(size2.getText()));
                        cursorShape.setY(0);
                    }
                    else if(e.getY() > height - Integer.parseInt(size2.getText()) - 3 && !spaceMode)
                    {
                        cursorShape.setX(e.getX() - Integer.parseInt(size2.getText()));
                        cursorShape.setY(height - (2 * Integer.parseInt(size2.getText())) - 3);
                    }
                    else
                    {
                        cursorShape.setX(e.getX() - Integer.parseInt(size2.getText()));
                        cursorShape.setY(e.getY() - Integer.parseInt(size2.getText()));
                    }
                }
                catch(NumberFormatException err)
                {

                }
                lastX = (int)cursorShape.getX() + Integer.parseInt(size2.getText());
                lastY = (int)cursorShape.getY() + Integer.parseInt(size2.getText());
            }
            else
            {
                int x;
                int y;
                if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (0 + Integer.parseInt(size2.getText()));
                    y = (0 + Integer.parseInt(size2.getText()));
                }
                else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                {
                    x = (width - Integer.parseInt(size2.getText()));
                    y = (height - Integer.parseInt(size2.getText()) + -3);
                }
                else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                {
                    x = (Integer.parseInt(size2.getText()));
                    y = (height - Integer.parseInt(size2.getText()) + -3);
                }
                else if(e.getX() > width - Integer.parseInt(size2.getText()) && e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (width - Integer.parseInt(size2.getText()));
                    y = (Integer.parseInt(size2.getText()));
                }
                else if(e.getX() > width - Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (width - Integer.parseInt(size2.getText()));
                    y = (int)(e.getY());
                }
                else if(e.getX() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (0 + Integer.parseInt(size2.getText()));
                    y = (int)(e.getY());
                }
                else if(e.getY() > height - Integer.parseInt(size2.getText()) + -3 && !spaceMode)
                {
                    x = (int)(e.getX());
                    y = (height - Integer.parseInt(size2.getText()) + -3);
                }
                else if(e.getY() < 0 + Integer.parseInt(size2.getText()) && !spaceMode)
                {
                    x = (int)(e.getX());
                    y = (0 + Integer.parseInt(size2.getText()));
                }
                else
                {
                    x = (int)(e.getX());
                    y = (int)(e.getY());
                }
                lastX = x;
                lastY = y;
            }
        });
        mainLayout.setOnMouseReleased(e ->
        {
            undoLog.add(strokeLog);
            strokeLog = new ArrayList<>();
            lastX = -1;
            lastY = -1;
        });
    }
}