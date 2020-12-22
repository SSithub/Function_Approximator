package functionapproximator;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Components {

    //Important information that is relied on by components
    public static final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    public static final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    public static final double chartWidth = screenWidth * 2 / 3;
    public static final double chartHeight = screenHeight * 2 / 3;

    public static void createShowFunction(NNFunction function, Stage functionStage) {
        Text activationFunction = new Text("t(x) = (e^x - e^-x)/(e^x + e^-x)");
        activationFunction.setTranslateY(25);
        TextArea functionTextField = new TextArea();
        functionTextField.setPrefSize(800, 750);
        functionTextField.setTranslateY(50);
        functionTextField.setText(function.toString());
        Group window = new Group();
        window.getChildren().addAll(functionTextField, activationFunction);
        Scene functionWindow = new Scene(window, 800, 800);
        try {
            functionStage = new Stage();
            functionStage.setScene(functionWindow);
        } catch (Exception e) {
            functionStage.setScene(functionWindow);
        }
        functionStage.show();
    }

    public static ScatterChart createChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        ScatterChart chart = new ScatterChart(xAxis, yAxis);
        chart.setPrefSize(chartWidth, chartHeight);
        XYChart.Series<Number, Number> functionData = new XYChart.Series<>();
        functionData.setName("Data");
        XYChart.Series<Number, Number> functionLine = new XYChart.Series<>();
        functionLine.setName("Function");
        chart.getData().addAll(functionData, functionLine);
        chart.setAnimated(false);
        return chart;
    }

    public static VBox createInputBox(XYChart.Series<Number, Number> data) {
        TilePane insertPointBox = new TilePane();
        Text xLabel = new Text("X Value");
        Text yLabel = new Text("Y Value");
        TextField xInput = new TextField();
        TextField yInput = new TextField();
        xInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    double x = Double.parseDouble(xInput.getText());
                    double y = Double.parseDouble(yInput.getText());
                    data.getData().add(new XYChart.Data<>(x, y));
                } catch (NumberFormatException e) {
                }
            }
        });
        yInput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    double x = Double.parseDouble(xInput.getText());
                    double y = Double.parseDouble(yInput.getText());
                    data.getData().add(new XYChart.Data<>(x, y));
                } catch (NumberFormatException e) {
                }
            }
        });
        insertPointBox.getChildren().addAll(xLabel, xInput, yLabel, yInput);
        insertPointBox.setHgap(-60);
        insertPointBox.setMaxSize(400, 0);
        //Add a title
        Text titleLabel = new Text("Insert Point");
        VBox inputBox = new VBox(titleLabel, insertPointBox);
        //Translations
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setSpacing(10);
        inputBox.setTranslateX(-51);
        inputBox.setTranslateY(chartHeight);
        return inputBox;
    }

    public static HBox createTrainingBox(NNFunction function, XYChart.Series<Number, Number> functionData, XYChart.Series<Number, Number> functionLine, NumberAxis xAxis) {
        HBox train = new HBox();
        train.setSpacing(10);
        Text sessionsLabel = new Text("Sessions");
        TextField sessionsText = new TextField();
        sessionsText.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    for (int i = 0; i < Integer.parseInt(sessionsText.getText()); i++) {
                        int random = (int) (Math.random() * functionData.getData().size());
                        float x = functionData.getData().get(random).getXValue().floatValue();
                        float y = functionData.getData().get(random).getYValue().floatValue();
                        function.train(x, y);
                    }
                } catch (Exception e) {
                }
                functionLine.getData().clear();
                double xLower = xAxis.getLowerBound();
                double xUpper = xAxis.getUpperBound();
                double increment = (xUpper - xLower) / 500;
                for (double i = xLower + 10 * increment; i < xUpper - 10 * increment; i += increment) {
                    float approxY = function.evaluate((float) i);
                    functionLine.getData().add(new XYChart.Data<>(i, approxY));
                }
            }
        });
        train.getChildren().addAll(sessionsLabel, sessionsText);
        //Translations
        train.setTranslateX(10);
        return train;
    }

    public static Button createGetFunctionButton(NNFunction function, Stage functionStage) {
        Button getFunctionButton = new Button("Get Function");
        getFunctionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createShowFunction(function, functionStage);
            }
        });
        //Translations
        getFunctionButton.setTranslateX(265);
        return getFunctionButton;
    }

    public static Group createTrainingRelatedGroup(NNFunction function, Stage functionStage, XYChart.Series<Number, Number> functionData, XYChart.Series<Number, Number> functionLine, NumberAxis xAxis) {
        Group group = new Group(createTrainingBox(function, functionData, functionLine, xAxis), createGetFunctionButton(function, functionStage));
        //Translations
        group.setTranslateY(chartHeight + 135);
        return group;
    }

    public static VBox createDebugValueBox(NNFunction function) {//For debugging
        VBox view = new VBox();
        Text inputVar = new Text("Input an X value");
        TextField inputVarField = new TextField();
        inputVarField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    float x = Float.parseFloat(inputVarField.getText());
                    System.out.println(function.evaluate(x));
                } catch (Exception e) {
                }
            }
        });
        view.getChildren().addAll(inputVar, inputVarField);
        view.setTranslateX(1100);
        return view;
    }

    public static Timeline createUpdateContinuouslyLoop(NNFunction function, XYChart.Series<Number, Number> functionData, XYChart.Series<Number, Number> functionLine, NumberAxis xAxis) {
        Timeline update = new Timeline(new KeyFrame(Duration.millis(16), eh -> {
            try {
                for (int i = 0; i < 50; i++) {
                    int random = (int) (Math.random() * functionData.getData().size());
                    float x = functionData.getData().get(random).getXValue().floatValue();
                    float y = functionData.getData().get(random).getYValue().floatValue();
                    function.train(x, y);
                }
            } catch (Exception e) {
            }
            functionLine.getData().clear();
            double xLower = xAxis.getLowerBound();
            double xUpper = xAxis.getUpperBound();
            double increment = (xUpper - xLower) / 500;
            for (double i = xLower + 10 * increment; i < xUpper - 10 * increment; i += increment) {
                float approxY = function.evaluate((float) i);
                functionLine.getData().add(new XYChart.Data<>(i, approxY));
            }
        }));
        update.setCycleCount(-1);
        return update;
    }

    public static HBox createUpdateLoopCheckBox(Timeline updateLoop) {
        CheckBox checkBox = new CheckBox();
        checkBox.setTranslateX(100);
        checkBox.setOnAction((t) -> {
            if (checkBox.isSelected()) {//If checked
                updateLoop.play();
            } else {
                updateLoop.stop();
            }
        });
        Text label = new Text("Train Continuously");
        HBox checkBoxWithLabel = new HBox(label, checkBox);
        //Translations
        checkBoxWithLabel.setSpacing(-95);
        checkBoxWithLabel.setTranslateX(400);
        checkBoxWithLabel.setTranslateY(chartHeight + 140);
        return checkBoxWithLabel;
    }

    //Helpers
    private static double[][] generatePointsFromFunction(Function1D fx, int numberOfPoints, double center, double increment) {
        double start = -(numberOfPoints / 2.0) * increment + center;
        double[][] points = new double[numberOfPoints][2];
        for (int i = 0; i < numberOfPoints; i++) {
            double x = start + i * increment;
            points[i][0] = x;
            double y = fx.evaluate(x);
            points[i][1] = y;
        }
        return points;
    }

    private static void addData(XYChart.Series<Number, Number> data, double[][] points) {
        int size = points.length;
        for (int i = 0; i < size; i++) {
            double[] point = points[i];
            data.getData().add(new XYChart.Data<>(point[0], point[1]));
        }
    }

    public static VBox createDataBox(XYChart.Series<Number, Number> functionData, XYChart.Series<Number, Number> functionLine) {
        Text numberOfPointsLabel = new Text("Number of points");
        TextField numberOfPointsField = new TextField();
        numberOfPointsField.setText("1000");
        Text centerLabel = new Text("Center");
        TextField centerField = new TextField();
        centerField.setText("0");
        Text gapBetweenPointsLabel = new Text("Gap between points");
        TextField gapBetweenPointsField = new TextField();
        gapBetweenPointsField.setText(".1");
        TilePane parameterFields = new TilePane(numberOfPointsLabel, numberOfPointsField, centerLabel, centerField, gapBetweenPointsLabel, gapBetweenPointsField);
        parameterFields.setPrefColumns(2);
        parameterFields.setPrefRows(3);
        parameterFields.setHgap(-24);
        parameterFields.setTranslateX(-35);

        HashMap<String, Function1D> namesWithFunctions = new HashMap();
        namesWithFunctions.put("Sine", new Function1D((x) -> Math.sin(x)));
        namesWithFunctions.put("Cosine", new Function1D((x) -> Math.cos(x)));
        namesWithFunctions.put("Arctan", new Function1D((x) -> Math.atan(x)));
        namesWithFunctions.put("Tanh", new Function1D((x) -> Math.tanh(x)));
        namesWithFunctions.put("Sigmoid", new Function1D((x) -> 1 / (1 + Math.exp(-2 * x))));
        namesWithFunctions.put("x", new Function1D((x) -> Math.pow(x, 1)));
        namesWithFunctions.put("x^2", new Function1D((x) -> Math.pow(x, 2)));
        namesWithFunctions.put("x^3", new Function1D((x) -> Math.pow(x, 3)));
        ComboBox functions = new ComboBox();
        functions.getItems().addAll("None", "Sine", "Cosine", "Arctan", "Tanh", "Sigmoid", "x", "x^2", "x^3");
        functions.setValue("None");
        functions.setOnAction((t) -> {
            functionData.getData().clear();
            functionLine.getData().clear();
            boolean invalidField = false;
            try {
                Integer.parseInt(numberOfPointsField.getText());
                numberOfPointsLabel.setFill(Color.BLACK);
            } catch (NumberFormatException e) {
                numberOfPointsLabel.setFill(Color.RED);
                invalidField = true;
            }
            try {
                Double.parseDouble(centerField.getText());
                centerLabel.setFill(Color.BLACK);
            } catch (NumberFormatException e) {
                centerLabel.setFill(Color.RED);
                invalidField = true;
            }
            try {
                Double.parseDouble(gapBetweenPointsField.getText());
                gapBetweenPointsLabel.setFill(Color.BLACK);
            } catch (NumberFormatException e) {
                gapBetweenPointsLabel.setFill(Color.RED);
                invalidField = true;
            }
            if (functions.getValue() != "None") {
                Function1D desiredFunction = namesWithFunctions.get(functions.getValue());
                if (!invalidField) {
                    addData(functionData, generatePointsFromFunction(desiredFunction,
                            Integer.parseInt(numberOfPointsField.getText()),
                            Double.parseDouble(centerField.getText()),
                            Double.parseDouble(gapBetweenPointsField.getText()))
                    );
                }
            }
        });

        Text databoxLabel = new Text("Create Preset Data (will clear current points)");

        VBox dataBox = new VBox(databoxLabel, functions, parameterFields);
        //Translations
        dataBox.setTranslateX(chartWidth + 10);
        return dataBox;
    }
}
