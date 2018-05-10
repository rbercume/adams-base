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
 * MultiPageIconSupporter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

/**
 * Interface for classes that allow changing the tab icon of the multi-page pane
 * that they are part of.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see MultiPagePane
 */
public interface MultiPageIconSupporter {

  /**
   * Sets the page icon.
   *
   * @param icon	the name of the icon, null to unset it
   */
  public void setPageIcon(String icon);
}
