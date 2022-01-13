/* This program is part of the ORIS Tool.
 * Copyright (C) 2011-2020 The ORIS Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oristool.eulero.graph;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.oristool.eulero.math.distribution.continuous.ContinuousDistribution;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.domain.DBMZone;
import org.oristool.math.expression.Expolynomial;
import org.oristool.math.expression.Variable;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.models.tpn.ConcurrencyTransitionFeature;
import org.oristool.models.tpn.RegenerationEpochLengthTransitionFeature;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

/**
 * Activity with an analytical CDF.
 */
@XmlRootElement(name = "Analytical")
public class Analytical extends Activity {

    @XmlTransient
    private StochasticTransitionFeature pdf;

    @XmlTransient
    private ArrayList<StochasticTransitionFeature> pdfFeatures;
    @XmlTransient
    private ArrayList<BigDecimal> pdfWeights;

    /**
     * Creates an activity with analytical PDF. 
     */
    public Analytical(String name, StochasticTransitionFeature pdf) {
        // Se funziona meglio co lo XOR di transizioni, questo praticamente non lo cancello, ma mi andrà a creare la lista e ad aggiungere pdf
        super(name);
        setEFT(pdf.density().getDomainsEFT().bigDecimalValue());
        setLFT((pdf.density().getDomainsLFT().bigDecimalValue() != null) ? pdf.density().getDomainsLFT().bigDecimalValue() : BigDecimal.valueOf(Double.MAX_VALUE));
        setC(BigInteger.ONE);
        setR(BigInteger.ONE);
        setSimplifiedC(BigInteger.ONE);
        setSimplifiedR(BigInteger.ONE);
        this.pdfFeatures = new ArrayList<>();
        this.pdfFeatures.add(pdf);
        this.pdfWeights = new ArrayList<>();
        this.pdfWeights.add(BigDecimal.ONE);
    }

    public Analytical(String name, ArrayList<StochasticTransitionFeature> pdfFeatures, ArrayList<BigDecimal> pdfWeights) {
        super(name);
        setEFT(BigDecimal.valueOf(pdfFeatures.stream().mapToDouble(t -> t.density().getDomainsEFT().doubleValue()).min().orElse(0)));
        setLFT(BigDecimal.valueOf(
                Double.isInfinite(pdfFeatures.stream().mapToDouble(t -> t.density().getDomainsLFT().doubleValue()).max().getAsDouble()) ? Double.MAX_VALUE :
                pdfFeatures.stream().mapToDouble(t -> t.density().getDomainsLFT().doubleValue()).max().getAsDouble())
        );
        setC(BigInteger.ONE);
        setR(BigInteger.ONE);
        setSimplifiedC(BigInteger.ONE);
        setSimplifiedR(BigInteger.ONE);
        this.pdfFeatures = pdfFeatures;
        this.pdfWeights = pdfWeights;
    }

    public Analytical(){
        super("");
    }

    @Override
    public Analytical copyRecursive(String suffix) {
        return new Analytical(this.name() + suffix, this.pdfFeatures, this.pdfWeights);
    }

    @Override
    public void buildTimedPetriNet(PetriNet pn, Place in, Place out, int priority) {}


    public StochasticTransitionFeature pdf() {
        return pdf;
    }
    
    @Override
    public String yamlData() {
        StringBuilder b = new StringBuilder();
        List<? extends DBMZone> domains = pdf.density().getDomains();
        List<? extends Expolynomial> densities = pdf.density().getDensities();
        for (int i = 0; i < domains.size(); i++) {
            b.append(String.format("  pdf: [%s, %s] %s\n",
                    domains.get(i).getBound(Variable.TSTAR, Variable.X).negate(),
                    domains.get(i).getBound(Variable.X, Variable.TSTAR),
                    densities.get(i).toString()));
        }
        return b.toString();
    }
    
    public static Analytical uniform(String name, BigDecimal a, BigDecimal b) {
        return new Analytical(name,
                StochasticTransitionFeature.newUniformInstance(a, b));
    }

    public static Analytical exp(String name, BigDecimal lambda) {
        return new Analytical(name,
                StochasticTransitionFeature.newExponentialInstance(lambda));
    }

    public static Analytical erlang(String name, int k, BigDecimal lambda) {
        return new Analytical(name,
                StochasticTransitionFeature.newErlangInstance(k, lambda));
    }

    public double[] getNumericalCDF(BigDecimal timeLimit, BigDecimal step){
        // Se si trova un modo più furbo di fare questa...
        TransientSolution<DeterministicEnablingState, RewardRate> analysisSolution = this.analyze(timeLimit.toString(), step.toString(), "0.001");
        double[] cdf = new double[analysisSolution.getSolution().length];
        for(int i = 0; i < cdf.length; i++){
            cdf[i] = analysisSolution.getSolution()[i][0][0];
        }
        return cdf;
    }

    @Override
    public int addStochasticPetriBlock(PetriNet pn, Place in, Place out, int prio) {
        if(pdfFeatures.size() == 1){
            Transition t = pn.addTransition(this.name());
            t.addFeature(new Priority(prio));
            t.addFeature(pdfFeatures.get(0));
            pn.addPrecondition(in, t);
            pn.addPostcondition(t, out);
            return prio + 1;
        }

        for(StochasticTransitionFeature feature: pdfFeatures){
            Transition immediateT = pn.addTransition(this.name() + "_imm_" + pdfFeatures.indexOf(feature));
            immediateT.addFeature(new Priority(prio));
            immediateT.addFeature(StochasticTransitionFeature.newDeterministicInstance(BigDecimal.ZERO, MarkingExpr.of(pdfWeights.get(pdfFeatures.indexOf(feature)).doubleValue())));
            Place p = pn.addPlace("p_" + this.name() + "_" + pdfFeatures.indexOf(feature));
            Transition t = pn.addTransition(this.name() + "_" + pdfFeatures.indexOf(feature));
            t.addFeature(new Priority(prio));
            t.addFeature(feature);

            if(feature.isEXP()){
                Place pDet = pn.addPlace("p_" + this.name() + "_" + pdfFeatures.indexOf(feature) + "DET");
                Transition tDet = pn.addTransition(this.name() + "_DET");
                tDet.addFeature(new Priority(prio));
                tDet.addFeature(StochasticTransitionFeature.newDeterministicInstance(BigDecimal.valueOf(pdfFeatures.stream().filter(feat -> !feat.isEXP()).mapToDouble(feat -> feat.density().getDomainsLFT().doubleValue()).max().orElse(0)), MarkingExpr.ONE));

                pn.addPostcondition(immediateT, pDet);
                pn.addPrecondition(pDet, tDet);
                pn.addPostcondition(tDet, p);
            } else {
                pn.addPostcondition(immediateT, p);
            }

            pn.addPrecondition(in, immediateT);
            pn.addPrecondition(p, t);
            pn.addPostcondition(t, out);
        }
        return prio + 1;
    }

    @Override
    public boolean isWellNested() {
        return true;
    }

    @Override
    public BigDecimal low() {
        return this.EFT();
    }

    @Override
    public BigDecimal upp() {
        return this.LFT();
    }

    public void setFeatures(ArrayList<StochasticTransitionFeature> pdfFeatures) {
        this.pdfFeatures = pdfFeatures;
    }

    public void setWeights(ArrayList<BigDecimal> pdfWeights) {
        this.pdfWeights = pdfWeights;
    }

}
