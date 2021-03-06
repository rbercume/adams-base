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
 * SpreadSheetMerge.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.columnfinder.AbstractColumnFinder;
import adams.data.spreadsheet.columnfinder.ByName;
import adams.data.spreadsheet.columnfinder.ColumnFinder;
import adams.data.spreadsheet.columnfinder.Invert;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Merges two or more spreadsheets. The merge can be done by using a common key-column or by simply putting the spreadsheets side-by-side.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetMerge
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-use-prefix &lt;boolean&gt; (property: usePrefix)
 * &nbsp;&nbsp;&nbsp;Whether to prefix the attribute names of each dataset with an index and 
 * &nbsp;&nbsp;&nbsp;an optional string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-add-index &lt;boolean&gt; (property: addIndex)
 * &nbsp;&nbsp;&nbsp;Whether to add the index of the dataset to the prefix.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-remove &lt;boolean&gt; (property: remove)
 * &nbsp;&nbsp;&nbsp;If true, only keep instances where data is available from each source.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The optional prefix string to prefix the index number with (in case prefixes 
 * &nbsp;&nbsp;&nbsp;are used).
 * &nbsp;&nbsp;&nbsp;default: dataset
 * </pre>
 * 
 * <pre>-prefix-separator &lt;java.lang.String&gt; (property: prefixSeparator)
 * &nbsp;&nbsp;&nbsp;The separator string between the generated prefix and the original attribute 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: -
 * </pre>
 * 
 * <pre>-exclude-atts &lt;java.lang.String&gt; (property: excludedAttributes)
 * &nbsp;&nbsp;&nbsp;The regular expression used on the attribute names, to determine whether 
 * &nbsp;&nbsp;&nbsp;an attribute should be excluded or not (matching sense can be inverted); 
 * &nbsp;&nbsp;&nbsp;leave empty to include all attributes.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-invert &lt;boolean&gt; (property: invertMatchingSense)
 * &nbsp;&nbsp;&nbsp;Whether to invert the matching sense of excluding attributes, ie, the regular 
 * &nbsp;&nbsp;&nbsp;expression is used for including attributes.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-unique-id &lt;java.lang.String&gt; (property: uniqueID)
 * &nbsp;&nbsp;&nbsp;The name of the column used for uniquely identifying rows among the spreadsheets.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-keep-only-single-unique-id &lt;boolean&gt; (property: keepOnlySingleUniqueID)
 * &nbsp;&nbsp;&nbsp;If enabled, only a single instance of the unique ID attribute is kept.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-strict &lt;boolean&gt; (property: strict)
 * &nbsp;&nbsp;&nbsp;If enabled, ensures that IDs in unique ID column are truly unique.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetMerge
  extends AbstractTransformer
  implements SpreadSheetMergeActor, ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = 3363405805013155845L;

  /** whether to prefix the attribute names of each dataset with an index. */
  protected boolean m_UsePrefix;

  /** whether to add the index to the prefix. */
  protected boolean m_AddIndex;

  /** whether to remove when not all present. */
  protected boolean m_Remove;

  /** the additional prefix name to use, apart from the index. */
  protected String m_Prefix;

  /** the separator between index and actual attribute name. */
  protected String m_PrefixSeparator;

  /** regular expression for excluding attributes from the datasets. */
  protected String m_ExcludedAttributes;

  /** whether to invert the matching sense for excluding attributes. */
  protected boolean m_InvertMatchingSense;

  /** the string or numeric attribute to use as unique identifier for rows. */
  protected String m_UniqueID;

  /** whether to keep only a single instance of the unique ID attribute. */
  protected boolean m_KeepOnlySingleUniqueID;

  /** whether to fail if IDs not unique. */
  protected boolean m_Strict;

  /** the unique ID attributes. */
  protected List<String> m_UniqueIDAtts;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Merges two or more spreadsheets. The merge can be done by using "
	+ "a common key-column or by simply putting the spreadsheets side-by-side.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{SpreadSheetMethodMerge.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"use-prefix", "usePrefix",
	false);

    m_OptionManager.add(
	"add-index", "addIndex",
	false);

    m_OptionManager.add(
	"remove", "remove",
	false);

    m_OptionManager.add(
	"prefix", "prefix",
	"dataset");

    m_OptionManager.add(
	"prefix-separator", "prefixSeparator",
	"-");

    m_OptionManager.add(
	"exclude-atts", "excludedAttributes",
	"");

    m_OptionManager.add(
	"invert", "invertMatchingSense",
	false);

    m_OptionManager.add(
	"unique-id", "uniqueID",
	"");

    m_OptionManager.add(
	"keep-only-single-unique-id", "keepOnlySingleUniqueID",
	false);

    m_OptionManager.add(
	"strict", "strict",
	false);
  }

  /**
   * Sets whether to remove if not all present
   *
   * @param value	if true then remove instance if not all there to merge
   */
  public void setRemove(boolean value) {
    m_Remove = value;
    reset();
  }

  /**
   * Returns whether to remove if not all present
   *
   * @return		if true then remove instance if not all there to merge
   */
  public boolean getRemove() {
    return m_Remove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeTipText() {
    return
	"If true, only keep instances where data is available from each source.";
  }

  /**
   * Sets whether to use prefixes.
   *
   * @param value	if true then the attributes will get prefixed
   */
  public void setUsePrefix(boolean value) {
    m_UsePrefix = value;
    reset();
  }

  /**
   * Returns whether to use prefixes.
   *
   * @return		true if the attributes will get prefixed
   */
  public boolean getUsePrefix() {
    return m_UsePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePrefixTipText() {
    return
	"Whether to prefix the attribute names of each dataset with an index "
	+ "and an optional string.";
  }

  /**
   * Sets whether to add the dataset index number to the prefix.
   *
   * @param value	if true then the index will be used in the prefix
   */
  public void setAddIndex(boolean value) {
    m_AddIndex = value;
    reset();
  }

  /**
   * Returns whether to add the dataset index number to the prefix.
   *
   * @return		true if the index will be used in the prefix
   */
  public boolean getAddIndex() {
    return m_AddIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addIndexTipText() {
    return "Whether to add the index of the dataset to the prefix.";
  }

  /**
   * Sets the optional prefix string.
   *
   * @param value	the optional prefix string
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the optional prefix string.
   *
   * @return		the optional prefix string
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
    return
	"The optional prefix string to prefix the index number with (in "
	+ "case prefixes are used).";
  }

  /**
   * Sets the prefix separator string.
   *
   * @param value	the prefix separator string
   */
  public void setPrefixSeparator(String value) {
    m_PrefixSeparator = value;
    reset();
  }

  /**
   * Returns the prefix separator string.
   *
   * @return		the prefix separator string
   */
  public String getPrefixSeparator() {
    return m_PrefixSeparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixSeparatorTipText() {
    return
	"The separator string between the generated prefix and the original "
	+ "attribute name.";
  }

  /**
   * Sets the regular expression for excluding attributes.
   *
   * @param value	the regular expression
   */
  public void setExcludedAttributes(String value) {
    m_ExcludedAttributes = value;
    reset();
  }

  /**
   * Returns the prefix separator string.
   *
   * @return		the prefix separator string
   */
  public String getExcludedAttributes() {
    return m_ExcludedAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String excludedAttributesTipText() {
    return
	"The regular expression used on the attribute names, to determine whether "
	+ "an attribute should be excluded or not (matching sense can be inverted); "
	+ "leave empty to include all attributes.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	if true then matching sense gets inverted
   */
  public void setInvertMatchingSense(boolean value) {
    m_InvertMatchingSense = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if the attributes will get prefixed
   */
  public boolean getInvertMatchingSense() {
    return m_InvertMatchingSense;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertMatchingSenseTipText() {
    return
	"Whether to invert the matching sense of excluding attributes, ie, "
	+ "the regular expression is used for including attributes.";
  }

  /**
   * Sets the attribute (string/numeric) to use for uniquely identifying rows.
   *
   * @param value	the attribute name
   */
  public void setUniqueID(String value) {
    m_UniqueID = value;
    reset();
  }

  /**
   * Returns the attribute (string/numeric) to use for uniquely identifying rows.
   *
   * @return		the attribute name
   */
  public String getUniqueID() {
    return m_UniqueID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String uniqueIDTipText() {
    return
	"The name of the column used for uniquely "
	+ "identifying rows among the spreadsheets.";
  }

  /**
   * Sets whether to keep only a single instance of the unique ID attribute.
   *
   * @param value	true if to keep only single instance
   */
  public void setKeepOnlySingleUniqueID(boolean value) {
    m_KeepOnlySingleUniqueID = value;
    reset();
  }

  /**
   * Returns whether to keep only a single instance of the unique ID attribute.
   *
   * @return		true if to keep only single instance
   */
  public boolean getKeepOnlySingleUniqueID() {
    return m_KeepOnlySingleUniqueID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepOnlySingleUniqueIDTipText() {
    return
	"If enabled, only a single instance of the unique ID attribute is kept.";
  }

  /**
   * Sets whether to enforce uniqueness in IDs.
   *
   * @param value	true if to enforce
   */
  public void setStrict(boolean value) {
    m_Strict = value;
    reset();
  }

  /**
   * Returns whether to enforce uniqueness in IDs.
   *
   * @return		true if to enforce
   */
  public boolean getStrict() {
    return m_Strict;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String strictTipText() {
    return "If enabled, ensures that IDs in unique ID column are truly unique.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    String		value;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
    if (result == null)
      result = "";
    value = QuickInfoHelper.toString(this, "prefixSeparator", m_PrefixSeparator, ", separator: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "excludedAttributes", m_ExcludedAttributes, ", excluded: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "uniqueID", m_UniqueID, ", unique: ");
    if (value != null)
      result += value;
    if (result.startsWith(", "))
      result = result.substring(2);

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "addIndex", m_AddIndex, "index"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "usePrefix", m_UsePrefix, "prefix"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "invertMatchingSense", m_InvertMatchingSense, "invert"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "remove", m_Remove, "remove"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "keepOnlySingleUniqueID", m_KeepOnlySingleUniqueID, "single unique ID"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "strict", m_Strict, "strict"));
    result += QuickInfoHelper.flatten(options);
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Excludes columns from the data.
   *
   * @param sheet	the data to process
   * @return		the processed data
   */
  protected SpreadSheet excludeAttributes(SpreadSheet sheet) {
    SpreadSheet		result;
    ByName		byname;
    Invert		invert;
    ColumnFinder	finder;

    byname = new ByName();
    byname.setRegExp(new BaseRegExp(m_ExcludedAttributes));
    finder = byname;
    if (m_InvertMatchingSense) {
      invert = new Invert();
      invert.setColumnFinder(byname);
      finder = invert;
    }
    result = AbstractColumnFinder.filter(sheet, finder);

    return result;
  }

  /**
   * Generates the prefix string.
   * 
   * @param index	the index of the spreadsheet to produce the prefix for
   * @return		the generated prefix
   */
  protected String createPrefix(int index) {
    String	result;
    
    if (!m_UsePrefix)
      return "";
    
    result = m_Prefix;
    if (m_AddIndex) {
      if (result.length() > 0)
	result += m_PrefixSeparator;
      result += (index + 1);
    }
    if ((result.length() > 0) && !result.endsWith(m_PrefixSeparator))
      result += m_PrefixSeparator;
    
    return result;
  }
  
  /**
   * Prefixes the columns.
   *
   * @param index	the index of the spreadsheet
   * @param inst	the data to process
   * @return		the processed data
   */
  protected SpreadSheet prefixColumns(SpreadSheet inst, int index) {
    SpreadSheet		result;
    String		prefix;
    int			i;
    Row			row;

    result = inst.getClone();

    // generate prefix
    prefix = createPrefix(index);

    // header
    row = result.getHeaderRow();
    for (i = 0; i < result.getColumnCount(); i++)
      row.getCell(i).setContent(prefix + row.getCell(i).getContent());

    return result;
  }

  /**
   * Prepares the data, prefixing columns, removing columns, etc, before
   * merging it.
   *
   * @param inst	the data to process
   * @param index	the 0-based index of the dataset being processed
   * @return		the prepared data
   */
  protected SpreadSheet prepareData(SpreadSheet inst, int index) {
    SpreadSheet	result;

    result = inst;

    if (m_KeepOnlySingleUniqueID && !m_UniqueID.isEmpty() && (inst.getHeaderRow().indexOfContent(m_UniqueID) > -1)) {
      if (index > 0)
	m_UniqueIDAtts.add(createPrefix(index) + m_UniqueID);
    }

    // exclude attributes
    if (m_ExcludedAttributes.length() > 0)
      result = excludeAttributes(result);

    // prefix
    if (m_UsePrefix)
      result = prefixColumns(inst, index);

    return result;
  }

  /**
   * Updates the IDs in the hashset with the ones stored in the ID column
   * of the provided spreadsheet.
   *
   * @param sheetIndex	the spreadheet index
   * @param inst	the spreadsheet to obtain the IDs from
   * @param ids		the hashset to store the IDs in
   */
  protected void updateIDs(int sheetIndex, SpreadSheet inst, HashSet ids) {
    int		i;
    int		index;
    boolean	numeric;
    HashSet 	current;
    Object	id;

    index = inst.getHeaderRow().indexOfContent(m_UniqueID);
    if (index == -1)
      throw new IllegalStateException(
	  "Column '" + m_UniqueID + "' not found in spreadsheet #" + (sheetIndex+1) + "!");

    // get IDs
    numeric = inst.isNumeric(index);
    current = new HashSet();
    for (i = 0; i < inst.getRowCount(); i++) {
      if (inst.hasCell(i, index) && !inst.getCell(i, index).isMissing()) {
	if (numeric)
	  id = inst.getCell(i, index).toDouble();
	else
	  id = inst.getCell(i, index).getContent();
	if (m_Strict && current.contains(id))
	  throw new IllegalStateException("ID '" + id + "' is not unique in spreadsheet #" + (sheetIndex+1) + "!");
	current.add(id);
      }
    }
    ids.addAll(current);
  }

  /**
   * Merges the datasets based on the collected IDs.
   *
   * @param orig	the original datasets
   * @param sheets	the processed datasets to merge into one
   * @param ids		the IDs for identifying the rows
   * @return		the merged dataset
   */
  protected SpreadSheet merge(SpreadSheet[] orig, SpreadSheet[] sheets, HashSet ids) {
    SpreadSheet			result;
    int				i;
    int				n;
    int				m;
    int				index;
    ArrayList			sortedIDs;
    int[]			indexStart;
    int				colIndex;
    HashMap<Integer,Integer> 	hashmap;
    HashSet<Integer> 		hs;
    List<Integer>		indices;
    String			prefix;
    boolean			numeric;

    // create header
    if (isLoggingEnabled())
      getLogger().info("Creating merged header...");
    result = orig[0].newInstance();
    indexStart = new int[sheets.length];
    for (i = 0; i < sheets.length; i++) {
      indexStart[i] = result.getColumnCount();
      for (n = 0; n < sheets[i].getColumnCount(); n++) {
	result.getHeaderRow().addCell("" + result.getColumnCount()).setContent(
	    sheets[i].getHeaderRow().getCell(n).getContent());
      }
    }

    // fill with missing values
    if (isLoggingEnabled())
      getLogger().info("Filling with missing values...");
    for (i = 0; i < ids.size(); i++) {
      if (isStopped())
	return null;
      // progress
      if (isLoggingEnabled() && ((i+1) % 1000 == 0))
	getLogger().info("" + (i+1));
      result.addRow();
    }

    // sort IDs
    if (isLoggingEnabled())
      getLogger().info("Sorting indices...");
    sortedIDs = new ArrayList(ids);
    Collections.sort(sortedIDs);

    // generate rows
    hashmap = new HashMap<>();
    for (i = 0; i < sheets.length; i++) {
      if (isStopped())
	return null;
      if (isLoggingEnabled())
	getLogger().info("Adding sheet #" + (i+1));
      prefix   = createPrefix(i);
      colIndex = sheets[i].getHeaderRow().indexOfContent(prefix + m_UniqueID);
      numeric  = sheets[i].isNumeric(colIndex);
      for (n = 0; n < sheets[i].getRowCount(); n++) {
	// progress
	if (isLoggingEnabled() && ((n+1) % 1000 == 0))
	  getLogger().info("" + (n+1));

	// determine index of row
	if (numeric)
	  index = Collections.binarySearch(sortedIDs, sheets[i].getCell(n, colIndex).toDouble());
	else
	  index = Collections.binarySearch(sortedIDs, sheets[i].getCell(n, colIndex).getContent());
	if (index < 0)
	  throw new IllegalStateException(
	      "Failed to determine index for row #" + (n+1) + " of sheet #" + (i+1) + "!");

	if (!hashmap.containsKey(index))
	  hashmap.put(index, 0);
	hashmap.put(index, hashmap.get(index) + 1);

	// add attribute values
	for (m = 0; m < sheets[i].getColumnCount(); m++) {
	  // missing value?
	  if (!sheets[i].hasCell(n, m) || sheets[i].getCell(n, m).isMissing())
	    continue;
	  result.getCell(index, indexStart[i] + m).assign(sheets[i].getCell(n, m));
	}
      }
    }

    if (getRemove()) {
      hs = new HashSet<>();
      for (Integer x: hashmap.keySet()){
	if (hashmap.get(x) != sheets.length)
	  hs.add(x);
      }
      indices = new ArrayList<>(hs);
      Collections.sort(indices);
      Collections.reverse(indices);
      for (Integer ind: indices)
	result.removeRow(ind);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    int			i;
    SpreadSheet		output;
    SpreadSheet[]	orig;
    SpreadSheet[]	sheet;
    HashSet		ids;
    int			max;
    TIntList 		uniqueList;

    result = null;

    // get filenames
    orig  = null;
    if (m_InputToken.getPayload() instanceof SpreadSheet[]) {
      orig = (SpreadSheet[]) m_InputToken.getPayload();
    }
    else {
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());
    }

    try {
      output = null;

      // simple merge
      if (m_UniqueID.length() == 0) {
	sheet = new SpreadSheet[1];
	for (i = 0; i < orig.length; i++) {
	  if (isStopped())
	    break;
	  sheet[0] = prepareData(orig[i], i);
	  if (i == 0) {
	    output = sheet[0];
	  }
	  else {
	    if (isLoggingEnabled())
	      getLogger().info("Merging with spreadsheet #" + (i+1));
	    output.mergeWith(sheet[0]);
	  }
	}
      }
      // merge based on row IDs
      else {
	m_UniqueIDAtts = new ArrayList<>();
	max = 0;
	for (i = 0; i < orig.length; i++)
	  max = Math.max(max, orig[i].getRowCount());
	sheet = new SpreadSheet[orig.length];
	ids  = new HashSet(max);
	for (i = 0; i < orig.length; i++) {
	  if (isStopped())
	    break;
	  if (isLoggingEnabled())
	    getLogger().info("Updating IDs #" + (i+1));
	  updateIDs(i, orig[i], ids);
	  if (isLoggingEnabled())
	    getLogger().info("Preparing spreadsheet #" + (i+1));
	  sheet[i] = prepareData(orig[i], i);
	}
	output = merge(orig, sheet, ids);

	// remove duplicate unique IDs
	if (m_UniqueIDAtts.size() > 0) {
	  for (String col: m_UniqueIDAtts) {
	    for (i = output.getColumnCount() - 1; i >= 0; i--) {
              if (output.getColumnName(i).equals(col)) {
                if (isLoggingEnabled())
                  getLogger().info("Removing unique ID column: #" + (i+1) + "/" + col);
                output.removeColumn(i);
	        break;
              }
            }
          }
	}
      }

      if (!isStopped())
	m_OutputToken = new Token(output);
    }
    catch (Exception e) {
      result = handleException("Failed to merge: ", e);
    }

    return result;
  }
}
