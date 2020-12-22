package functionapproximator;

import java.util.function.Function;

public class Function1D {

    private Function<Double, Double> fx;

    Function1D(Function<Double, Double> fx) {
        this.fx = fx;
    }

    public double evaluate(double x) {
        return fx.apply(x);
    }
    
}
