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
 * FilteredBoofCVFeatureGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.features;

import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.transformer.AbstractBoofCVTransformer;
import adams.data.featureconverter.HeaderDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies the filter (an image transformer) to the image first before generating the features from the transformed images.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for the feature names.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-filter &lt;adams.data.boofcv.transformer.AbstractBoofCVTransformer&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.boofcv.transformer.PassThrough
 * </pre>
 *
 * <pre>-generator &lt;adams.data.boofcv.features.AbstractBoofCVFeatureGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use on the filtered data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.boofcv.features.Pixels -converter \"adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet\"
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9196 $
 */
public class FilteredBoofCVFeatureGenerator
  extends AbstractBoofCVFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the filter to use. */
  protected AbstractBoofCVTransformer m_Filter;

  /** the base feature generator. */
  protected AbstractBoofCVFeatureGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the filter (an image transformer) to the image first before "
	+ "generating the features from the transformed images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "filter", "filter",
        new adams.data.boofcv.transformer.PassThrough());

    m_OptionManager.add(
        "generator", "generator",
        new Pixels());
  }

  /**
   * Sets the filter to use.
   *
   * @param value the filter to use
   */
  public void setFilter(AbstractBoofCVTransformer value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return the filter in use
   */
  public AbstractBoofCVTransformer getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use.";
  }

  /**
   * Sets the feature generator to use on the filtered data.
   *
   * @param value the generator to use
   */
  public void setGenerator(AbstractBoofCVFeatureGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the feature generator to use on the filtered data.
   *
   * @return the generator in use
   */
  public AbstractBoofCVFeatureGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use on the filtered data.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BoofCVImageContainer img) {
    HeaderDefinition		result;
    BoofCVImageContainer[]	conts;

    conts  = m_Filter.transform(img);
    result = m_Generator.postProcessHeader(m_Generator.createHeader(conts[0]));

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BoofCVImageContainer img) {
    List<Object>[]		result;
    List			rows;
    BoofCVImageContainer[]	conts;
    int				i;

    rows = new ArrayList();
    conts = m_Filter.transform(img);
    for (BoofCVImageContainer cont: conts) {
      result = m_Generator.postProcessRows(cont, m_Generator.generateRows(cont));
      rows.addAll(Arrays.asList(result));
    }

    result = new List[rows.size()];
    for (i = 0; i < rows.size(); i++)
      result[i] = (List<Object>) rows.get(i);

    return result;
  }
}
