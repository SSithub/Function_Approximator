package functionapproximator;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FunctionApproximator extends Application {

    private Stage functionStage;
    private NNFunction function = new NNFunction(10);
    private Timeline updateLoop;

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        //Create chart first which the other components rely on
        ScatterChart chart = Components.createChart();
        XYChart.Series<Number, Number> functionData = (XYChart.Series<Number, Number>) chart.getData().get(0);
        XYChart.Series<Number, Number> functionLine = (XYChart.Series<Number, Number>) chart.getData().get(1);
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        //Create the rest of the components for the application
        updateLoop = Components.createUpdateContinuouslyLoop(function, functionData, functionLine, xAxis);
        VBox inputBox = Components.createInputBox(functionData);
        Group trainingRelatedGroup = Components.createTrainingRelatedGroup(function, functionStage, functionData, functionLine, xAxis);
        HBox checkBox = Components.createUpdateLoopCheckBox(updateLoop);
        VBox dataBox = Components.createDataBox(functionData, functionLine);
        //Add components into the root
        root.getChildren().addAll(chart, inputBox, trainingRelatedGroup, checkBox, dataBox);
        //Create the scene
        Scene scene = new Scene(root, 0, 0);
        //Add styling to the points of the chart
        scene.getStylesheets().add("functionapproximator/ScatterStyle.css");
        //Set up the stage and show
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

//        addData(functionData, generatePointsFromFunction(new Function1D((x) -> Math.sin(x), "Sine")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
