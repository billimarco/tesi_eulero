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

import java.util.List;

/**
 * XOR: A random choice between activities
 */
public class Xor extends Activity {
    private List<Double> probs;
    
    public Xor(List<Double> probs, List<Activity> dependencies) {
        for (Activity a : dependencies) {
            addDependency(a);
        }

        this.probs = probs;
    }

    public List<Double> probs() {
        return probs;
    }
}
