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
 * ImageObjectOverlap.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.AbstractField;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Computes the overlap of objects with the specified report from storage.<br>
 * It stores the overlap percentage of the highest overlap found (overlap_highest) and the total number of overlaps greater than the specified minimum (overlap_count).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageObjectOverlap
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item to merge with (Report or ReportHandler).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder for locating the objects of interest.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-min-overlap-ratio &lt;double&gt; (property: minOverlapRatio)
 * &nbsp;&nbsp;&nbsp;The minimum ratio that an overlap must have before being considered an actual
 * &nbsp;&nbsp;&nbsp;overlap.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectOverlap
  extends AbstractTransformer {

  private static final long serialVersionUID = 8175397929496972306L;

  /** the highest overlap percentage. */
  public final static String OVERLAP_HIGHEST = "overlap_highest";

  /** the overlap count. */
  public final static String OVERLAP_COUNT = "overlap_count";

  /** the storage item. */
  protected StorageName m_StorageName;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the minimum overlap ratio to use. */
  protected double m_MinOverlapRatio;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Computes the overlap of objects with the specified report from storage.\n"
      + "It stores the overlap percentage of the highest overlap found (" + OVERLAP_HIGHEST + ") and the "
      + "total number of overlaps greater than the specified minimum (" + OVERLAP_COUNT + ").";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "min-overlap-ratio", "minOverlapRatio",
      0.0, 0.0, 1.0);
  }

  /**
   * Sets the name of the storage item to merge with (Report or ReportHandler).
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to merge with (Report or ReportHandler).
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage item to merge with (Report or ReportHandler).";
  }

  /**
   * Sets the object finder for locating the objects.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns object finder for locating the objects.
   *
   * @return 		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder for locating the objects of interest.";
  }

  /**
   * Sets the minimum overlap ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setMinOverlapRatio(double value) {
    if (getOptionManager().isValid("minOverlapRatio", value)) {
      m_MinOverlapRatio = value;
      reset();
    }
  }

  /**
   * Returns the minimum overlap ratio to use.
   *
   * @return 		the minimum ratio
   */
  public double getMinOverlapRatio() {
    return m_MinOverlapRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minOverlapRatioTipText() {
    return "The minimum ratio that an overlap must have before being considered an actual overlap.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");
    result += QuickInfoHelper.toString(this, "minOverlapRatio", m_MinOverlapRatio, ", overlap ratio: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			obj;
    Report			newReport;
    Report			thisReport;
    Report 			otherReport;
    LocatedObjects		thisObjs;
    LocatedObjects		otherObjs;
    LocatedObjects newObjs;
    int 			count;
    double			highest;
    double			ratio;
    Object			output;

    result = null;

    output      = null;
    thisReport  = null;
    otherReport = null;

    if (m_InputToken.getPayload() instanceof AbstractImageContainer)
      thisReport = ((AbstractImageContainer) m_InputToken.getPayload()).getReport();
    else if (m_InputToken.getPayload() instanceof Report)
      thisReport = (Report) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof ReportHandler)
      thisReport = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      result = "Unsupported input class: " + Utils.classToString(m_InputToken.getPayload());

    if (thisReport != null) {
      obj = getStorageHandler().getStorage().get(m_StorageName);
      if (obj == null)
	result = "Failed to retrieve storage item: " + m_StorageName;
      else {
	if (obj instanceof Report)
	  otherReport = (Report) obj;
	else if (obj instanceof ReportHandler)
	  otherReport = ((ReportHandler) obj).getReport();
	else
	  result = "Unhandled type of storage item '" + m_StorageName + "': " + Utils.classToString(obj);
      }
    }

    if (otherReport != null) {
      thisObjs  = LocatedObjects.fromReport(thisReport,  m_Finder.getPrefix());
      thisObjs  = thisObjs.subset(m_Finder.find(thisObjs));
      otherObjs = LocatedObjects.fromReport(otherReport, m_Finder.getPrefix());
      otherObjs = otherObjs.subset(m_Finder.find(otherObjs));
      newObjs   = new LocatedObjects();
      if (thisObjs.size() == 0) {
        newObjs = otherObjs;
      }
      else {
        for (LocatedObject thisObj : thisObjs) {
          count   = 0;
          highest = 0.0;
          for (LocatedObject otherObj : otherObjs) {
            ratio = thisObj.overlapRatio(otherObj);
            if (ratio >= m_MinOverlapRatio) {
              count++;
              highest = Math.max(highest, ratio);
            }
          }
          thisObj.getMetaData().put(OVERLAP_HIGHEST, highest);
          thisObj.getMetaData().put(OVERLAP_COUNT, count);
	  newObjs.add(thisObj);
        }
      }

      // assemble new report
      try {
	newReport = thisReport.getClass().newInstance();
	// transfer non-object fields
	for (AbstractField field: thisReport.getFields()) {
	  if (!field.getName().startsWith(m_Finder.getPrefix())) {
	    newReport.addField(field);
	    newReport.setValue(field, thisReport.getValue(field));
	  }
	}
	// store objects
	newReport.mergeWith(newObjs.toReport(m_Finder.getPrefix()));
	// update report
	if (m_InputToken.getPayload() instanceof AbstractImageContainer) {
	  output = m_InputToken.getPayload();
	  ((AbstractImageContainer) output).setReport(newReport);
	}
	else if (m_InputToken.getPayload() instanceof MutableReportHandler) {
	  output = m_InputToken.getPayload();
	  ((MutableReportHandler) output).setReport(newReport);
	}
	else {
	  output = newReport;
	}
      }
      catch (Exception e) {
	result = handleException("Failed to create new report with updated objects!", e);
	output = null;
      }
    }

    if (output != null)
      m_OutputToken = new Token(output);

    return result;
  }
}
