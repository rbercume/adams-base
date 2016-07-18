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
 * DataTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.io.PlaceholderFile;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.FileContainer;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils.DataSink;
import weka.gui.arffviewer.ArffSortedTableModel;
import weka.gui.arffviewer.ArffTable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Lists the currently loaded datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -94945456385486233L;

  /** the table model. */
  protected DataTableModel m_Model;

  /** the table. */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** the button for removing a dataset. */
  protected JButton m_ButtonRemove;

  /** the button for exporting a dataset. */
  protected JButton m_ButtonExport;

  /** the panel with the data. */
  protected JPanel m_PanelData;

  /** the file chooser for exporting. */
  protected WekaFileChooser m_FileChooser;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new WekaFileChooser();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    add(m_SplitPane, BorderLayout.CENTER);

    m_Model = new DataTableModel(new ArrayList<>());
    m_Table = new SortableAndSearchableTableWithButtons(m_Model);
    m_Table.setPreferredSize(new Dimension(200, 150));
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
      updateButtons();
      displayData();
    });
    m_SplitPane.setTopComponent(new BaseScrollPane(m_Table));
    m_SplitPane.setTopComponentHidden(false);

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeData(m_Table.getSelectedRows()));
    m_Table.addToButtonsPanel(m_ButtonRemove);

    m_ButtonExport = new JButton("Export...");
    m_ButtonExport.addActionListener((ActionEvent e) -> exportData(m_Table.getSelectedRows()));
    m_Table.addToButtonsPanel(m_ButtonExport);

    m_PanelData = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(m_PanelData);
    m_SplitPane.setBottomComponentHidden(true);
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Data";
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    ArffTable			table;
    int				index;
    ArffSortedTableModel	model;

    if (m_Table.getSelectedRow() > -1) {
      index = m_Table.getActualRow(m_Table.getSelectedRow());
      model = new ArffSortedTableModel(getData().get(index).getData());
      table = new ArffTable(model);
      m_PanelData.removeAll();
      m_PanelData.add(new BaseScrollPane(table), BorderLayout.CENTER);
      if (m_SplitPane.isBottomComponentHidden()) {
	m_SplitPane.setDividerLocation(150);
	m_SplitPane.setBottomComponentHidden(false);
      }
    }
    else {
      m_PanelData.removeAll();
      m_SplitPane.setBottomComponentHidden(true);
    }
    invalidate();
    revalidate();
    doLayout();
  }

  /**
   * Notifies the tab that the data changed.
   */
  @Override
  public void dataChanged() {
    m_Model = new DataTableModel(getData());
    m_Table.setModel(m_Model);
    m_Table.setOptimalColumnWidth();
    if (m_Table.getSelectedRow() == -1) {
      if (m_Model.getRowCount() > 0)
	m_Table.getComponent().setRowSelectionInterval(0, 0);
    }
    updateButtons();
  }

  /**
   * Removes the selected rows, removes all if rows are null.
   *
   * @param rows	the rows to remove, null for all
   */
  protected void removeData(int[] rows) {
    int[]	actRows;
    int		i;

    if (rows == null) {
      getData().clear();
      fireDataChange();
    }
    else {
      actRows = new int[rows.length];
      for (i = 0; i < actRows.length; i++)
	actRows[i] = m_Table.getActualRow(rows[i]);
      Arrays.sort(actRows);
      for (i = actRows.length - 1; i >= 0; i--) {
	logMessage("Removing: " + getData().get(i).getSourceFull());
	getData().remove(actRows[i]);
      }
      fireDataChange();
    }
  }

  /**
   * Exports the selected rows.
   *
   * @param rows	the rows to export
   */
  protected void exportData(int[] rows) {
    int			actRow;
    int			i;
    DataContainer	data;
    FileContainer	cont;
    int			retVal;
    AbstractFileSaver	saver;

    for (i = 0; i < rows.length; i++) {
      actRow = m_Table.getActualRow(rows[i]);
      data   = getData().get(actRow);
      m_FileChooser.setDialogTitle("Exporting " + (i+1) + "/" + (rows.length) + ": " + data.getData().relationName());
      m_FileChooser.setSelectedFile(new PlaceholderFile(m_FileChooser.getCurrentDirectory().getAbsolutePath() + File.separator + data.getSourceShort()));
      retVal = m_FileChooser.showSaveDialog(this);
      if (retVal != WekaFileChooser.APPROVE_OPTION)
	break;
      try {
	logMessage("Exporting: " + data.getSourceFull());
	saver = m_FileChooser.getWriter();
	saver.setFile(m_FileChooser.getSelectedFile());
	DataSink.write(saver, data.getData());
	logMessage("Exported: " + m_FileChooser.getSelectedFile());
	cont = new FileContainer(m_FileChooser.getReaderForFile(m_FileChooser.getSelectedFile()), m_FileChooser.getSelectedFile());
	getData().set(actRow, cont);
      }
      catch (Exception e) {
	logError("Failed to export: " + m_FileChooser.getSelectedFile() + "\n", e, "Export");
	break;
      }
    }
    fireDataChange();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonRemove.setEnabled(m_Table.getSelectedRowCount() > 0);
    m_ButtonExport.setEnabled(m_Table.getSelectedRowCount() > 0);
  }
}
