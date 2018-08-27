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
 * RemoveAutogeneratedActors.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.AutogeneratedActor;
import adams.flow.core.MutableActorHandler;

/**
 <!-- globalinfo-start -->
 * Removes all actors implementing the adams.flow.core.AutogeneratedActor. If an actor cannot be removed, it gets disabled.
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveAutogeneratedActors
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -8658024993875114313L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes all actors implementing the " + Utils.classToString(AutogeneratedActor.class) + ". If an actor cannot be removed, it gets disabled.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(Actor actor) {
    ActorHandler	handler;
    MutableActorHandler	mutable;
    int			i;

    if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;

      // remove disabled actors
      if (handler instanceof MutableActorHandler) {
	mutable = (MutableActorHandler) handler;
	i       = 0;
	while (i < mutable.size()) {
	  if (mutable.get(i) instanceof AutogeneratedActor) {
	    mutable.remove(i);
	    m_Modified = true;
	  }
	  else {
	    i++;
	  }
	}
      }
      else {
	for (i = 0; i < handler.size(); i++) {
	  if (handler.get(i) instanceof AutogeneratedActor) {
	    handler.get(i).setSkip(true);
	    m_Modified = true;
	  }
	}
      }

      // recurse
      for (i = 0; i < handler.size(); i++)
	processActor(handler.get(i));
    }
  }
}
