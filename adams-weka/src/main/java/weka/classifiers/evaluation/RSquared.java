/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Bias.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.evaluation;

import adams.data.statistics.StatUtils;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instance;
import weka.core.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Computes the R^2 for regression models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RSquared
  extends AbstractEvaluationMetric
  implements StandardEvaluationMetric {

  private static final long serialVersionUID = 6501729731780442367L;

  public static final String RSQUARED = "R^2";

  /** the collected actual. */
  protected TDoubleList m_Actual = new TDoubleArrayList();

  /** the collected predicted. */
  protected TDoubleList m_Predicted = new TDoubleArrayList();

  /**
   * Return true if this evaluation metric can be computed when the class is
   * nominal
   *
   * @return true if this evaluation metric can be computed when the class is
   *         nominal
   */
  @Override
  public boolean appliesToNominalClass() {
    return false;
  }

  /**
   * Return true if this evaluation metric can be computed when the class is
   * numeric
   *
   * @return true if this evaluation metric can be computed when the class is
   *         numeric
   */
  @Override
  public boolean appliesToNumericClass() {
    return true;
  }

  /**
   * Get the name of this metric
   *
   * @return the name of this metric
   */
  @Override
  public String getMetricName() {
    return RSQUARED;
  }

  /**
   * Get a short description of this metric (algorithm, forumulas etc.).
   *
   * @return a short description of this metric
   */
  @Override
  public String getMetricDescription() {
    return RSQUARED;
  }

  /**
   * Get a list of the names of the statistics that this metrics computes. E.g.
   * an information theoretic evaluation measure might compute total number of
   * bits as well as average bits/instance
   *
   * @return the names of the statistics that this metric computes
   */
  @Override
  public List<String> getStatisticNames() {
    return Arrays.asList(RSQUARED);
  }

  /**
   * Get the value of the named statistic
   *
   * @param statName the name of the statistic to compute the value for
   * @return the computed statistic or Utils.missingValue() if the statistic
   *         can't be computed for some reason
   */
  @Override
  public double getStatistic(String statName) {
    double 	meanY;
    double 	sumSqRes;
    double 	sumSqTot;
    int		i;

    if (statName.equals(RSQUARED)) {
      if (m_Actual.size() == 0) {
	return Utils.missingValue();
      }
      else {
	meanY    = StatUtils.mean(m_Actual.toArray());
	sumSqRes = 0.0;
	sumSqTot = 0.0;
	for (i = 0; i < m_Actual.size(); i++) {
	  sumSqTot += Math.pow(m_Actual.get(i) - meanY, 2);
	  sumSqRes += Math.pow(m_Actual.get(i) - m_Predicted.get(i), 2);
	}
	if (sumSqTot != 0)
	  return 1 - sumSqRes / sumSqTot;
	else
	  return Utils.missingValue();
      }
    }
    else {
      return Utils.missingValue();
    }
  }

  /**
   * Return a formatted string (suitable for displaying in console or GUI
   * output) containing all the statistics that this metric computes.
   *
   * @return a formatted string containing all the computed statistics
   */
  @Override
  public String toSummaryString() {
    return Utils.padRight(RSQUARED, 41) + Utils.doubleToString(getStatistic(RSQUARED), 3) + "\n";
  }

  /**
   * Ignored.
   *
   * @param predictedDistribution the probabilities assigned to each class
   * @param instance the instance to be classified
   * @throws Exception if the class of the instance is not set
   */
  @Override
  public void updateStatsForClassifier(double[] predictedDistribution, Instance instance) throws Exception {
    // ignored
  }

  /**
   * Updates the statistics about a predictors performance for the current test
   * instance. Gets called when the class is numeric. Implementers need only
   * implement this method if it is not possible to compute their statistics
   * from what is stored in the base Evaluation object.
   *
   * @param predictedValue the numeric value the classifier predicts
   * @param instance the instance to be classified
   * @throws Exception if the class of the instance is not set
   */
  @Override
  public void updateStatsForPredictor(double predictedValue, Instance instance) throws Exception {
    if (!instance.classIsMissing()) {
      m_Actual.add(instance.classValue());
      m_Predicted.add(predictedValue);
    }
  }
}
