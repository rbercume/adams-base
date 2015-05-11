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
 * AbstractScript.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.features;

import adams.data.image.BufferedImageContainer;
import adams.flow.core.AdditionalOptions;
import adams.flow.core.AdditionalOptionsHandler;

import java.util.List;

/**
 * Ancestor for BufferedImage feature generator scripts.
 * <p/>
 * Scripts of scripting languages like Jython or Groovy need to be derived from this
 * class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScript
  extends AbstractBufferedImageFeatureGenerator
  implements AdditionalOptionsHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8283487312539061029L;

  /** for storing the additional options. */
  protected AdditionalOptions m_AdditionalOptions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AdditionalOptions = new AdditionalOptions();
  }

  /**
   * Sets the additional options.
   *
   * @param options	the options (name &lt;-&gt;value relation)
   */
  public void setAdditionalOptions(AdditionalOptions options) {
    m_AdditionalOptions = (AdditionalOptions) options.clone();
  }

  /**
   * Returns the value associated with the (additional) option.
   *
   * @return	the options (name &lt;-&gt;value relation)
   */
  public AdditionalOptions getAdditionalOptions() {
    return m_AdditionalOptions;
  }

  /**
   * Performs the actual feature genration.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  protected abstract List[] doGenerateRows(BufferedImageContainer img);

  /**
   * Performs the actual feature genration.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  public List<Object>[] generateRows(BufferedImageContainer img) {
    return doGenerateRows(img);
  }
}
