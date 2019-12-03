/*******************************************************************************
 * Copyright (c) 2014 Opt4J
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
 

package org.opt4j.benchmarks.wfg;

import java.util.ArrayList;
import java.util.List;

import org.opt4j.benchmarks.K;
import org.opt4j.benchmarks.M;

import com.google.inject.Inject;

/**
 * The {@link WFG2} benchmark function.
 * 
 * @author lukasiewycz
 * 
 */
public class WFG2 extends WFGEvaluator {

	/**
	 * Constructs a {@link WFG2} benchmark function.
	 * 
	 * @param k
	 *            the position parameters
	 * @param M
	 *            the number of objectives
	 */
	@Inject
	public WFG2(@K int k, @M int M) {
		super(k, M);
	}

	protected static List<Double> t2(final List<Double> y, final int k) {
		final int n = y.size();
		final int l = n - k;

		assert (k >= 1);
		assert (k < n);
		assert (l % 2 == 0);

		List<Double> t = new ArrayList<Double>();

		for (int i = 0; i < k; i++) {
			t.add(y.get(i));
		}

		for (int i = k + 1; i <= k + l / 2; i++) {
			final int head = k + 2 * (i - k) - 2;
			final int tail = k + 2 * (i - k);

			t.add(WFGTransFunctions.r_nonsep(y.subList(head, tail), 2));
		}

		return t;
	}

	protected static List<Double> t3(final List<Double> y, final int k, final int M) {
		final int n = y.size();

		assert (k >= 1);
		assert (k < n);
		assert (M >= 2);
		assert (k % (M - 1) == 0);

		List<Double> w = new ArrayList<Double>();
		for (int i = 0; i < n; i++) {
			w.add(1.0);
		}

		List<Double> t = new ArrayList<Double>();

		for (int i = 1; i <= M - 1; i++) {
			final int head = (i - 1) * k / (M - 1);
			final int tail = i * k / (M - 1);

			final List<Double> y_sub = y.subList(head, tail);
			final List<Double> w_sub = w.subList(head, tail);

			t.add(WFGTransFunctions.r_sum(y_sub, w_sub));
		}

		final List<Double> y_sub = y.subList(k, n);
		final List<Double> w_sub = w.subList(k, n);

		t.add(WFGTransFunctions.r_sum(y_sub, w_sub));

		return t;
	}

	protected static List<Double> shape(final List<Double> t_p) {

		assert (t_p.size() >= 2);

		final int M = t_p.size();

		final List<Boolean> A = createA(M, false);
		final List<Double> x = calculateX(t_p, A);

		List<Double> h = new ArrayList<Double>();

		for (int m = 1; m <= M - 1; m++) {
			h.add(WFGShapeFunctions.convex(x, m));
		}
		h.add(WFGShapeFunctions.disc(x, 5, 1.0, 1.0));

		return calculateF(x, h);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.benchmark.wfg.WFGEvaluator#f(java.util.List)
	 */
	@Override
	public List<Double> f(List<Double> y) {
		y = WFG1.t1(y, k);
		y = WFG2.t2(y, k);
		y = WFG2.t3(y, k, M);

		return WFG2.shape(y);
	}
}
