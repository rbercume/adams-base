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
 * AbstractClustererTestCase.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.clustering;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.io.input.ChunkedSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.ml.data.Dataset;
import adams.ml.data.DefaultDataset;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for regressor tests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClustererTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public AbstractClustererTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/ml/model/clustering/data");
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected abstract String[] getRegressionInputFiles();

  /**
   * Returns the readers for the input data files to use
   * in the regression test.
   *
   * @return		the readers
   */
  protected abstract SpreadSheetReader[] getRegressionInputReaders();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract Clusterer[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the line indices
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Reads the data using the reader.
   *
   * @param filename	the file to read (no path)
   * @param reader	the reader for loading the data
   * @return		the generated content
   */
  protected Dataset load(String filename, SpreadSheetReader reader) {
    DefaultDataset	result;
    SpreadSheet 	full;
    SpreadSheet		chunk;

    m_TestHelper.copyResourceToTmp(filename);
    full = reader.read(new TmpFile(filename));
    if (reader instanceof ChunkedSpreadSheetReader) {
      while (((ChunkedSpreadSheetReader) reader).hasMoreChunks()) {
	chunk = ((ChunkedSpreadSheetReader) reader).nextChunk();
	for (Row row : chunk.rows())
	  full.addRow().assign(row);
      }
    }
    m_TestHelper.deleteFileFromTmp(filename);

    result = new DefaultDataset(full);

    return result;
  }

  /**
   * Trains the regressor and returns the predictions.
   *
   * @param cls		the regressor to use
   * @param data	the training data
   * @return		the predictions on the training data
   */
  protected double[][] predict(Clusterer cls, Dataset data) {
    double[][]		result;
    ClusteringModel	model;
    int			i;

    result = new double[data.getRowCount()][];

    try {
      model = cls.buildModel(data);
    }
    catch (Exception e) {
      fail(
	"Failed to build model on data!\n"
	  + "Algorithm: " + OptionUtils.getCommandLine(cls) + "\n"
	  + "Data:\n" + data);
      return null;
    }

    for (i = 0; i < data.getRowCount(); i++) {
      try {
	result[i] = model.distribution(data.getRow(i));
      }
      catch (Exception e) {
	result[i] = null;
      }
    }

    return result;
  }

  /**
   * Saves the generated predictions as file.
   *
   * @param preds	the generated predictions
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(double[][] preds, String filename) {
    StringBuilder	data;
    int			i;

    data = new StringBuilder();
    for (double[] pred: preds) {
      if (data.length() > 0)
	data.append("\n");
      if (pred == null) {
	data.append("null");
      }
      else {
	for (i = 0; i < pred.length; i++) {
	  if (i > 0)
	    data.append(",");
	  data.append(Utils.doubleToString(pred[i], 6));
	}
      }
    }

    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data, false);
  }

  /**
   * Creates an output filename based on the input filename.
   *
   * @param input	the input filename (no path)
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(String input, int no) {
    String	result;
    int		index;
    String	ext;

    ext = "-out" + no;

    index = input.lastIndexOf('.');
    if (index == -1) {
      result = input + ext;
    }
    else {
      result  = input.substring(0, index);
      result += ext;
      result += input.substring(index);
    }

    return result;
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    Dataset		data;
    Dataset		dataCopy;
    boolean		ok;
    String		regression;
    int			i;
    String[]		input;
    SpreadSheetReader[]	readers;
    Clusterer[]		setups;
    String[]		output;
    double[][] 		preds;
    TmpFile[]		outputFiles;
    String		modified;

    if (m_NoRegressionTest)
      return;

    input     = getRegressionInputFiles();
    readers   = getRegressionInputReaders();
    output    = new String[input.length];
    setups    = getRegressionSetups();
    assertEquals("Number of files and readers differ!", input.length, readers.length);
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i], readers[i]);
      assertNotNull("Failed to load data?", data);
      dataCopy = data.getClone();

      preds = predict(setups[i], data);
      assertNotNull("Failed to make predictions?", preds);

      modified = SpreadSheetHelper.compare(data, dataCopy);
      assertNull("Algorithm modified data?", modified);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(preds, output[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, getRegressionIgnoredLineIndices());
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      setups[i].destroy();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
