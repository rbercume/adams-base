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
 * TimeseriesLOWESSTest.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.timeseries.Timeseries;
import adams.env.Environment;

/**
 * Test class for the TimeseriesLOWESS filter. Run from the command line with: <br><br>
 * java adams.data.filter.TimeseriesLOWESSTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesLOWESSTest
  extends AbstractTimeseriesFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public TimeseriesLOWESSTest(String name) {
    super(name);
  }

  /**
   * Returns the configured filter.
   *
   * @return		the filter
   */
  public Filter<Timeseries> getFilter() {
    return new TimeseriesLOWESS();
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"wine_mod.sts",
	"wine_mod.sts",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Filter[] getRegressionSetups() {
    TimeseriesLOWESS[]	result;

    result = new TimeseriesLOWESS[2];

    result[0] = new TimeseriesLOWESS();

    result[1] = new TimeseriesLOWESS();
    result[1].setWindowSize(5);;

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(TimeseriesLOWESSTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
