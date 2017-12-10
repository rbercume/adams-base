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
 * ObjectCentersOverlayFromReport.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.Fonts;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Ancestor for overlays that use object locations from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 198 $
 */
public class ReportObjectOverlay
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = "Object.";

  /** the prefix for the objects in the report. */
  protected String m_Prefix;

  /** the color for the objects. */
  protected Color m_Color;

  /** whether to use colors per type. */
  protected boolean m_UseColorsPerType;

  /** the color provider to use. */
  protected ColorProvider m_TypeColorProvider;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the label for the rectangles. */
  protected String m_LabelFormat;

  /** the label font. */
  protected Font m_LabelFont;

  /** the cached locations. */
  protected List<Rectangle> m_Locations;

  /** the type/color mapping. */
  protected HashMap<String,Color> m_TypeColors;

  /** the cached colors. */
  protected HashMap<Rectangle,Color> m_Colors;

  /** the labels. */
  protected HashMap<Rectangle,String> m_Labels;

  /** predefined labels. */
  protected BaseString[] m_PredefinedLabels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes colors and labels for objects in report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"prefix", "prefix",
	PREFIX_DEFAULT);

    m_OptionManager.add(
	"color", "color",
	Color.RED);

    m_OptionManager.add(
	"use-colors-per-type", "useColorsPerType",
	false);

    m_OptionManager.add(
	"type-color-provider", "typeColorProvider",
	new DefaultColorProvider());

    m_OptionManager.add(
	"type-suffix", "typeSuffix",
	".type");

    m_OptionManager.add(
	"label-format", "labelFormat",
	"#");

    m_OptionManager.add(
	"label-font", "labelFont",
	Fonts.getSansFont(14));

    m_OptionManager.add(
	"predefined-labels", "predefinedLabels",
	new BaseString[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_Locations  = null;
    m_TypeColors = new HashMap<>();
    m_Colors     = new HashMap<>();
    m_Labels     = new HashMap<>();
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects in the report.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix of fields in the report to identify as object location, eg 'Object.'.";
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use for the objects.";
  }

  /**
   * Sets whether to use colors per type.
   *
   * @param value 	true if to use colors per type
   */
  public void setUseColorsPerType(boolean value) {
    m_UseColorsPerType = value;
    reset();
  }

  /**
   * Returns whether to use colors per type.
   *
   * @return 		true if to use colors per type
   */
  public boolean getUseColorsPerType() {
    return m_UseColorsPerType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColorsPerTypeTipText() {
    return "If enabled, individual colors per type are used.";
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setTypeColorProvider(ColorProvider value) {
    m_TypeColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the types.
   *
   * @return 		the provider
   */
  public ColorProvider getTypeColorProvider() {
    return m_TypeColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColorProviderTipText() {
    return "The color provider to use for the various types.";
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_TypeSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return "The suffix of fields in the report to identify the type.";
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setLabelFormat(String value) {
    m_LabelFormat = value;
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getLabelFormat() {
    return m_LabelFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFormatTipText() {
    return "The label format string to use for the rectangles; '#' for index, '@' for type and '$' for short type (type suffix must be defined for '@' and '$'); for instance: '# @'.";
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setLabelFont(Font value) {
    m_LabelFont = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_LabelFont;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return "The font to use for the labels.";
  }

  /**
   * Sets the predefined labels.
   *
   * @param value	the labels
   */
  public void setPredefinedLabels(BaseString[] value) {
    m_PredefinedLabels = value;
    reset();
  }

  /**
   * Returns the predefined labels.
   *
   * @return		the labels
   */
  public BaseString[] getPredefinedLabels() {
    return m_PredefinedLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predefinedLabelsTipText() {
    return "The predefined labels to use for setting up the colors; avoids constants changing in color pallet.";
  }

  /**
   * Determines the locations of the objects.
   * 
   * @param report	the report to inspect
   */
  public void determineLocations(Report report) {
    LocatedObjects	located;
    HashSet<String>	types;
    Rectangle		rect;
    String		suffix;
    String		type;
    Color		color;
    String		label;

    if (m_Locations != null)
      return;
    if (report == null)
      return;

    // initialize colors
    if (m_UseColorsPerType) {
      m_TypeColors.clear();
      m_TypeColorProvider.resetColors();
      types = new HashSet<>();
      for (BaseString predefined: m_PredefinedLabels)
	m_TypeColors.put(predefined.getValue(), m_TypeColorProvider.next());
      for (AbstractField field: report.getFields()) {
	if (field.getName().endsWith(m_TypeSuffix))
	  types.add("" + report.getValue(field));
      }
      for (String t: types) {
        if (!m_TypeColors.containsKey(t))
	  m_TypeColors.put(t, m_TypeColorProvider.next());
      }
    }

    m_Locations = new ArrayList<>();
    m_Colors    = new HashMap<>();
    m_Labels    = new HashMap<>();
    suffix      = m_TypeSuffix.isEmpty() ? "" : m_TypeSuffix.substring(1);
    located     = LocatedObjects.fromReport(report, m_Prefix);
    for (LocatedObject object: located) {
      m_Locations.add(object.getRectangle());

      color = m_Color;
      rect  = object.getRectangle();

      if (!suffix.isEmpty() && (object.getMetaData() != null) && (object.getMetaData().containsKey(suffix))) {
	type  = "" + object.getMetaData().get(suffix);
	// color per type?
	if (m_UseColorsPerType) {
	  if (m_TypeColors.containsKey(type))
	    color = m_TypeColors.get(type);
	}

	// label?
	if (!m_LabelFormat.isEmpty()) {
	  label = m_LabelFormat
	    .replace("#", "" + object.getMetaData().get(LocatedObjects.KEY_INDEX))
	    .replace("@", type)
	    .replace("$", type.replaceAll(".*\\.", ""));
	  m_Labels.put(rect, label);
	}
      }
      else {
	// label?
	if (!m_LabelFormat.isEmpty()) {
	  label = m_LabelFormat
	    .replace("#", "" + object.getMetaData().get(LocatedObjects.KEY_INDEX))
	    .replace("@", "")
	    .replace("$", "");
	  m_Labels.put(rect, label);
	}
      }

      m_Colors.put(rect, color);
    }
  }

  /**
   * Checks whether a color has been stored for the given object.
   *
   * @param rect	the object to check
   * @return		true if custom color available
   */
  public boolean hasColor(Rectangle rect) {
    return m_Colors.containsKey(rect);
  }

  /**
   * Returns the color for the object.
   *
   * @param rect	the object to get the color for
   * @return		the color, null if none available
   */
  public Color getColor(Rectangle rect) {
    return m_Colors.get(rect);
  }

  /**
   * Checks whether a color has been stored for the given object type.
   *
   * @param type	the type to check
   * @return		true if custom color available
   */
  public boolean hasTypeColor(String type) {
    return m_UseColorsPerType && m_Colors.containsKey(type);
  }

  /**
   * Returns the color for the object type.
   *
   * @param type	the type to get the color for
   * @return		the color, null if none available
   */
  public Color getTypeColor(String type) {
    return m_TypeColors.get(type);
  }

  /**
   * Checks whether a label has been stored for the given object.
   *
   * @param rect	the object to check
   * @return		true if custom label available
   */
  public boolean hasLabel(Rectangle rect) {
    return !m_LabelFormat.isEmpty() && m_Labels.containsKey(rect);
  }

  /**
   * Returns the label for the object.
   *
   * @param rect	the object to get the label for
   * @return		the label, null if none available
   */
  public String getLabel(Rectangle rect) {
    return m_Labels.get(rect);
  }

  /**
   * Checks whether any locations are available.
   *
   * @return		true if locations available
   */
  public boolean hasLocations() {
    return (m_Locations != null) && (m_Locations.size() > 0);
  }

  /**
   * Returns the current locations.
   *
   * @return		the locations, null if not initialized
   * @see		#determineLocations(Report)
   */
  public List<Rectangle> getLocations() {
    return m_Locations;
  }
}