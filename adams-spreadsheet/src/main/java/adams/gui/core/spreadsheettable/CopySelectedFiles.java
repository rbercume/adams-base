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
 * CopySelectedFiles.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.PropertiesParameterDialog;

import java.awt.Dialog.ModalityType;
import java.io.File;

/**
 * Allows copying of the selected files to a target directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CopySelectedFiles
  extends AbstractProcessSelectedRows {

  private static final long serialVersionUID = 7786133414905315983L;

  public static final String KEY_COLUMN = "column";

  public static final String KEY_TARGETDIR = "targetdir";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to copy the selected files in the specified column.";
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  @Override
  public String getIconName() {
    return "copy.gif";
  }

  /**
   * Returns the minimum number of rows that the plugin requires.
   *
   * @return		the minimum
   */
  @Override
  public int minNumRows() {
    return 1;
  }

  /**
   * Returns the maximum number of rows that the plugin requires.
   *
   * @return		the maximum, -1 for none
   */
  @Override
  public int maxNumRows() {
    return -1;
  }

  /**
   * Returns the default name for the menu item.
   *
   * @return            the name
   */
  protected String getDefaultMenuItem() {
    return "Copy selected file(s)";
  }

  /**
   * Prompts the user to configure the parameters.
   *
   * @param table	the table to do this for
   * @return		the parameters, null if cancelled
   */
  protected Properties promptParameters(SpreadSheetTable table) {
    PropertiesParameterDialog 	dialog;
    PropertiesParameterPanel 	panel;
    Properties			last;

    if (GUIHelper.getParentDialog(table) != null)
      dialog = new PropertiesParameterDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new PropertiesParameterDialog(GUIHelper.getParentFrame(table), true);
    panel = dialog.getPropertiesParameterPanel();
    panel.addPropertyType(KEY_COLUMN, PropertyType.INDEX);
    panel.setLabel(KEY_COLUMN, "Column");
    panel.setHelp(KEY_COLUMN, "The column with the file names");
    panel.addPropertyType(KEY_TARGETDIR, PropertyType.DIRECTORY_ABSOLUTE);
    panel.setLabel(KEY_TARGETDIR, "Target dir");
    panel.setHelp(KEY_TARGETDIR, "The directory to copy the files to");
    panel.setPropertyOrder(new String[]{KEY_COLUMN, KEY_TARGETDIR});
    last = new Properties();
    last.setProperty(KEY_COLUMN, SpreadSheetColumnIndex.FIRST);
    last.setPath(KEY_TARGETDIR, new PlaceholderDirectory().getAbsolutePath());
    dialog.setProperties(last);
    last = (Properties) table.getLastSetup(getClass(), false, true);
    if (last != null)
      dialog.setProperties(last);
    dialog.setTitle(getMenuItem());
    dialog.pack();
    dialog.setLocationRelativeTo(table.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != PropertiesParameterDialog.APPROVE_OPTION)
      return null;

    return dialog.getProperties();
  }

  /**
   * Processes the specified rows.
   *
   * @param state	the table state
   * @return		true if successful
   */
  @Override
  protected boolean doProcessSelectedRows(TableState state) {
    Properties 			last;
    int				col;
    File			sourceFile;
    File			targetDir;
    MessageCollection		errors;
    SpreadSheetColumnIndex 	column;
    SpreadSheet			sheet;

    last = promptParameters(state.table);
    if (last == null)
      return false;

    // determine column
    sheet = state.table.toSpreadSheet(state.range, true);
    column = new SpreadSheetColumnIndex(last.getProperty(KEY_COLUMN, SpreadSheetColumnIndex.FIRST));
    column.setData(sheet);
    col = column.getIntIndex();
    if (col == -1) {
      GUIHelper.showErrorMessage(state.table.getParent(), "Failed to locate column:" + column);
      return false;
    }
    targetDir = new PlaceholderDirectory(last.getPath(KEY_TARGETDIR, new PlaceholderDirectory().getAbsolutePath()));

    // store setup
    state.table.addLastSetup(getClass(), false, false, last);

    // copy
    errors = new MessageCollection();
    for (int row: state.actRows) {
      sourceFile = new PlaceholderFile(sheet.getCell(row, col).toString());
      try {
	if (!FileUtils.copy(sourceFile, targetDir))
	  errors.add("Failed to copy '" + sourceFile + "' to '" + targetDir + "'!");
      }
      catch (Exception e) {
	errors.add("Failed to copy '" + sourceFile + "' to '" + targetDir + "'!", e);
      }
    }

    if (!errors.isEmpty())
      GUIHelper.showErrorMessage(state.table.getParent(), "Failed to copy files:\n" + errors);

    return errors.isEmpty();
  }
}
