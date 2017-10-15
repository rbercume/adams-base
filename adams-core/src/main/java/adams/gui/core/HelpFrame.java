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
 * HelpPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.gui.core.BrowserHelper.DefaultHyperlinkListener;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * For displaying help as plain text or html.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HelpFrame
  extends BaseFrame {

  private static final long serialVersionUID = -2182546218856998120L;

  /** the singleton. */
  protected static HelpFrame m_Singleton;

  /** for displaying the help. */
  protected HelpHistoryPanel m_PanelHistory;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the editor pane. */
  protected JEditorPane m_Text;

  /**
   * Initializes the frame.
   */
  protected HelpFrame() {
    super();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Help");
    setDefaultCloseOperation(BaseFrame.HIDE_ON_CLOSE); // TODO does this prevent application from closing?
    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(200);
    add(m_SplitPane, BorderLayout.CENTER);

    m_Text = new JEditorPane();
    m_Text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_Text.setEditable(false);
    m_Text.setFont(Fonts.getMonospacedFont());
    m_Text.setAutoscrolls(true);
    m_Text.addHyperlinkListener(new DefaultHyperlinkListener());
    m_Text.addKeyListener(getKeyListener());
    m_Text.setFont(Fonts.getMonospacedFont());
    m_SplitPane.setRightComponent(new BaseScrollPane(m_Text));

    m_PanelHistory = new HelpHistoryPanel();
    m_PanelHistory.setText(m_Text);
    m_PanelHistory.setCaretAtStart(true);
    m_PanelHistory.setAllowSearch(true);
    m_SplitPane.setLeftComponent(m_PanelHistory);

    // TODO clear all button?
  }

  /**
   * Returns the {@link KeyListener} to use for text and button.
   *
   * @return		the listener
   */
  protected KeyListener getKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if ((e.getKeyCode() == KeyEvent.VK_W) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
	  e.consume();
	  setVisible(false);
	}
	else {
	  super.keyPressed(e);
	}
      }
    };
  }

  /**
   * Shows the help.
   *
   * @param key 	the key for the help
   * @param cont	the help
   */
  public void showHelp(String key, HelpContainer cont) {
    m_PanelHistory.addEntry(key, cont);
    m_PanelHistory.setSelectedEntry(key);
  }

  /**
   * Gives access to the singleton.
   *
   * @return		the singleton
   */
  public static synchronized HelpFrame getSingleton() {
    if (m_Singleton == null) {
      m_Singleton = new HelpFrame();
      m_Singleton.setSize(GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension()));
      m_Singleton.setLocationRelativeTo(null);
    }
    return m_Singleton;
  }

  /**
   * Shows the help in the frame.
   *
   * @param cls		the class identifying the help screen
   * @param help	the help to display
   * @param html	true if to display as HTML
   */
  public static void showHelp(Class cls, String help, boolean html) {
    showHelp(cls.getName(), help, html);
  }

  /**
   * Shows the help in the frame.
   *
   * @param key		the key identifying the help screen
   * @param help	the help to display
   * @param html	true if to display as HTML
   */
  public static void showHelp(String key, String help, boolean html) {
    HelpContainer	cont;
    HelpFrame		frame;

    cont = new HelpContainer(help, html);
    frame = getSingleton();
    frame.showHelp(key, cont);
    frame.setVisible(true);
  }
}
