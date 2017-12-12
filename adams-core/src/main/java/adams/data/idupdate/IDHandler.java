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
 * IDHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.idupdate;

import adams.data.id.MutableIDHandler;

/**
 * Updates the ID of (mutable) ID handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IDHandler
  extends AbstractIDUpdater {

  private static final long serialVersionUID = 371417041544523756L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the ID of (mutable) ID handlers.";
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof MutableIDHandler);
  }

  /**
   * Updates the ID of the object.
   *
   * @param obj		the object to process
   * @param id 		the new ID
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdateID(Object obj, String id) {
    ((MutableIDHandler) obj).setID(id);
    return null;
  }
}