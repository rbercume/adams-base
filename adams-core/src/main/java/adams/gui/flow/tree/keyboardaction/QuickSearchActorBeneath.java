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
 * QuickSearchActorHere.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.flow.core.Actor;
import adams.gui.core.BaseShortcut;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.TreeOperations.InsertPosition;

/**
 * Allows the user to search for an actor to be inserted beneath the current location.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class QuickSearchActorBeneath
  extends AbstractQuickSearchActor {

  private static final long serialVersionUID = 5437085259210069183L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to search for an actor to be inserted beneath the current location.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  @Override
  protected BaseShortcut getDefaultShortcut() {
    return new BaseShortcut("ctrl shift K");
  }

  /**
   * Checks whether the current state is suitable.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String check(StateContainer state) {
    String	result;

    result = super.check(state);

    if (result == null) {
      if (!state.isMutable)
	result = "Current actor is not mutable";
    }

    return result;
  }

  /**
   * Performs the actual execution of the aciton.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute(StateContainer state) {
    Actor actor;

    actor = search(state, InsertPosition.BENEATH);
    if (actor == null)
      return null;

    state.tree.getOperations().addActor(state.selPath, actor, InsertPosition.BENEATH, true);
    return null;
  }
}