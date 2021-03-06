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
 * MeanTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowstatistic;

/**
 * Tests the Mean statistic generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class MeanTest
  extends AbstractRowStatisticTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public MeanTest(String name) {
    super(name);
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
	"house_16H.csv",
	"house_16H.csv",
	"house_16H.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractRowStatistic[] getRegressionSetups() {
    Mean[]	result;
    
    result    = new Mean[3];
    result[0] = new Mean();
    result[1] = new Mean();
    result[2] = new Mean();
    
    return result;
  }

  @Override
  protected int[] getRegressionRows() {
    return new int[]{
	0,
	1,
	8,
    };
  }
}
