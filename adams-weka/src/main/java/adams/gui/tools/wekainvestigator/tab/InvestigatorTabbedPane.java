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
 * InvestigatorTabbedPane.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.CleanUpHandler;
import adams.gui.core.ButtonTabComponent;
import adams.gui.core.DragAndDropTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.PopupMenuProvider;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tabbed pane for managing the tabs of the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorTabbedPane
  extends DragAndDropTabbedPane
  implements CleanUpHandler {

  private static final long serialVersionUID = -8555377473460866456L;

  /** the owner. */
  protected InvestigatorPanel m_Owner;

  /**
   * Initializes the tabbed pane.
   *
   * @param owner	the owning investigator instance
   */
  public InvestigatorTabbedPane(InvestigatorPanel owner) {
    super();

    m_Owner = owner;

    setCloseTabsWithMiddleMouseButton(true);
    setShowCloseTabButton(true);
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public InvestigatorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Adds the tab.
   *
   * @param tab		the tab to add
   */
  public void addTab(AbstractInvestigatorTab tab) {
    ButtonTabComponent	button;

    tab.setOwner(getOwner());
    tab.setFrameTitle(tab.getTitle());
    addTab(tab.getTitle(), tab);

    // icon
    button = (ButtonTabComponent) getTabComponentAt(getTabCount() - 1);
    button.setIcon((tab.getTabIcon() == null) ? null : GUIHelper.getIcon(tab.getTabIcon()));
  }

  /**
   * Inserts a new tab for the given component, at the given index,
   * represented by the given title and/or icon, either of which may
   * be {@code null}.
   *
   * @param title the title to be displayed on the tab
   * @param icon the icon to be displayed on the tab
   * @param component the component to be displayed when this tab is clicked.
   * @param tip the tooltip to be displayed for this tab
   * @param index the position to insert this new tab
   *       ({@code > 0 and <= getTabCount()})
   *
   * @throws IndexOutOfBoundsException if the index is out of range
   *         ({@code < 0 or > getTabCount()})
   */
  public void insertTab(String title, Icon icon, final Component component, String tip, int index) {
    ButtonTabComponent 		tabComp;

    super.insertTab(title, icon, component, tip, index);

    if (component instanceof PopupMenuProvider) {
      tabComp = (ButtonTabComponent) getTabComponentAt(index);
      tabComp.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isRightClick(e)) {
	    JPopupMenu menu = ((PopupMenuProvider) component).getPopupMenu();
	    menu.show(tabComp, e.getX(), e.getY());
	  }
	  // for some reason, adding a mouse listener stops left/middle
	  // mouse button clicks from working??
	  else if (MouseUtils.isLeftClick(e)) {
	    setSelectedComponent(component);
	  }
	  else if (MouseUtils.isMiddleClick(e)) {
	    tabClicked(e);
	  }
	}
      });
    }
  }

  /**
   * Removes the tab.
   *
   * @param index	the index of the tab to remove
   */
  @Override
  public void removeTabAt(int index) {
    Component comp;

    comp = getComponentAt(index);

    super.removeTabAt(index);

    if (comp instanceof CleanUpHandler)
      ((CleanUpHandler) comp).cleanUp();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    removeAll();
  }
}
