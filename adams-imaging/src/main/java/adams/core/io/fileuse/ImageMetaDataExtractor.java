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
 * ImageMetaDataExtractor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import adams.flow.transformer.metadata.AbstractMetaDataExtractor;
import adams.flow.transformer.metadata.MetaDataExtractor;

import java.io.File;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Uses the specified image reader to load the file for checking the 'in use' state: if reading fails, then it is assumed the file is in use.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-extractor &lt;adams.flow.transformer.metadata.AbstractMetaDataExtractor&gt; (property: extractor)
 * &nbsp;&nbsp;&nbsp;The meta-data extractor to use for checking the 'in use' state.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.metadata.MetaDataExtractor
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMetaDataExtractor
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /** the meta-data extractor to use. */
  protected AbstractMetaDataExtractor m_Extractor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the specified image reader to load the file for checking the 'in use' "
	+ "state: if reading fails, then it is assumed the file is in use. ";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "extractor", "extractor",
      new MetaDataExtractor());
  }

  /**
   * Sets the meta-data extractor to use for checking the file use.
   *
   * @param value	the extractor
   */
  public void setExtractor(AbstractMetaDataExtractor value) {
    m_Extractor = value;
    reset();
  }

  /**
   * Returns the meta-data extractor to use for checking the file use.
   *
   * @return		the extractor
   */
  public AbstractMetaDataExtractor getExtractor() {
    return m_Extractor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extractorTipText() {
    return "The meta-data extractor to use for checking the 'in use' state.";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    boolean		result;

    try {
      m_Extractor.extract(file);
      result = false;
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().log(Level.SEVERE, "Failed to extract meta-data from: " + file, e);
      result = true;
    }

    return result;
  }
}
