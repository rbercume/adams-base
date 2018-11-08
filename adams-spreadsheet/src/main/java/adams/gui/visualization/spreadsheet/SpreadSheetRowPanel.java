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
 * SpreadSheetRowPanel.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.spreadsheet;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.ArrayHistogram;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;
import adams.gui.core.Undo;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.event.PaintListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CoordinatesPaintlet;
import adams.gui.visualization.core.CoordinatesPaintlet.Coordinates;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.core.plot.TipTextCustomizer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A panel for displaying instances.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12198 $
 */
public class SpreadSheetRowPanel
  extends DataContainerPanelWithContainerList<SpreadSheetRow, SpreadSheetRowContainerManager, SpreadSheetRowContainer>
  implements PaintListener, TipTextCustomizer, AntiAliasingSupporter, HitDetectorSupporter<SpreadSheetRowPointHitDetector> {

  /** for serialization. */
  private static final long serialVersionUID = 7985845939008731534L;

  /** paintlet for drawing the graph. */
  protected AbstractSpreadSheetRowPaintlet m_RowPaintlet;

  /** paintlet for drawing the X-axis. */
  protected CoordinatesPaintlet m_CoordinatesPaintlet;

  /** the undo manager. */
  protected Undo m_Undo;

  /** whether to adjust to visible data or not. */
  protected boolean m_AdjustToVisibleData;

  /** the hit detector for the tooltip. */
  protected SpreadSheetRowPointHitDetector m_SpreadSheetRowPointHitDetector;

  /** the maximum number of columns for the tooltip. */
  protected int m_ToolTipMaxColumns;

  /** the maximum number of rows for the tooltip. */
  protected int m_ToolTipMaxRows;

  /** the zoom overview panel. */
  protected SpreadSheetRowZoomOverviewPanel m_PanelZoomOverview;

  /** the file chooser for saving a specific sequence. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the dialog for displaying a sequence. */
  protected List<SpreadSheetDialog> m_ViewDialogs;

  /** the dialog for the histogram setup. */
  protected HistogramFactory.SetupDialog m_HistogramSetup;

  /**
   * Initializes the panel.
   */
  public SpreadSheetRowPanel() {
    super();
  }

  /**
   * Initializes the panel.
   *
   * @param title	the title for the panel
   */
  public SpreadSheetRowPanel(String title) {
    super(title);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    m_Undo                  = null;
    m_AdjustToVisibleData   = true;
    m_HistogramSetup         = null;

    super.initialize();
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the container manager to use.
   *
   * @return		the container manager
   */
  @Override
  protected SpreadSheetRowContainerManager newContainerManager() {
    return new SpreadSheetRowContainerManager(this);
  }

  /**
   * Returns the paintlet used for painting the containers.
   *
   * @return		the paintlet
   */
  @Override
  public AbstractSpreadSheetRowPaintlet getContainerPaintlet() {
    return m_RowPaintlet;
  }

  /**
   * Returns the hit detector.
   *
   * @return		the hit detector
   */
  public SpreadSheetRowPointHitDetector getHitDetector() {
    return m_SpreadSheetRowPointHitDetector;
  }

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    Properties	props;
    JPanel	panel;

    super.initGUI();

    props = getProperties();

    m_ToolTipMaxColumns = props.getInteger("Plot.ToolTip.MaxColumns", 80);
    m_ToolTipMaxRows = props.getInteger("Plot.ToolTip.MaxRows", 40);

    setAdjustToVisibleData(props.getBoolean("Plot.AdjustToVisibleData", false));

    panel = new JPanel();
    panel.setMinimumSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    panel.setPreferredSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    m_SidePanel.add(panel, BorderLayout.SOUTH);

    getPlot().setPopupMenuCustomizer(this);

    // paintlets
    m_RowPaintlet = new SpreadSheetRowLinePaintlet();
    m_RowPaintlet.setStrokeThickness(props.getDouble("Plot.StrokeThickness", 1.0).floatValue());
    ((AntiAliasingSupporter) m_RowPaintlet).setAntiAliasingEnabled(props.getBoolean("Plot.AntiAliasing", true));
    m_RowPaintlet.setPanel(this);
    m_CoordinatesPaintlet = new CoordinatesPaintlet();
    m_CoordinatesPaintlet.setYInvisible(true);
    m_CoordinatesPaintlet.setPanel(this);
    m_CoordinatesPaintlet.setXColor(props.getColor("Plot.CoordinatesColor." + Coordinates.X, Color.DARK_GRAY));
    m_CoordinatesPaintlet.setYColor(props.getColor("Plot.CoordinatesColor." + Coordinates.Y, Color.DARK_GRAY));

    m_SpreadSheetRowPointHitDetector = new SpreadSheetRowPointHitDetector(this);
    getPlot().setTipTextCustomizer(this);

    try {
      getContainerManager().setColorProvider(
	(ColorProvider) OptionUtils.forAnyCommandLine(
	  ColorProvider.class,
	  props.getProperty("Plot.ColorProvider", DefaultColorProvider.class.getName())));
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + " - Failed to set the color provider:");
      getContainerManager().setColorProvider(new DefaultColorProvider());
    }

    m_PanelZoomOverview = new SpreadSheetRowZoomOverviewPanel();
    m_PlotWrapperPanel.add(m_PanelZoomOverview, BorderLayout.SOUTH);
    m_PanelZoomOverview.setDataContainerPanel(this);
  }

  /**
   * Returns the container list.
   *
   * @return		the list
   */
  @Override
  protected SpreadSheetRowContainerList createContainerList() {
    SpreadSheetRowContainerList 	result;

    result = new SpreadSheetRowContainerList();
    result.setTitle("Rows");
    result.setManager(getContainerManager());
    result.setAllowSearch(getProperties().getBoolean("ContainerList.AllowSearch", false));
    result.setPopupMenuSupplier(this);
    result.addTableModelListener((TableModelEvent e) -> {
      final ContainerTable table = result.getTable();
      if ((table.getRowCount() > 0) && (table.getSelectedRowCount() == 0)) {
	SwingUtilities.invokeLater(() -> table.getSelectionModel().addSelectionInterval(0, 0));
      }
    });

    return result;
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  public AbstractContainerManager getSequenceManager() {
    return m_Manager;
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    m_Undo = value;
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    return m_Undo;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return (m_Undo != null);
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return ((getPlot() != null) && (m_Manager != null));
  }

  /**
   * Updates the axes with the min/max of the new data.
   */
  @Override
  public void prepareUpdate() {
    List<SpreadSheetRowPoint>		points;
    Iterator<SpreadSheetRowPoint>	iter;
    SpreadSheetRowPoint			point;
    double 				minX;
    double 				maxX;
    double 				minY;
    double 				maxY;
    int					i;

    minX = Double.MAX_VALUE;
    maxX = -Double.MAX_VALUE;
    minY = 0;
    maxY = -Double.MAX_VALUE;

    for (i = 0; i < getContainerManager().count(); i++) {
      if (m_AdjustToVisibleData) {
	if (!getContainerManager().isVisible(i))
	  continue;
      }

      points = getContainerManager().get(i).getData().toList();

      if (points.size() == 0)
	continue;

      // determine min/max
      if (points.get(0).getX() < minX)
	minX = points.get(0).getX();
      if (points.get(points.size() - 1).getX() > maxX)
	maxX = points.get(points.size() - 1).getX();

      iter = points.iterator();
      while (iter.hasNext()) {
	point = iter.next();
	if (point.getY() > maxY)
	  maxY = point.getY();
	if (point.getY() < minY)
	  minY = point.getY();
      }
    }

    // update axes
    getPlot().getAxis(Axis.LEFT).setMinimum(minY);
    getPlot().getAxis(Axis.LEFT).setMaximum(maxY);
    getPlot().getAxis(Axis.BOTTOM).setMinimum(minX);
    getPlot().getAxis(Axis.BOTTOM).setMaximum(maxX);
  }

  /**
   * Displays the histograms for the given instances.
   *
   * @param data	the instances to display
   */
  public void showHistogram(List<SpreadSheetRowContainer> data) {
    HistogramFactory.Dialog	dialog;
    int				i;
    SpreadSheetRow		inst;

    // get parameters for histograms
    if (m_HistogramSetup == null) {
      if (getParentDialog() != null)
	m_HistogramSetup = HistogramFactory.getSetupDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_HistogramSetup = HistogramFactory.getSetupDialog(getParentFrame(), true);
    }
    m_HistogramSetup.setLocationRelativeTo(this);
    m_HistogramSetup.setVisible(true);
    if (m_HistogramSetup.getResult() != HistogramFactory.SetupDialog.APPROVE_OPTION)
      return;

    // generate histograms and display them
    if (getParentDialog() != null)
      dialog = HistogramFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(getParentFrame(), false);
    for (i = 0; i < data.size(); i++) {
      inst = data.get(i).getData();
      dialog.add((ArrayHistogram) m_HistogramSetup.getCurrent(), inst, data.get(i).getID());
    }
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Saves the specified instance as spreadsheet file.
   *
   * @param cont	the instance to save
   */
  public void saveInstance(SpreadSheetRowContainer cont) {
    int			retVal;
    SpreadSheetRow 	row;
    SpreadSheetWriter	writer;

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    row = cont.getData();
    writer = m_FileChooser.getWriter();
    if (!writer.write(row.toSpreadSheet(), m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(
	this, "Failed to save instance to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Views the specified instance in a table.
   *
   * @param cont	the instance to view
   */
  public void viewInstance(SpreadSheetRowContainer cont) {
    SpreadSheetRow 	row;
    SpreadSheetDialog	dialog;
    SpreadSheet		sheet;

    if (m_ViewDialogs == null)
      m_ViewDialogs = new ArrayList<>();

    row = cont.getData();
    sheet = row.toSpreadSheet();
    if (getParentDialog() != null)
      dialog = new SpreadSheetDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getParentFrame(), false);
    m_ViewDialogs.add(dialog);
    dialog.setTitle("Instance: " + cont.getDisplayID());
    dialog.setSize(
      GUIHelper.getInteger("DefaultSmallDialog.Height", 400),
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600));
    dialog.setLocationRelativeTo(this);
    dialog.setSpreadSheet(sheet);
    dialog.setVisible(true);
  }

  /**
   * Sets the zoom overview panel visible or hides it.
   *
   * @param value	if true then the panel is displayed
   */
  public void setZoomOverviewPanelVisible(boolean value) {
    m_PanelZoomOverview.setVisible(value);
  }

  /**
   * Returns whether the zoom overview panel is visible or not.
   *
   * @return		true if visible
   */
  public boolean isZoomOverviewPanelVisible() {
    return m_PanelZoomOverview.isVisible();
  }

  /**
   * Returns the zoom overview panel.
   *
   * @return		the panel
   */
  public SpreadSheetRowZoomOverviewPanel getZoomOverviewPanel() {
    return m_PanelZoomOverview;
  }

  /**
   * Sets whether the display is adjusted to only the visible data or
   * everything currently loaded.
   *
   * @param value	if true then plot is adjusted to visible data
   */
  public void setAdjustToVisibleData(boolean value) {
    m_AdjustToVisibleData = value;
    update();
  }

  /**
   * Returns whether the display is adjusted to only the visible spectrums
   * or all of them.
   *
   * @return		true if the plot is adjusted to only the visible data
   */
  public boolean getAdjustToVisibleData() {
    return m_AdjustToVisibleData;
  }

  /**
   * Returns the paintlet used for painting the data.
   *
   * @return		the paintlet
   */
  @Override
  public Paintlet getDataPaintlet() {
    return m_RowPaintlet;
  }

  /**
   * Sets the paintlet to use for painting the data.
   *
   * @param value	the paintlet
   */
  public void setDataPaintlet(Paintlet value) {
    removePaintlet(m_RowPaintlet);
    m_RowPaintlet = (AbstractSpreadSheetRowPaintlet) value;
    m_RowPaintlet.setPanel(this);
    addPaintlet(m_RowPaintlet);
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    if (m_RowPaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_RowPaintlet).setAntiAliasingEnabled(value);
    if (m_PanelZoomOverview.getContainerPaintlet() instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_PanelZoomOverview.getContainerPaintlet()).setAntiAliasingEnabled(value);
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return (m_RowPaintlet instanceof AntiAliasingSupporter)
      && ((AntiAliasingSupporter) m_RowPaintlet).isAntiAliasingEnabled();
  }

  /**
   * Returns true if storing the color in the report of container's data object
   * is supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsStoreColorInReport() {
    return true;
  }

  /**
   * Stores the color of the container in the report of container's
   * data object.
   *
   * @param indices	the indices of the containers of the container manager
   * @param name	the field name to use
   */
  @Override
  public void storeColorInReport(int[] indices, String name) {
    Field 			field;
    SpreadSheetRowContainer	cont;

    field = new Field(name, DataType.STRING);
    for (int index: indices) {
      cont = getContainerManager().get(index);
      cont.getData().getReport().addField(field);
      cont.getData().getReport().setValue(field, ColorHelper.toHex(cont.getColor()));
    }
  }

  /**
   * Returns the currently visible instances.
   *
   * @return		the instances, null if none visible
   */
  public SpreadSheet getSpreadSheet() {
    SpreadSheet				result;
    SpreadSheetRow 			row;
    int					i;
    List<SpreadSheetRowContainer>	list;

    result = null;

    list = getContainerManager().getAllVisible();
    for (i = 0; i < list.size(); i++) {
      row = list.get(i).getData();
      if (result == null)
	result = row.getDatasetHeader();
      result.addRow().assign(row.toRow());
    }

    return result;
  }

  /**
   * Processes the given tip text. Among the current mouse position, the
   * panel that initiated the call are also provided.
   *
   * @param panel	the content panel that initiated this call
   * @param mouse	the mouse position
   * @param tiptext	the tiptext so far
   * @return		the processed tiptext
   */
  @Override
  public String processTipText(PlotPanel panel, Point mouse, String tiptext) {
    String	result;
    MouseEvent	event;
    String	hit;

    result = tiptext;
    event  = new MouseEvent(
      getPlot().getContent(),
      MouseEvent.MOUSE_MOVED,
      new Date().getTime(),
      0,
      (int) mouse.getX(),
      (int) mouse.getY(),
      0,
      false);

    hit = m_SpreadSheetRowPointHitDetector.detect(event);
    if (hit != null)
      result = GUIHelper.processTipText(hit, m_ToolTipMaxColumns, m_ToolTipMaxRows);

    return result;
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  @Override
  public AbstractScriptingEngine getScriptingEngine() {
    return ScriptingEngine.getSingleton(getDatabaseConnection());
  }

  /**
   * Hook method, called after the update was performed.
   */
  @Override
  protected void postUpdate() {
    super.postUpdate();

    if (m_PanelZoomOverview != null)
      m_PanelZoomOverview.update();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_SpreadSheetRowPointHitDetector != null) {
      m_SpreadSheetRowPointHitDetector.cleanUp();
      m_SpreadSheetRowPointHitDetector = null;
    }

    if (m_ViewDialogs != null) {
      for (SpreadSheetDialog dialog: m_ViewDialogs)
	dialog.dispose();
      m_ViewDialogs.clear();
      m_ViewDialogs = null;
    }

    if (m_HistogramSetup != null) {
      m_HistogramSetup.dispose();
      m_HistogramSetup = null;
    }

    super.cleanUp();
  }
}
