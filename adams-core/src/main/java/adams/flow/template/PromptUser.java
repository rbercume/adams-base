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
 * PromptUser.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.source.EnterManyValues;
import adams.flow.source.EnterManyValues.OutputType;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.flow.transformer.MapToVariables;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that prompts the user with the specified parameters and stores the values in variables.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: Please enter values
 * </pre>
 *
 * <pre>-value &lt;adams.flow.source.valuedefinition.AbstractValueDefinition&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The value definitions that define the dialog prompting the user to enter
 * &nbsp;&nbsp;&nbsp;the values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PromptUser
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the message for the user. */
  protected String m_Message;

  /** the value definitions. */
  protected AbstractValueDefinition[] m_Values;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a sub-flow that prompts the user with the specified "
        + "parameters and stores the values in variables.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "message", "message",
      "Please enter values");

    m_OptionManager.add(
      "value", "values",
      new AbstractValueDefinition[0]);
  }

  /**
   * Sets the message to prompt the user with.
   *
   * @param value	the message
   */
  public void setMessage(String value) {
    m_Message = value;
    reset();
  }

  /**
   * Returns the message the user is prompted with.
   *
   * @return 		the message
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String messageTipText() {
    return "The message to prompt the user with.";
  }

  /**
   * Sets the value definitions.
   *
   * @param value	the definitions
   */
  public void setValues(AbstractValueDefinition[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the value definitions.
   *
   * @return 		the definitions
   */
  public AbstractValueDefinition[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valuesTipText() {
    return "The value definitions that define the dialog prompting the user to enter the values.";
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected Actor doGenerate() {
    Trigger		result;
    EnterManyValues	enter;
    MapToVariables	map2var;

    result = new Trigger();
    result.setName("prompt user");

    enter = new EnterManyValues();
    enter.setMessage(m_Message);
    enter.setValues(m_Values);
    enter.setStopFlowIfCanceled(true);
    enter.setOutputType(OutputType.MAP);
    result.add(enter);

    map2var = new MapToVariables();
    result.add(map2var);

    return result;
  }
}