package functionapproximator;

import functionapproximator.NNlib.*;
import functionapproximator.NNlib.Activations.Activation;
import functionapproximator.NNlib.LossFunctions.LossFunction;
import functionapproximator.NNlib.Optimizers.Optimizer;

public class NNFunction {

    private NN nn;
    private long seed = 0;
    private int[] hiddenLayersNodes = {10};
    private Activation activation = Activations.SIGMOID;
    private LossFunction lossFunction = LossFunctions.QUADRATIC(.5);
    private float learningRate = .001f;
    private Optimizer optimizer = Optimizers.VANILLA;

    public NNFunction() {
        buildNetwork();
    }

    public void buildNetwork() {
        int hiddenLayersNum = hiddenLayersNodes.length;
        Layer[] layers = new Layer[hiddenLayersNum + 1];//Hidden Layers + Output Layer
        for (int i = 0; i < hiddenLayersNum; i++) {
            layers[i] = new Layer.Dense(hiddenLayersNodes[i], activation, Initializers.VANILLA);//Hidden Layers
        }
        layers[hiddenLayersNum] = new Layer.Dense(1, Activations.LINEAR, Initializers.VANILLA);//Output Layer
        nn = new NN("", seed, learningRate, lossFunction, optimizer,
                new Layer.Dense(1, hiddenLayersNodes[0], activation, Initializers.VANILLA), layers);//Input Layer and adding the other Layers
    }

    public void setHyperParameters(long seed, int hiddenLayerNodes, Activation activation, LossFunction lossFunction, float learningRate, Optimizer optimizer) {
        this.seed = seed;
        this.hiddenLayersNodes[0] = hiddenLayerNodes;
        this.activation = activation;
        this.lossFunction = lossFunction;
        this.learningRate = learningRate;
        this.optimizer = optimizer;
        buildNetwork();
    }

    public void setLearningRateDirectly(float learningRate) {
        nn.setLearningRate(learningRate);
    }
    
    public void setLossFunction(LossFunction a){
        nn.setLossFunction(a);
    }

    public float evaluate(float x) {
        return ((float[][]) nn.feedforward(new float[][]{{x}}))[0][0];
    }

    public void train(float x, float y) {
        nn.backpropagation(new float[][]{{x}}, new float[][]{{y}});
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
