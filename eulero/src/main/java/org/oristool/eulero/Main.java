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

package org.oristool.eulero;

import java.math.BigDecimal;
import java.util.List;

import org.oristool.eulero.graph.Analytical;
import org.oristool.eulero.graph.DAG;
import org.oristool.eulero.graph.Repeat;
import org.oristool.eulero.graph.Xor;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

public class Main {
    public static void main(String[] args) {
        StochasticTransitionFeature unif01 =
                StochasticTransitionFeature.newUniformInstance(BigDecimal.ZERO, BigDecimal.ONE);
        
        Analytical a = new Analytical("A", unif01);
        Analytical b = new Analytical("B", unif01);
        Analytical c = new Analytical("C", unif01);
        Analytical d = new Analytical("D", unif01);       
        Analytical f = new Analytical("F", unif01);
        
        DAG g = DAG.sequence("G", 
                new Analytical("G1", unif01),
                new Analytical("G2", unif01));
        
        DAG h = DAG.sequence("H", 
                new Analytical("H1", unif01),
                new Analytical("H2", unif01));
        
        Xor i = new Xor("I",
                List.of(new Analytical("IA", unif01),
                        new Analytical("IB", unif01)),
                List.of(0.3, 0.7));
        
        DAG j = DAG.sequence("J", 
                new Analytical("J1", unif01),
                new Analytical("J2", unif01),
                new Analytical("J3", unif01));

        Xor k = new Xor("K", List.of(
                DAG.sequence("KA",
                        new Analytical("KA1", unif01),
                        new Analytical("KA2", unif01)),
                DAG.sequence("KB",
                        new Analytical("KB1", unif01),
                        new Analytical("KB2", unif01))),
                List.of(0.4, 0.6));
        
        Analytical n = new Analytical("N", unif01);
        
        DAG o = DAG.forkJoin("O", 
                DAG.sequence("YAPBP",
                        new Analytical("Y", unif01),
                        DAG.forkJoin("APBP",
                                new Analytical("AP", unif01),
                                new Analytical("BP", unif01))),
                DAG.sequence("ZCPDP",
                        new Analytical("Z", unif01),
                        DAG.forkJoin("CPDP",
                                DAG.sequence("CP", 
                                        new Analytical("CP1", unif01),
                                        new Analytical("CP2", unif01)),
                                DAG.sequence("DP", 
                                        new Analytical("DP1", unif01),
                                        new Analytical("DP2", unif01)))));

        o.flatten();  // to remove DAG nesting
        
        Analytical q = new Analytical("Q", unif01);
        Analytical r = new Analytical("R", unif01);
        Analytical s = new Analytical("S", unif01);
        
        DAG t = DAG.sequence("T", 
                new Analytical("T1", unif01),
                new Analytical("T2", unif01));
        Analytical u = new Analytical("U", unif01);
        DAG tu = DAG.forkJoin("TU", t, u);

        DAG v = DAG.sequence("V", 
                new Analytical("V1", unif01),
                new Analytical("V2", unif01));
        
        Analytical w = new Analytical("W", unif01);
        DAG x = DAG.sequence("X", 
                new Analytical("X1", unif01),
                new Analytical("X2", unif01));

        DAG wx = DAG.forkJoin("WX", w, x);
        
        DAG p = DAG.empty("P");
        q.addPrecondition(p.begin());
        r.addPrecondition(p.begin());
        s.addPrecondition(p.begin());
        tu.addPrecondition(q, r);
        v.addPrecondition(r);
        wx.addPrecondition(r, s);
        p.end().addPrecondition(tu, v, wx);
        
        Repeat e = new Repeat("E", 0.1, 
                DAG.sequence("L", new Repeat("M", 0.2, p), n, o));

        DAG main = DAG.empty("MAIN");
        a.addPrecondition(main.begin());
        b.addPrecondition(main.begin());
        c.addPrecondition(main.begin());
        d.addPrecondition(main.begin());
        e.addPrecondition(a, b);
        f.addPrecondition(b);
        g.addPrecondition(c);
        h.addPrecondition(c);
        i.addPrecondition(e, f);
        j.addPrecondition(f, g, h);
        k.addPrecondition(h, d);
        main.end().addPrecondition(i, j, k);
        
        System.out.println(main.yamlRecursive());
        new TransientSolutionViewer(main.analyze("10", "0.1", "0.1"));
        
    }
}
