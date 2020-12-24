package functionapproximator;

import functionapproximator.NNlib.Activations;
import functionapproximator.NNlib.Activations.Activation;
import functionapproximator.NNlib.LossFunctions;
import functionapproximator.NNlib.LossFunctions.LossFunction;
import functionapproximator.NNlib.Optimizers;
import functionapproximator.NNlib.Optimizers.Optimizer;
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
import javafx.scene.text.Font;
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
    private static int numberOfSamples = 100;

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
        xAxis.setLowerBound(-30);
        xAxis.setUpperBound(30);
        return chart;
    }

    public static VBox createInputBox(XYChart.Series<Number, Number> data) {
        TilePane insertPointBox = new TilePane();
        Text xLabel = new Text("X Value");
        Text yLabel = new Text("Y Value");
        TextField xInput = new TextField();
        TextField yInput = new TextField();
        xInput.setOnAction((t) -> {
            try {
                double x = Double.parseDouble(xInput.getText());
                double y = Double.parseDouble(yInput.getText());
                data.getData().add(new XYChart.Data<>(x, y));
            } catch (NumberFormatException e) {
            }
        });
        yInput.setOnAction((t) -> {
            try {
                double x = Double.parseDouble(xInput.getText());
                double y = Double.parseDouble(yInput.getText());
                data.getData().add(new XYChart.Data<>(x, y));
            } catch (NumberFormatException e) {
            }
        });
        insertPointBox.getChildren().addAll(xLabel, xInput, yLabel, yInput);
        insertPointBox.setHgap(-60);
        insertPointBox.setMaxSize(400, 0);
        //Add a title
        Text titleLabel = new Text("Insert Point");
        VBox inputBox = new VBox(titleLabel, insertPointBox);
        //Adjustments
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
        //Adjustments
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
        //Adjustments
        getFunctionButton.setTranslateX(265);
        return getFunctionButton;
    }

    public static Group createTrainingRelatedGroup(NNFunction function, Stage functionStage, XYChart.Series<Number, Number> functionData, XYChart.Series<Number, Number> functionLine, NumberAxis xAxis) {
        Group group = new Group(createTrainingBox(function, functionData, functionLine, xAxis), createGetFunctionButton(function, functionStage));
        //Adjustments
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
        Timeline update = new Timeline(new KeyFrame(Duration.millis(50), eh -> {
            try {
                for (int i = 0; i < numberOfSamples; i++) {
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
            double increment = (xUpper - xLower) / 700;
            for (double i = xLower + 10 * increment; i < xUpper - 10 * increment; i += increment) {
                float approxY = function.evaluate((float) i);
                functionLine.getData().add(new XYChart.Data<>(i, approxY));
            }
        }));
        update.setCycleCount(-1);
        return update;
    }

    public static Group createUpdateLoopCheckBox(Timeline updateLoop) {
        CheckBox checkBox = new CheckBox();
        checkBox.setTranslateX(100);
        checkBox.setOnAction((t) -> {
            if (checkBox.isSelected()) {//If checked
                updateLoop.play();
            } else {
                updateLoop.stop();
            }
        });
        TextField rateField = new TextField("100");
        Text rateLabel = new Text("# of samples to train on every 50ms");
        Text label = new Text("Train Continuously");
        Group checkBoxWithLabel = new Group(label, checkBox, rateLabel, rateField);
        rateField.setOnAction((t) -> {
            if (isFieldParsableInt(rateField, rateLabel)) {
                numberOfSamples = Integer.parseInt(rateField.getText());
            }
        });
        //Adjustments
        checkBox.setTranslateX(130);
        checkBox.setTranslateY(-15);
        rateLabel.setTranslateY(20);
        rateField.setTranslateX(240);
        checkBoxWithLabel.setTranslateX(450);
        checkBoxWithLabel.setTranslateY(chartHeight + 145);
        return checkBoxWithLabel;
    }

    public static VBox createDataBox(XYChart.Series<Number, Number> functionData, XYChart.Series<Number, Number> functionLine) {
        Text numberOfPointsLabel = new Text("Number of points");
        TextField numberOfPointsField = new TextField("500");//default
        Text centerLabel = new Text("Center");
        TextField centerField = new TextField("0");//default
        Text gapBetweenPointsLabel = new Text("Gap between points");
        TextField gapBetweenPointsField = new TextField(".1");//default
        TilePane parameterFields = new TilePane(numberOfPointsLabel, numberOfPointsField, centerLabel, centerField, gapBetweenPointsLabel, gapBetweenPointsField);
        parameterFields.setPrefColumns(2);
        parameterFields.setPrefRows(3);
        parameterFields.setHgap(-24);
        parameterFields.setTranslateX(-35);

        HashMap<String, Function1D> functions = new HashMap();
        functions.put("Sine", new Function1D((x) -> Math.sin(x)));
        functions.put("Cosine", new Function1D((x) -> Math.cos(x)));
        functions.put("Arctan", new Function1D((x) -> Math.atan(x)));
        functions.put("Tanh", new Function1D((x) -> Math.tanh(x)));
        functions.put("Sigmoid", new Function1D((x) -> 1 / (1 + Math.exp(-2 * x))));
        functions.put("x", new Function1D((x) -> Math.pow(x, 1)));
        functions.put("x^2", new Function1D((x) -> Math.pow(x, 2)));
        functions.put("x^3", new Function1D((x) -> Math.pow(x, 3)));
        ComboBox functionsBox = new ComboBox();
        functionsBox.getItems().addAll("None", "Sine", "Cosine", "Arctan", "Tanh", "Sigmoid", "x", "x^2", "x^3");
        functionsBox.setValue("None");
        functionsBox.setOnHiding((t) -> {
            functionData.getData().clear();
            functionLine.getData().clear();
            boolean validFields = andAll(isFieldParsableInt(numberOfPointsField, numberOfPointsLabel),
                    isFieldParsableDouble(centerField, centerLabel),
                    isFieldParsableDouble(gapBetweenPointsField, gapBetweenPointsLabel));
            if (functionsBox.getValue() != "None" && validFields) {
                Function1D desiredFunction = functions.get(functionsBox.getValue());
                addData(functionData, generatePointsFromFunction(desiredFunction,
                        Integer.parseInt(numberOfPointsField.getText()),
                        Double.parseDouble(centerField.getText()),
                        Double.parseDouble(gapBetweenPointsField.getText()))
                );
            }
        });

        Text databoxLabel = new Text("Create Preset Data (will clear current points)");

        VBox dataBox = new VBox(databoxLabel, functionsBox, parameterFields);
        //Adjustments
        dataBox.setTranslateX(chartWidth + 10);
        return dataBox;
    }

    public static VBox createHyperParameterBox(NNFunction nnfunction) {
        //Labels
        Text seedLabel = new Text("Seed");
        Text hiddenLayerNodesLabel = new Text("Hidden Layer Nodes");
        Text activationLabel = new Text("Activation Function");
        Text lossFunctionLabel = new Text("Loss Function");
        Text learningRateLabel = new Text("Learning Rate");
        Text optimizerLabel = new Text("Optimizer");
        //Method of inputting parameters
        TextField seedInput = new TextField("0");
        TextField hiddenLayerNodesInput = new TextField("10");
        ComboBox activationInput = new ComboBox();
        ComboBox lossFunctionInput = new ComboBox();
        TextField learningRateInput = new TextField(".001");
        ComboBox optimizerInput = new ComboBox();
        activationInput.setValue("Sigmoid");
        lossFunctionInput.setValue("Quadratic");
        optimizerInput.setValue("Vanilla");
        //Maps for ComboBoxes
        HashMap<String, Activation> activations = new HashMap<>();
        activations.put("Sigmoid", Activations.SIGMOID);
        activations.put("Tanh", Activations.TANH);
        activations.put("ReLU", Activations.RELU);
        activations.put("LeakyReLU", Activations.RELU);
        activations.put("Swish", Activations.SWISH);
        activations.put("Mish", Activations.MISH);
        HashMap<String, LossFunction> lossFunctions = new HashMap<>();
        lossFunctions.put("Quadratic", LossFunctions.QUADRATIC(.5));
        lossFunctions.put("Huber", LossFunctions.HUBER(.5));
        lossFunctions.put("HuberPseudo", LossFunctions.HUBERPSEUDO(.5));
        HashMap<String, Optimizer> optimizers = new HashMap<>();
        optimizers.put("Vanilla", Optimizers.VANILLA);
        optimizers.put("Momentum", Optimizers.MOMENTUM);
        optimizers.put("Nesterov", Optimizers.NESTEROV);
        optimizers.put("AdaGrad", Optimizers.ADAGRAD);
        optimizers.put("RMSProp", Optimizers.RMSPROP);
        optimizers.put("AdaDelta", Optimizers.ADADELTA);
        optimizers.put("Adam", Optimizers.ADAM);
        optimizers.put("NAdam", Optimizers.NADAM);
        optimizers.put("AdaMax", Optimizers.ADAMAX);
        optimizers.put("AMSGrad", Optimizers.AMSGRAD);
        activationInput.getItems().addAll("Sigmoid", "Tanh", "ReLU", "LeakyReLU", "Swish", "Mish");
        lossFunctionInput.getItems().addAll("Quadratic", "Huber", "HuberPseudo");
        optimizerInput.getItems().addAll("Vanilla", "Momentum", "Nesterov", "AdaGrad", "RMSProp", "AdaDelta", "Adam", "NAdam", "AdaMax", "AMSGrad");
        TilePane hyperParameterTiles = new TilePane(seedLabel, hiddenLayerNodesLabel, activationLabel, lossFunctionLabel, learningRateLabel, optimizerLabel,
                seedInput, hiddenLayerNodesInput, activationInput, lossFunctionInput, learningRateInput, optimizerInput
        );
        Button newNNFunctionButton = new Button("Create New Neural Network From Selected Hyperparameters");
        Text info = new Text(" (Learning rate and loss function can be changed anytime)");
        HBox newNNFunctionBox = new HBox(newNNFunctionButton, info);
        VBox hyperParameterBox = new VBox(hyperParameterTiles, newNNFunctionBox);
        //Actions
        learningRateInput.setOnAction((t) -> {
            if (isFieldParsableDouble(learningRateInput, learningRateLabel)) {
                nnfunction.setLearningRateDirectly(Float.parseFloat(learningRateInput.getText()));
            }
        });
        lossFunctionInput.setOnHiding((t) -> {
            nnfunction.setLossFunction(lossFunctions.get(lossFunctionInput.getValue()));
        });
        newNNFunctionButton.setOnAction((t) -> {
            if (andAll(isFieldParsableLong(seedInput, seedLabel),
                    isFieldParsableInt(hiddenLayerNodesInput, hiddenLayerNodesLabel),
                    isFieldParsableFloat(learningRateInput, learningRateLabel))) {
                nnfunction.setHyperParameters(Long.parseLong(seedInput.getText()),
                        Integer.parseInt(hiddenLayerNodesInput.getText()),
                        activations.get(activationInput.getValue()),
                        lossFunctions.get(lossFunctionInput.getValue()),
                        Float.parseFloat(learningRateInput.getText()),
                        optimizers.get(optimizerInput.getValue())
                );
            }
        });
        //Adjustments
        info.setFont(Font.font(23));
        double prefWidth = 150;
        seedInput.setMaxWidth(prefWidth);
        hiddenLayerNodesInput.setMaxWidth(prefWidth);
        activationInput.setMaxWidth(prefWidth);
        lossFunctionInput.setMaxWidth(prefWidth);
        learningRateInput.setMaxWidth(prefWidth);
        optimizerInput.setMaxWidth(prefWidth);
        hyperParameterTiles.setPrefRows(2);
        hyperParameterTiles.setPrefColumns(6);
        hyperParameterBox.setTranslateX(300);
        hyperParameterBox.setTranslateY(chartHeight);
        return hyperParameterBox;
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

    private static boolean isFieldParsableInt(TextField tf, Text t) {
        try {
            Integer.parseInt(tf.getText());
            t.setFill(Color.BLACK);
            return true;
        } catch (NumberFormatException e) {
            t.setFill(Color.RED);
            return false;
        }
    }

    private static boolean isFieldParsableLong(TextField tf, Text t) {
        try {
            Long.parseLong(tf.getText());
            t.setFill(Color.BLACK);
            return true;
        } catch (NumberFormatException e) {
            t.setFill(Color.RED);
            return false;
        }
    }

    private static boolean isFieldParsableFloat(TextField tf, Text t) {
        try {
            Float.parseFloat(tf.getText());
            t.setFill(Color.BLACK);
            return true;
        } catch (NumberFormatException e) {
            t.setFill(Color.RED);
            return false;
        }
    }

    private static boolean isFieldParsableDouble(TextField tf, Text t) {
        try {
            Double.parseDouble(tf.getText());
            t.setFill(Color.BLACK);
            return true;
        } catch (NumberFormatException e) {
            t.setFill(Color.RED);
            return false;
        }
    }

    private static boolean andAll(boolean... booleans) {
        boolean result = true;
        int size = booleans.length;
        for (int i = 0; i < size; i++) {
            result = result && booleans[i];
        }
        return result;
    }
}
