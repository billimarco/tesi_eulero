package org.oristool.eulero.math.approximation;

import org.oristool.eulero.math.distribution.continuous.LinearEuleroPieceDistribution;
import org.oristool.eulero.math.distribution.continuous.ShiftedExponentialDistribution;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.function.GEN;
import org.oristool.math.function.PartitionedGEN;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class SplineBodyEXPTailApproximation extends Approximator {
    private int bodyPieces;
    private int scale = 4;

    public SplineBodyEXPTailApproximation(int bodyPieces){
        this.bodyPieces = bodyPieces;
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getApproximationSupports(double[] cdf, double low, double upp, BigDecimal step) {
        BigDecimal tukeysUpperBound = ApproximationHelpers.getQuartileBounds(cdf, low, upp).get("upp");
        int tukeysUpperBoundIndex = ApproximationHelpers.getQuartileBoundsIndices(cdf, low, upp).get("upp").intValue();

        double timeTick = step.doubleValue();
        double[] x = new double[tukeysUpperBoundIndex + 1];
        for(int i = 0; i <= tukeysUpperBoundIndex; i++){
            x[i] = low + i * timeTick;
        }

        Map<String, Map<String, BigDecimal>> supports = new HashMap<>();
        int pieceLength = (int) tukeysUpperBoundIndex / bodyPieces;

        for(int i = 0; i < this.bodyPieces; i++){
            Map<String, BigDecimal> support = new HashMap<>();
            support.put("start", BigDecimal.valueOf(x[i * pieceLength]).setScale(BigDecimal.valueOf(timeTick).scale(), RoundingMode.HALF_DOWN));
            support.put("end", BigDecimal.valueOf(
                    i == this.bodyPieces - 1 ? x[tukeysUpperBoundIndex] : x[(i + 1) * pieceLength]
            ).setScale(BigDecimal.valueOf(timeTick).scale(), RoundingMode.HALF_DOWN));

            supports.put("body_" + i, support);
        }

        Map<String, BigDecimal> tailSupport = new HashMap<>();
        tailSupport.put("start", BigDecimal.valueOf(x[tukeysUpperBoundIndex]).setScale(BigDecimal.valueOf(timeTick).scale(), RoundingMode.HALF_DOWN));
        tailSupport.put("end", BigDecimal.valueOf(Double.MAX_VALUE));
        supports.put("tail", tailSupport);

        return supports;
    }

    @Override
    public Map<String, Map<String, BigInteger>> getApproximationSupportIndices(double[] cdf, double low, double upp) {
        int tukeysUpperBoundIndex = ApproximationHelpers.getQuartileBoundsIndices(cdf, low, upp).get("upp").intValue();

        Map<String, Map<String, BigInteger>> supports = new HashMap<>();
        int pieceLength = (int) tukeysUpperBoundIndex / bodyPieces;

        for(int i = 0; i < this.bodyPieces; i++){
            Map<String, BigInteger> supportIndices = new HashMap<>();
            supportIndices.put("start", BigInteger.valueOf(i * pieceLength));
            supportIndices.put("end", BigInteger.valueOf(
                    i == this.bodyPieces - 1 ? tukeysUpperBoundIndex : (i + 1) * pieceLength
            ));

            supports.put("body_" + i, supportIndices);
        }

        Map<String, BigInteger> tailSupport = new HashMap<>();
        tailSupport.put("start", BigInteger.valueOf(tukeysUpperBoundIndex));
        tailSupport.put("end", BigInteger.valueOf(cdf.length - 1));
        supports.put("tail", tailSupport);

        return supports;
    }

    @Override
    public Map<String, BigDecimal> getApproximationSupportsWeight(double[] cdf, double low, double upp, BigDecimal step){
        Map<String, BigDecimal> weights = new HashMap<>();

        Map<String, Map<String, BigInteger>> supportIndices = getApproximationSupportIndices(cdf, low, upp);

        double weightsSum = supportIndices.entrySet().stream().filter(t -> !t.getKey().equals("tail")).mapToDouble(t -> cdf[t.getValue().get("end").intValue()] - (t.getValue().get("start").intValue() - 1 < 0 ? 0 : cdf[t.getValue().get("start").intValue() - 1])).sum();

        supportIndices.forEach((name, support) -> {
            if(name.equals("tail")){
                weights.put(name, BigDecimal.valueOf(0.25));
            } else {
                weights.put(name, BigDecimal.valueOf(
                        (cdf[support.get("end").intValue()] - (support.get("start").intValue() - 1 < 0 ? 0 : cdf[support.get("start").intValue() - 1]))
                ));

                /*double[] x = new double[cdf.length];
                for(int i = 0; i < x.length; i++){
                    x[i] = BigDecimal.valueOf(low + i * step.doubleValue()).setScale(step.scale(), RoundingMode.HALF_DOWN).doubleValue();
                }

                double localArea = 0;
                for (int j = support.get("start").intValue(); j <= support.get("end").intValue(); j++){
                    localArea += cdf[j] * step.doubleValue();
                }
                weights.put(name, BigDecimal.valueOf(localArea));*/
            }
        });

        return weights;
    }

    @Override
    public Map<String, ApproximationSupportSetup> getApproximationSupportSetups(double[] cdf, double low, double upp, BigDecimal step) {
        Map<String, ApproximationSupportSetup> setups = new HashMap<>();
        Map<String, Map<String, BigDecimal>> approximationSupports = getApproximationSupports(cdf, low, upp, step);
        Map<String, Map<String, BigInteger>> approximationIndexedSupports = getApproximationSupportIndices(cdf, low, upp);
        Map<String, BigDecimal> weights = getApproximationSupportsWeight(cdf, low, upp, step);
        Map<String, Map<String, BigDecimal>> params = getApproximationParameters(cdf, low, upp, step);
        double[] pdf = new double[cdf.length];

        for(int i = 0; i < pdf.length; i++){
            pdf[i] = (i != pdf.length - 1 ? (cdf[i+1] - cdf[i]) : cdf[i]) / step.doubleValue() ;
        }

        params.forEach((name, parameters) -> {
            if(name.equals("tail")){
                setups.put(name, new ApproximationSupportSetup(weights.get(name), approximationSupports.get(name), parameters,
                        new ShiftedExponentialDistribution(name, approximationSupports.get(name).get("start"), parameters.get("lambda"))));
            } else {
                setups.put(name, new ApproximationSupportSetup(weights.get(name), approximationSupports.get(name), parameters,
                        new LinearEuleroPieceDistribution(name,
                                approximationSupports.get(name).get("start"),
                                approximationSupports.get(name).get("end"),
                                parameters.get("alpha"),
                                weights.get(name),
                                BigDecimal.valueOf(pdf[Math.max(approximationIndexedSupports.get(name).get("start").intValue(), 0)])
                        )
                ));
            }
        });

        return setups;
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getApproximationParameters(double[] cdf, double low, double upp, BigDecimal step) {
        Map<String, Map<String, BigInteger>> supportIndices = getApproximationSupportIndices(cdf, low, upp);
        Map<String, Map<String, BigDecimal>> supportValues = getApproximationSupports(cdf, low, upp, step);
        Map<String, BigDecimal> supportWeight = getApproximationSupportsWeight(cdf, low, upp, step);

        double timeTick = step.doubleValue();
        double[] x = new double[cdf.length];
        double[] pdf = new double[cdf.length];
        for(int i = 0; i < x.length; i++){
            x[i] = BigDecimal.valueOf(low + i * timeTick).setScale(step.scale(), RoundingMode.HALF_DOWN).doubleValue();
        }
        for(int i = 0; i < pdf.length; i++){
            pdf[i] = (i != pdf.length - 1 ? (cdf[i+1] - cdf[i]) : 0) / timeTick ;
        }

        Map<String, Map<String, BigDecimal>> allParameters = new HashMap<>();

        supportIndices.forEach((name, indices) -> {
            Map<String, BigDecimal> parameters = new HashMap<>();

            if(name.equals("tail")){
                double tailLambda = Double.MAX_VALUE;
                for(int i = indices.get("start").intValue() ; i < indices.get("end").intValue(); i++){
                    double cdfValue = ((i != 0 ? cdf[i - 1] : cdf[i]) - (cdf[indices.get("start").intValue() - 1])) / 0.25;//supportWeight.get("tail").doubleValue();
                    //TODO Fixme
                    if(cdfValue>0 && cdfValue<1 && x[i]> supportValues.get("tail").get("start").doubleValue()) {
                        tailLambda = Math.min(
                                tailLambda,
                                -Math.log(1 - cdfValue) / (x[i] - supportValues.get("tail").get("start").doubleValue())
                        );
                    }
                }
                parameters.put("lambda", BigDecimal.valueOf(tailLambda));
                allParameters.put(name, parameters);

            } else {
                double localArea = supportWeight.get(name).doubleValue();
                double localMean = 0;

                for (int j = indices.get("start").intValue(); j < indices.get("end").intValue(); j++){
                    localMean += //((pdf[j] + pdf[j+1]) * timeTick / 2 ) * x[j + 1];
                            (pdf[j] * timeTick) * x[j];
                }

                localMean = localMean/localArea;

                double x1 = x[Math.max(indices.get("start").intValue(),0)];
                double x2 = x[(indices.get("end").intValue())];
                double f1 = pdf[Math.max(indices.get("start").intValue(),0)];
                double f2 = pdf[Math.max(indices.get("end").intValue(),0)];
                double h = BigDecimal.valueOf(x2 - x1).setScale(BigDecimal.valueOf(timeTick).scale(), RoundingMode.HALF_DOWN).doubleValue();
                double m = (Math.pow(x1, 3)/6 - Math.pow(x2, 3)/6  + (x1 * x2 * h)/2) / (localArea * h);
                double q = (Math.pow(x1, 3)/6 - Math.pow(x2, 3)/6  + (x1 * x2 * h)/2) * f1 / (localArea * h) /*-
                        (Math.pow(x2, 3)/3 + Math.pow(x1, 3)/6  - (x1 * x2 * x2 / 2)) * f2 / (localArea * h)*/ +
                        (Math.pow(x2, 3)/3 + Math.pow(x1, 3)/6  - (x1 * x2 * x2) / 2) * 2 / (h * h);

                double alpha = (localMean - q) / m;

                if(alpha < -f1){
                    alpha = -f1;
                }
                if(alpha > 2*localArea/h - f1){
                    alpha = 2*localArea/h - f1;
                }

                parameters.put("alpha", BigDecimal.valueOf(alpha));
                allParameters.put(name, parameters);
            }
        });

        return allParameters;
    }

    @Override
    public StochasticTransitionFeature getApproximatedStochasticTransitionFeature(double[] cdf, double low, double upp, BigDecimal step) {
        // Ricorda che la cdf è data da 0 a upp; low si usa se serve sapere il supporto reale.
        if(cdf.length < (upp - low)/step.doubleValue()){
            throw new RuntimeException("cdf has enough samples with respect to provided support and time step value");
        }

        ArrayList<GEN> distributionPieces = new ArrayList<>();

        int Q3Index = IntStream.range(0, cdf.length)
                .filter(i -> cdf[i] >= 0.75)
                .findFirst()
                .orElse(cdf.length-1);

        double Q3 = /*low +*/ Q3Index * step.doubleValue();

        int bodyPieceWidth = (int) ((Q3 - low) / step.doubleValue() / (double) bodyPieces);

        double[] pdf = new double[cdf.length];
        for(int i = 0; i < pdf.length; i++){
            pdf[i] = (i != pdf.length - 1 ? (cdf[i+1] - cdf[i]) : 0) / step.doubleValue() ;
        }

        for(int i = 0; i < bodyPieces; i++){
            // Body
            int bodyPieceStartingIndex = (int) (low / step.doubleValue()) + i * bodyPieceWidth;
            int bodyPieceEndingIndex = (i != bodyPieces - 1) ?  (int) (low / step.doubleValue()) + (i + 1) * bodyPieceWidth : Q3Index;
            OmegaBigDecimal eft = new OmegaBigDecimal(String.valueOf(bodyPieceStartingIndex * step.doubleValue()));
            OmegaBigDecimal lft = new OmegaBigDecimal(String.valueOf(bodyPieceEndingIndex * step.doubleValue())); // sull'ultimo dovrebbe venire proprio Q3
            double bodyPieceLocalWeight = cdf[bodyPieceEndingIndex] - cdf[bodyPieceStartingIndex];

            double bodyPieceLocalMean = 0;

            for (int j = bodyPieceStartingIndex; j < bodyPieceEndingIndex; j++){
                bodyPieceLocalMean += //((pdf[j] + pdf[j+1]) * timeTick / 2 ) * x[j + 1];
                        (pdf[j] * step.doubleValue()) * (j * step.doubleValue());
            }

            bodyPieceLocalMean = bodyPieceLocalMean/bodyPieceLocalWeight;

            double x1 = eft.doubleValue();
            double x2 = lft.doubleValue();
            double f1 = pdf[bodyPieceStartingIndex];
            double h = BigDecimal.valueOf(x2 - x1).setScale(step.scale(), RoundingMode.HALF_DOWN).doubleValue();
            double m = (Math.pow(x1, 3)/6 - Math.pow(x2, 3)/6  + (x1 * x2 * h)/2) / (bodyPieceLocalWeight * h);
            double q = (Math.pow(x1, 3)/6 - Math.pow(x2, 3)/6  + (x1 * x2 * h)/2) * f1 / (bodyPieceLocalWeight * h) +
                    (Math.pow(x2, 3)/3 + Math.pow(x1, 3)/6  - (x1 * x2 * x2) / 2) * 2 / (h * h);

            double alpha = (bodyPieceLocalMean - q) / m;

            if(alpha < -f1){
                alpha = -f1;
            }
            if(alpha > 2 * bodyPieceLocalWeight / h - f1){
                alpha = 2 * bodyPieceLocalWeight / h - f1;
            }

            double c1 = (f1 + alpha) / h;
            double c2 = (2 * bodyPieceLocalWeight / h - f1 - alpha) / h;

            String density = c1 * x2 - c2 * x1 + " + " + (c2 - c1) + "*x^1";

            distributionPieces.add(GEN.newExpolynomial(density, eft, lft));
        }

        //tail
        double tailLambda = Double.MAX_VALUE;
        double[] test = new double[cdf.length - Q3Index];
        for(int i = Q3Index ; i < cdf.length; i++){
            double cdfValue = (cdf[i] - cdf[Q3Index]) / (1 - cdf[Q3Index]);

            //Discard bad conditioned values
            if(cdfValue > 0  &&  cdfValue < 1 && /*low + */(i * step.doubleValue()) > Q3) {
                tailLambda = Math.min(
                        tailLambda,
                        -Math.log(1 - cdfValue) / (/*low + */(i * step.doubleValue()) - Q3)
                );
            }
            test[i - Q3Index] = cdfValue;
        }

        String density = (1 - cdf[Q3Index]) * tailLambda * Math.exp(tailLambda * Q3) + " * Exp[-" + tailLambda + " x]";
        distributionPieces.add(GEN.newExpolynomial(density, new OmegaBigDecimal(String.valueOf(Q3)), OmegaBigDecimal.POSITIVE_INFINITY));

        return StochasticTransitionFeature.of(new PartitionedGEN(distributionPieces));
    }

    @Override
    public ArrayList<StochasticTransitionFeature> getApproximatedStochasticTransitionFeatures(double[] cdf, double low, double upp, BigDecimal step) {
        // Clean weights vector
        stochasticTransitionFeatureWeights().clear();
        ArrayList<StochasticTransitionFeature> features = new ArrayList<>();

        // Ricorda che la cdf è data da 0 a upp; low si usa se serve sapere il supporto reale.
        if(cdf.length < (upp - low)/step.doubleValue()){
            throw new RuntimeException("cdf has enough samples with respect to provided support and time step value");
        }

        ArrayList<GEN> distributionPieces = new ArrayList<>();

        int Q3Index = IntStream.range(0, cdf.length)
                .filter(i -> cdf[i] >= 0.75)
                .findFirst()
                .orElse(cdf.length-1);

        double Q3 = /*low +*/ Q3Index * step.doubleValue();

        int bodyPieceWidth = (int) ((upp - low) / step.doubleValue() / (double) bodyPieces);

        double[] pdf = new double[cdf.length];
        for(int i = 0; i < pdf.length; i++){
            pdf[i] = (i != pdf.length - 1 ? (cdf[i+1] - cdf[i]) : 0) / step.doubleValue() ;
        }

        for(int i = 0; i < bodyPieces; i++){
            // Body
            int bodyPieceStartingIndex = (int) (low / step.doubleValue()) + i * bodyPieceWidth;
            int bodyPieceEndingIndex = (i != bodyPieces - 1) ?  (int) (low / step.doubleValue()) + (i + 1) * bodyPieceWidth : cdf.length - 1;
            OmegaBigDecimal eft = new OmegaBigDecimal(String.valueOf(bodyPieceStartingIndex * step.doubleValue()));
            OmegaBigDecimal lft = new OmegaBigDecimal(String.valueOf(bodyPieceEndingIndex * step.doubleValue())); // sull'ultimo dovrebbe venire proprio Q3
            double bodyPieceLocalWeight = cdf[bodyPieceEndingIndex] - cdf[bodyPieceStartingIndex];

            double bodyPieceLocalMean = 0;

            for (int j = bodyPieceStartingIndex; j < bodyPieceEndingIndex; j++){
                bodyPieceLocalMean += //((pdf[j] + pdf[j+1]) * timeTick / 2 ) * x[j + 1];
                        (pdf[j] * step.doubleValue()) * (j * step.doubleValue());
            }

            bodyPieceLocalMean = bodyPieceLocalMean/bodyPieceLocalWeight;

            double x1 = eft.doubleValue();
            double x2 = lft.doubleValue();
            double f1 = pdf[bodyPieceStartingIndex];
            double h = BigDecimal.valueOf(x2 - x1).setScale(step.scale(), RoundingMode.HALF_DOWN).doubleValue();
            double m = (Math.pow(x1, 3)/6 - Math.pow(x2, 3)/6  + (x1 * x2 * h)/2) / (bodyPieceLocalWeight * h);
            double q = (Math.pow(x1, 3)/6 - Math.pow(x2, 3)/6  + (x1 * x2 * h)/2) * f1 / (bodyPieceLocalWeight * h) +
                    (Math.pow(x2, 3)/3 + Math.pow(x1, 3)/6  - (x1 * x2 * x2) / 2) * 2 / (h * h);

            double alpha = (bodyPieceLocalMean - q) / m;

            if(alpha < -f1){
                alpha = -f1;
            }

            if(alpha > 2 * bodyPieceLocalWeight / h - f1){
                alpha = 2 * bodyPieceLocalWeight / h - f1;
            }

            double c1 = (f1 + alpha) / h;
            double c2 = (2 * bodyPieceLocalWeight / h - f1 - alpha) / h;

            // Va capito se qui come è scritto ora è normalizzato, ma credo di sì
            features.add(StochasticTransitionFeature.newExpolynomial(
                    (c1 * x2 - c2 * x1) / bodyPieceLocalWeight + " + " + (c2 - c1) / bodyPieceLocalWeight+ "*x^1", eft, lft
            ));
            stochasticTransitionFeatureWeights().add(BigDecimal.valueOf(bodyPieceLocalWeight));
        }

        //tail
        /*double tailLambda = Double.MAX_VALUE;
        double[] test = new double[cdf.length - Q3Index];
        for(int i = Q3Index ; i < cdf.length; i++){
            double cdfValue = (cdf[i] - cdf[Q3Index]) / (1 - cdf[Q3Index]);

            //Discard bad conditioned values
            if(cdfValue > 0  &&  cdfValue < 1 && (i * step.doubleValue()) > Q3) {
                tailLambda = Math.min(
                        tailLambda,
                        -Math.log(1 - cdfValue) / ((i * step.doubleValue()) - Q3)
                );
            }
            test[i - Q3Index] = cdfValue;
        }

        features.add(StochasticTransitionFeature.newExponentialInstance(BigDecimal.valueOf(tailLambda)));
        stochasticTransitionFeatureWeights().add(BigDecimal.valueOf(0.25));*/
        return features;
    }
}
