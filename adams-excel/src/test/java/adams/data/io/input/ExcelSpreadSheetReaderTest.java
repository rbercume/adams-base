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

/*
 * ExcelSpreadSheetReaderTest.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.io.ExcelSpreadSheetReader class. Run from commandline with: <br><br>
 * java adams.core.io.ExcelSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExcelSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ExcelSpreadSheetReaderTest(String name) {
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
	"sample.xls",
	"sample2.xlsx",
	"sample2.xlsx",
	"sample2.xlsx",
	"sample2.xlsx",
	"sample2.xlsx",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    ExcelSpreadSheetReader[]  result;

    result    = new ExcelSpreadSheetReader[6];
    result[0] = new ExcelSpreadSheetReader();
    result[1] = new ExcelSpreadSheetReader();
    result[2] = new ExcelSpreadSheetReader();
    result[2].setNoHeader(true);
    result[3] = new ExcelSpreadSheetReader();
    result[3].setNoHeader(true);
    result[3].setCustomColumnHeaders("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54");
    result[4] = new ExcelSpreadSheetReader();
    result[4].setFirstRow(3);
    result[4].setNumRows(2);
    result[5] = new ExcelSpreadSheetReader();
    result[5].setNoHeader(true);
    result[5].setFirstRow(3);
    result[5].setNumRows(2);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ExcelSpreadSheetReaderTest.class);
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
