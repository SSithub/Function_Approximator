package functionapproximator;

import functionapproximator.NNlib.*;
import functionapproximator.NNlib.Activations.Activation;
import functionapproximator.NNlib.LossFunctions.LossFunction;
import functionapproximator.NNlib.Optimizers.Optimizer;

public class NNFunction {

    private float learningRate = .0001f;
    private LossFunction lossFunction = LossFunctions.QUADRATIC(.5);
    private Optimizer optimizer = Optimizers.ADAM;
    private Activation activation = Activations.TANH;
    private NN nn;
    private float scale = 1;

    public NNFunction() {
        setHiddenLayers(20);
    }

    public NNFunction(int... hiddenLayersNodes) {
        setHiddenLayers(hiddenLayersNodes);
    }

    public void setHiddenLayers(int... hiddenLayersNodes) {
        int hiddenLayersNum = hiddenLayersNodes.length;
        Layer[] layers = new Layer[hiddenLayersNum + 1];//Hidden Layers + Output Layer
        for (int i = 0; i < hiddenLayersNum; i++) {
            layers[i] = new Layer.Dense(hiddenLayersNodes[i], activation, Initializers.VANILLA);//Hidden Layers
        }
        layers[hiddenLayersNum] = new Layer.Dense(1, Activations.LINEAR, Initializers.VANILLA);//Output Layer
        nn = new NN("", 0, learningRate, lossFunction, optimizer,
                new Layer.Dense(1, hiddenLayersNodes[0], activation, Initializers.VANILLA), layers);//Input Layer and adding the other Layers
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float evaluate(float x) {
        return scale * ((float[][]) nn.feedforward(new float[][]{{x}}))[0][0];
    }

    public void train(float x, float y) {
        nn.backpropagation(new float[][]{{x}}, new float[][]{{y / scale}});
    }

    private String[] layerToString(Layer.Dense layer, String[] input, boolean activation) {
        int rows = layer.weights.length;
        int cols = layer.weights[0].length;
        String[] layerString = new String[cols];
        for (int col = 0; col < cols; col++) {
            if (activation) {
                layerString[col] = "t(";
            } else {
                layerString[col] = "(";
            }
            for (int row = 0; row < rows; row++) {
                layerString[col] += layer.weights[row][col] + input[row] + "+";
            }
            layerString[col] += layer.biases[0][col] + ")";
        }
        return layerString;
    }

    @Override
    public String toString() {
        String[] input = {"x"};
        for (int layerNum = 0; layerNum < nn.length; layerNum++) {
            input = layerToString((Layer.Dense) nn.getLayer(layerNum), input, layerNum != nn.length - 1);
        }
        return input[0];
    }
}
