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
 * MultiPagePane.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.gui.event.RemoveItemsListener;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.HashSet;

/**
 * Manages multiple pages, like JTabbedPane manages multiple tabs.
 * Uses a JList for listing the page titles.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiPagePane
  extends BasePanel {

  private static final long serialVersionUID = -2108092957035381345L;

  /**
   * Container for page component and title.
   */
  public static class PageContainer
    implements Serializable {

    private static final long serialVersionUID = -7918640108273902031L;

    /** the title. */
    protected String m_Title;

    /** the page. */
    protected Component m_Page;

    /** the current icon (can be null). */
    protected ImageIcon m_Icon;

    /**
     * Initializes the container with no icon.
     *
     * @param title	the title
     * @param page	the page
     */
    public PageContainer(String title, Component page) {
      this(title, page, null);
    }

    /**
     * Initializes the container.
     *
     * @param title	the title
     * @param page	the page
     * @param icon	the icon
     */
    public PageContainer(String title, Component page, ImageIcon icon) {
      super();
      m_Title = title;
      m_Page  = page;
      m_Icon  = icon;
    }

    /**
     * Returns the title.
     *
     * @return		the title
     */
    public String getTitle() {
      return m_Title;
    }

    /**
     * Sets the title.
     *
     * @param value	the title
     */
    public void setTitle(String value) {
      m_Title = value;
    }

    /**
     * Returns the page.
     *
     * @return		the page
     */
    public Component getPage() {
      return m_Page;
    }

    /**
     * Sets the page.
     *
     * @param value	the page
     */
    public void setPage(Component value) {
      m_Page = value;
    }

    /**
     * Just returns the title.
     *
     * @return		the title
     */
    public String toString() {
      return m_Title;
    }

    /**
     * Returns the icon.
     *
     * @return		the icon
     */
    public ImageIcon getIcon() {
      return m_Icon;
    }

    /**
     * Sets the icon.
     *
     * @param value	the icon
     */
    public void setIcon(ImageIcon value) {
      m_Icon = value;
    }
  }

  /**
   * Interface for classes that can hook into the closing of tabs and stop it.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public interface PageCloseApprover {

    /**
     * Method gets called when having to approve a close operation.
     *
     * @param source 	the origin of the closing operation
     * @param index	the page that is to be closed
     */
    public boolean approvePageClosing(MultiPagePane source, int index);
  }

  /**
   * Interface for classes that want to customize the popup menu for entry.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static interface PopupCustomizer {

    /**
     * Gets called before the popup for the entry is displayed.
     *
     * @param index	the index this menu is for
     * @param menu	the menu so far
     */
    public void customizePopup(int index, JPopupMenu menu);
  }

  /**
   * The cell renderer.
   */
  public static class TitleRenderer
    extends DefaultListCellRenderer {

    private static final long serialVersionUID = 662711521384106051L;

    /**
     * Returns the rendering component.
     *
     * @param list		the list this renderer is for
     * @param value		the current list value
     * @param index		the index of the value
     * @param isSelected	whether the item is selected
     * @param cellHasFocus	whether the cell has the focus
     * @return			the rendering component
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component		result;
      JLabel 		label;
      PageContainer	cont;

      result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      label  = (JLabel) result;
      cont   = (PageContainer) list.getModel().getElementAt(index);
      label.setIcon(cont.getIcon());

      return result;
    }
  }

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the panel with the list and buttons. */
  protected BasePanel m_LeftPanel;

  /** the page list. */
  protected BaseList m_PageList;

  /** the list model. */
  protected DefaultListModel<PageContainer> m_PageListModel;

  /** the panel for the list buttons. */
  protected BasePanel m_PanelListButtons;

  /** the move up button. */
  protected JButton m_ButtonUp;

  /** the move down button. */
  protected JButton m_ButtonDown;

  /** the remove button. */
  protected JButton m_ButtonRemove;

  /** the action button. */
  protected BaseButtonWithDropDownMenu m_ButtonAction;

  /** the content pane for the pages. */
  protected BasePanel m_PanelContent;

  /** the listeners when pages get selected. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** whether to ignore updates. */
  protected boolean m_IgnoreUpdates;

  /** for approving page closing. */
  protected PageCloseApprover m_PageCloseApprover;

  /** an optional customizer for the popup on the JList. */
  protected PopupCustomizer m_PopupCustomizer;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PageListModel     = new DefaultListModel<>();
    m_ChangeListeners   = new HashSet<>();
    m_IgnoreUpdates     = false;
    m_PageCloseApprover = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(250);
    m_SplitPane.setResizeWeight(0.0);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);

    m_LeftPanel = new BasePanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_LeftPanel);

    m_PageList = new BaseList(m_PageListModel);
    m_PageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_PageList.addListSelectionListener((ListSelectionEvent e) -> update());
    m_PageList.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (!processListKey(e))
	  super.keyPressed(e);
      }
    });
    m_PageList.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
	int index = m_PageList.locationToIndex(e.getPoint());
	String tooltip = null;
	if (index >-1)
	  tooltip = m_PageListModel.getElementAt(index).toString();
	m_PageList.setToolTipText(tooltip);
      }
    });
    m_PageList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          showPopup(e);
          e.consume();
        }
        else {
          super.mouseClicked(e);
        }
      }
    });
    m_LeftPanel.add(new BaseScrollPane(m_PageList), BorderLayout.CENTER);

    m_PanelListButtons = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_LeftPanel.add(m_PanelListButtons, BorderLayout.SOUTH);

    m_ButtonUp = new JButton(GUIHelper.getIcon("arrow_up.gif"));
    m_ButtonUp.addActionListener((ActionEvent e) -> moveUp());
    m_PanelListButtons.add(m_ButtonUp);

    m_ButtonDown = new JButton(GUIHelper.getIcon("arrow_down.gif"));
    m_ButtonDown.addActionListener((ActionEvent e) -> moveDown());
    m_PanelListButtons.add(m_ButtonDown);

    m_ButtonRemove = new JButton(GUIHelper.getIcon("delete.gif"));
    m_ButtonRemove.addActionListener((ActionEvent e) -> checkedRemoveSelectedPage());
    m_PanelListButtons.add(m_ButtonRemove);

    m_ButtonAction = new BaseButtonWithDropDownMenu("...");
    m_ButtonAction.setVisible(false);
    m_PanelListButtons.add(m_ButtonAction);

    m_PanelContent = new BasePanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelContent);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setTitleRenderer(new TitleRenderer());
  }

  /**
   * Sets the location for the divider between page titles and content.
   *
   * @param value	the location in pixels
   */
  public void setDividerLocation(int value) {
    m_SplitPane.setDividerLocation(value);
  }

  /**
   * Returns the current location of the divider between page titles and
   * content.
   *
   * @return		the location in pixels
   */
  public int getDividerLocation() {
    return m_SplitPane.getDividerLocation();
  }

  /**
   * Sets the renderer for the titles.
   *
   * @param renderer	the renderer to use
   */
  public void setTitleRenderer(ListCellRenderer renderer) {
    m_PageList.setCellRenderer(renderer);
  }

  /**
   * Returns the renderer for the titles.
   *
   * @return		the renderer to use
   */
  public ListCellRenderer getTitleRenderer() {
    return m_PageList.getCellRenderer();
  }

  /**
   * Returns the number of pages.
   *
   * @return		the number of pages
   */
  public int getPageCount() {
    return m_PageList.getModel().getSize();
  }

  /**
   * Returns the currently selected page index.
   *
   * @return		the index, -1 if none selected
   */
  public int getSelectedIndex() {
    return m_PageList.getSelectedIndex();
  }

  /**
   * Selects the specified page index.
   *
   * @param index	the index of the page to select
   */
  public void setSelectedIndex(int index) {
    m_PageList.setSelectedIndex(index);
  }

  /**
   * Selects the specified page component as active page.
   *
   * @param page	the component to select
   */
  public void setSelectedPage(Component page) {
    int		i;

    for (i = 0; i < m_PageListModel.getSize(); i++) {
      if (m_PageListModel.get(i).getPage() == page) {
	setSelectedIndex(i);
	break;
      }
    }
  }

  /**
   * Sets the container at the specified index.
   *
   * @param index	the page index
   * @param cont	the new container
   */
  public void setPageAt(int index, PageContainer cont) {
    m_PageListModel.set(index, cont);
    update();
  }

  /**
   * Returns the page container at the specified index.
   *
   * @param index	the page index
   * @return		the associated page container
   */
  public PageContainer getPageContainerAt(int index) {
    return (PageContainer) m_PageList.getModel().getElementAt(index);
  }

  /**
   * Returns the currently selected page container.
   *
   * @return		the page container, null if none selected
   */
  public PageContainer getSelectedPageContainer() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getPageContainerAt(getSelectedIndex());
  }

  /**
   * Sets the page at the specified index.
   *
   * @param index	the page index
   * @param page	the new page
   */
  public void setPageAt(int index, Component page) {
    getPageContainerAt(index).setPage(page);
    update();
  }

  /**
   * Returns the page component at the specified index.
   *
   * @param index	the page index
   * @return		the associated page component
   */
  public Component getPageAt(int index) {
    return getPageContainerAt(index).getPage();
  }

  /**
   * Returns the page index for the page component.
   *
   * @param page	the page component to look up
   * @return		the associated page index, -1 if not found
   */
  public int indexOfPage(Component page) {
    int		result;
    int		i;

    result = -1;
    for (i = 0; i < getPageCount(); i++) {
      if (getPageAt(i) == page) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the page index for the page container.
   *
   * @param cont	the page component to look up
   * @return		the associated page index, -1 if not found
   */
  public int indexOfPage(PageContainer cont) {
    int		result;
    int		i;

    result = -1;
    for (i = 0; i < getPageCount(); i++) {
      if (getPageContainerAt(i) == cont) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the currently selected page.
   *
   * @return		the page, null if none selected
   */
  public Component getSelectedPage() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getPageAt(getSelectedIndex());
  }

  /**
   * Sets the title at the specified index.
   *
   * @param index	the page index
   * @param title	the new title
   */
  public void setTitleAt(int index, String title) {
    getPageContainerAt(index).setTitle(title);
    update();
  }

  /**
   * Returns the title at the specified index.
   *
   * @param index	the page index
   * @return		the associated title
   */
  public String getTitleAt(int index) {
    return getPageContainerAt(index).getTitle();
  }

  /**
   * Returns the title of the currently selected page.
   *
   * @return		the title, null if none selected
   */
  public String getSelectedTitle() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getTitleAt(getSelectedIndex());
  }

  /**
   * Sets the icon at the specified index.
   *
   * @param index	the page index
   * @param icon	the new icon
   */
  public void setIconAt(int index, ImageIcon icon) {
    getPageContainerAt(index).setIcon(icon);
    update();
  }

  /**
   * Returns the icon at the specified index.
   *
   * @param index	the page index
   * @return		the associated icon
   */
  public ImageIcon getIconAt(int index) {
    return getPageContainerAt(index).getIcon();
  }

  /**
   * Returns the icon of the currently selected page.
   *
   * @return		the icon, null if none selected
   */
  public ImageIcon getSelectedIcon() {
    if (getSelectedIndex() == -1)
      return null;
    else
      return getIconAt(getSelectedIndex());
  }

  /**
   * Removes the currently selected page container, if approved.
   *
   * @return		the removed container, if any
   */
  public PageContainer checkedRemoveSelectedPage() {
    if (getSelectedIndex() == -1)
      return null;
    if ((m_PageCloseApprover == null) || (m_PageCloseApprover.approvePageClosing(this, getSelectedIndex())))
      return removeSelectedPage();
    else
      return null;
  }

  /**
   * Removes the currently selected page container.
   *
   * @return		the removed container
   */
  public PageContainer removeSelectedPage() {
    if (getSelectedIndex() > -1)
      return removePageAt(getSelectedIndex());
    else
      return null;
  }

  /**
   * Removes the page container at the specified index, if approved.
   *
   * @param index	the page index
   * @return		the removed container, null if not removed
   * @see		#m_PageCloseApprover
   */
  public PageContainer checkedRemovePageAt(int index) {
    if ((m_PageCloseApprover == null) || (m_PageCloseApprover.approvePageClosing(this, index)))
      return removePageAt(index);
    else
      return null;
  }

  /**
   * Removes the page container at the specified index.
   *
   * @param index	the page index
   * @return		the removed container
   */
  public PageContainer removePageAt(int index) {
    PageContainer	result;

    result = m_PageListModel.remove(index);
    if (index < getPageCount())
      setSelectedIndex(index);
    else if (index > 0)
      setSelectedIndex(index - 1);

    return result;
  }

  /**
   * Removes all pages.
   */
  public void removeAllPages() {
    m_IgnoreUpdates = true;

    while (getPageCount() > 0)
      removePageAt(0);

    m_IgnoreUpdates = false;
    update();
  }

  /**
   * Adds the page at the end.
   *
   * @param title	the title
   * @param page	the page component
   */
  public void addPage(String title, Component page) {
    addPage(new PageContainer(title, page));
  }

  /**
   * Adds the page at the end.
   *
   * @param cont	the page container
   */
  public void addPage(PageContainer cont) {
    m_PageListModel.addElement(cont);
    setSelectedIndex(getPageCount() - 1);
  }

  /**
   * Adds the page at the specified index.
   *
   * @param index	the page index to insert the page at
   * @param title	the title
   * @param page	the page component
   */
  public void addPage(int index, String title, Component page) {
    addPage(index, new PageContainer(title, page));
  }

  /**
   * Adds the page at the specified index.
   *
   * @param index	the page index to insert the page at
   * @param cont	the page container
   */
  public void addPage(int index, PageContainer cont) {
    m_PageListModel.add(index, cont);
    setSelectedIndex(index);
  }

  /**
   * moves the selected items up by 1.
   */
  public void moveUp() {
    m_PageList.moveUp();
  }

  /**
   * moves the selected item down by 1.
   */
  public void moveDown() {
    m_PageList.moveDown();
  }

  /**
   * moves the selected items to the top.
   */
  public void moveTop() {
    m_PageList.moveTop();
  }

  /**
   * moves the selected items to the end.
   */
  public void moveBottom() {
    m_PageList.moveBottom();
  }

  /**
   * checks whether the selected items can be moved up.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp() {
    return m_PageList.canMoveUp();
  }

  /**
   * checks whether the selected items can be moved down.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveDown() {
    return m_PageList.canMoveDown();
  }

  /**
   * Adds the remove items listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addRemoveItemsListener(RemoveItemsListener l) {
    m_PageList.addRemoveItemsListener(l);
  }

  /**
   * Removes the remove items listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeRemoveItemsListener(RemoveItemsListener l) {
    m_PageList.removeRemoveItemsListener(l);
  }

  /**
   * Adds the change listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies the change listeners.
   */
  protected void notifyChangeListeners() {
    ChangeEvent event;

    event = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(event);
  }

  /**
   * Updates the content panel.
   */
  protected void update() {
    Component	comp;

    if (m_IgnoreUpdates)
      return;

    m_PanelContent.removeAll();

    comp = getSelectedPage();
    if (comp != null)
      m_PanelContent.add(comp, BorderLayout.CENTER);

    m_PanelContent.invalidate();
    m_PanelContent.revalidate();
    m_PanelContent.repaint();

    m_PageList.repaint();

    updateButtons();
    notifyChangeListeners();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    boolean	pageSelected;

    pageSelected = (getPageCount() > 0) && (getSelectedIndex() > -1);

    m_ButtonUp.setEnabled(pageSelected && canMoveUp());
    m_ButtonDown.setEnabled(pageSelected && canMoveDown());
    m_ButtonRemove.setEnabled(pageSelected);
  }

  /**
   * Adds the action to the action button.
   *
   * @param action	the action to add
   */
  protected void addAction(Action action) {
    m_ButtonAction.addToMenu(action);
    m_ButtonAction.setVisible(true);
  }

  /**
   * Adds the menu item to the action button.
   *
   * @param action	the item to add
   */
  protected void addAction(JMenuItem action) {
    m_ButtonAction.addToMenu(action);
    m_ButtonAction.setVisible(true);
  }

  /**
   * Handles the key event from the page list.
   *
   * @param e		the event
   * @return		true if processed
   */
  protected boolean processListKey(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
      if (getSelectedIndex() > -1) {
	checkedRemoveSelectedPage();
	return true;
      }
    }
    return false;
  }

  /**
   * Sets the approver for closing pages.
   *
   * @param value	the approver, null to remove
   */
  public void setPageCloseApprover(PageCloseApprover value) {
    m_PageCloseApprover = value;
  }

  /**
   * Returns the approver for closing pages.
   *
   * @return		the approver, null if none set
   */
  public PageCloseApprover getPageCloseApprover() {
    return m_PageCloseApprover;
  }

  /**
   * Generates the right-click menu for the JList.
   * <br><br>
   * Derived classes should override this method instead of making use
   * of the PopupCustomizer.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   * @see		#showPopup(MouseEvent)
   */
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem	  	menuitem;
    final int		index;

    result = new BasePopupMenu();
    index  = getSelectedIndex();

    // remove
    menuitem = new JMenuItem("Remove");
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    menuitem.setEnabled(index > -1);
    menuitem.addActionListener((ActionEvent ae) -> removePageAt(index));
    result.add(menuitem);

    // remove all
    menuitem = new JMenuItem("Remove all");
    menuitem.setEnabled(m_PageListModel.getSize() > 0);
    menuitem.addActionListener((ActionEvent ae) -> removeAllPages());
    result.add(menuitem);

    return result;
  }

  /**
   * Generates and pops up the right-click menu on the JList.
   *
   * @param e		the event that triggered the popup
   * @see		#createPopup(MouseEvent)
   */
  protected void showPopup(MouseEvent e) {
    BasePopupMenu	menu;

    menu = createPopup(e);

    // customizer available?
    if (m_PopupCustomizer != null)
      m_PopupCustomizer.customizePopup(getSelectedIndex(), menu);

    menu.showAbsolute(this, e);
  }

  /**
   * Sets the popup customizer to use.
   *
   * @param value	the customizer, use null to turn off
   */
  public void setPopupCustomizer(PopupCustomizer value) {
    m_PopupCustomizer = value;
  }

  /**
   * Returns the currently set popup customizer.
   *
   * @return		the customizer, can be null if none set
   */
  public PopupCustomizer getPopupCustomizer() {
    return m_PopupCustomizer;
  }
}
