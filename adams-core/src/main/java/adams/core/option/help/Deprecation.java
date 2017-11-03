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
 * Deprecation.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.option.help;

import adams.core.Utils;
import adams.core.annotation.DeprecatedClass;

/**
 <!-- globalinfo-start -->
 * Generates help for deprecated classes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Deprecated
 * @see DeprecatedClass
 */
public class Deprecation
  extends AbstractHelpGenerator {

  private static final long serialVersionUID = -3885494293535045819L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates help for deprecated classes.";
  }

  /**
   * Checks whether the generator handles this class.
   *
   * @param cls		the class to check
   * @return		true if it can handle the class
   */
  @Override
  public boolean handles(Class cls) {
    return cls.isAnnotationPresent(DeprecatedClass.class)
      || cls.isAnnotationPresent(Deprecated.class);
  }

  /**
   * Generates the help for the object in the requested format.
   *
   * @param obj		the object to generate the help for
   * @param format	the format of the output
   * @return		the generated help
   */
  @Override
  public String generate(Object obj, HelpFormat format) {
    StringBuilder	result;
    DeprecatedClass	dep;

    result = new StringBuilder();

    switch (format) {
      case PLAIN_TEXT:
	if (obj.getClass().isAnnotationPresent(DeprecatedClass.class)) {
	  dep = obj.getClass().getAnnotation(DeprecatedClass.class);
	  result.append(Utils.classToString(obj) + " is deprecated!<br>" + "Use instead: " + Utils.classesToString(dep.useInstead()));
	  result.append("\n\n");
	}
	else if (obj.getClass().isAnnotationPresent(Deprecated.class)) {
	  result.append(Utils.classToString(obj) + " is deprecated!");
	  result.append("\n\n");
	}
	break;

      case HTML:
	if (obj.getClass().isAnnotationPresent(DeprecatedClass.class)) {
	  dep = obj.getClass().getAnnotation(DeprecatedClass.class);
	  result.append("<b>");
	  result.append(Utils.classToString(obj) + " is deprecated!<br>" + "Use instead: " + Utils.classesToString(dep.useInstead()));
	  result.append("<b><br>\n");
	  result.append("\n");
	}
	else if (obj.getClass().isAnnotationPresent(Deprecated.class)) {
	  result.append("<b>");
	  result.append(Utils.classToString(obj) + " is deprecated!");
	  result.append("<b><br>\n");
	  result.append("\n");
	}
	break;

      default:
	throw new IllegalStateException("Unhandled format: " + format);
    }

    return result.toString();
  }
}
