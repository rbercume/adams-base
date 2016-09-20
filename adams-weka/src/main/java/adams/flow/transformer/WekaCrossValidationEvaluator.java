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
 * WekaCrossValidationEvaluator.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Performance;
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.ThreadLimiter;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.standalone.JobRunnerSetup;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Cross-validates a classifier on an incoming dataset. The classifier setup being used in the evaluation is a callable 'Classifier' actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output
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
 * &nbsp;&nbsp;&nbsp;default: WekaCrossValidationEvaluator
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output &lt;weka.classifiers.evaluation.output.prediction.AbstractOutput&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The class for generating prediction output; if 'Null' is used, then an Evaluation 
 * &nbsp;&nbsp;&nbsp;object is forwarded instead of a String; not used when using parallel execution.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.evaluation.output.prediction.Null
 * </pre>
 * 
 * <pre>-always-use-container &lt;boolean&gt; (property: alwaysUseContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, always outputs an evaluation container.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The callable classifier actor to cross-validate on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
 * </pre>
 * 
 * <pre>-no-predictions &lt;boolean&gt; (property: discardPredictions)
 * &nbsp;&nbsp;&nbsp;If enabled, the collection of predictions during evaluation is suppressed,
 * &nbsp;&nbsp;&nbsp; wich will conserve memory.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the cross-validation (used for randomization).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in the cross-validation; use -1 for leave-one-out 
 * &nbsp;&nbsp;&nbsp;cross-validation (LOOCV).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for cross-validation; -1 = number of
 * &nbsp;&nbsp;&nbsp;CPUs&#47;cores; 0 or 1 = sequential execution.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCrossValidationEvaluator
  extends AbstractCallableWekaClassifierEvaluator
  implements Randomizable, ProvenanceSupporter, ThreadLimiter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the number of folds. */
  protected int m_Folds;

  /** the seed value. */
  protected long m_Seed;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** for performing cross-validation. */
  protected WekaCrossValidationExecution m_CrossValidation;

  /** the jobrunner setup. */
  protected transient JobRunnerSetup m_JobRunnerSetup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Cross-validates a classifier on an incoming dataset. The classifier "
      + "setup being used in the evaluation is a callable 'Classifier' actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "seed", "seed",
	    1L);

    m_OptionManager.add(
	    "folds", "folds",
	    10, -1, null);

    m_OptionManager.add(
	    "num-threads", "numThreads",
	    1);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();

    result += QuickInfoHelper.toString(this, "folds", m_Folds, ", folds: ");
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    result += QuickInfoHelper.toString(this, "numThreads", Performance.getNumThreadsQuickInfo(m_NumThreads), ", ");

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String classifierTipText() {
    return "The callable classifier actor to cross-validate on the input data.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputTipText() {
    return
	"The class for generating prediction output; if 'Null' is used, then "
	+ "an Evaluation object is forwarded instead of a String; not used when "
	+ "using parallel execution.";
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds, -1 for LOOCV
   */
  public void setFolds(int value) {
    if ((value == -1) || (value >= 2)) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of folds must be >=2 or -1 for LOOCV, provided: " + value);
    }
  }

  /**
   * Returns the number of folds.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in the cross-validation; use -1 for leave-one-out cross-validation (LOOCV).";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the cross-validation (used for randomization).";
  }

  /**
   * Sets the number of threads to use for cross-validation.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public void setNumThreads(int value) {
    m_NumThreads = value;
    reset();
  }

  /**
   * Returns the number of threads to use for cross-validation.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores; 0/1 = sequential execution
   */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return Performance.getNumThreadsHelp();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null)
      m_JobRunnerSetup = (JobRunnerSetup) ActorUtils.findClosestType(this, JobRunnerSetup.class);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Instances				data;
    weka.classifiers.Classifier		cls;
    int[]				indices;

    indices = null;

    try {
      // evaluate classifier
      cls = getClassifierInstance();
      if (cls == null)
	throw new IllegalStateException("Classifier '" + getClassifier() + "' not found!");
      if (isLoggingEnabled())
        getLogger().info(OptionUtils.getCommandLine(cls));

      data = (Instances) m_InputToken.getPayload();

      m_CrossValidation = new WekaCrossValidationExecution();
      m_CrossValidation.setJobRunnerSetup(m_JobRunnerSetup);
      m_CrossValidation.setClassifier(cls);
      m_CrossValidation.setData(data);
      m_CrossValidation.setFolds(m_Folds);
      m_CrossValidation.setSeed(m_Seed);
      m_CrossValidation.setDiscardPredictions(m_DiscardPredictions);
      m_CrossValidation.setNumThreads(m_NumThreads);
      m_CrossValidation.setOutput(m_Output);
      result = m_CrossValidation.execute();

      if (!m_CrossValidation.isStopped()) {
	indices = m_CrossValidation.getOriginalIndices();
	if (m_CrossValidation.isSingleThreaded()) {
	  if (m_Output instanceof Null) {
	    m_OutputToken = new Token(new WekaEvaluationContainer(m_CrossValidation.getEvaluation()));
	  }
	  else {
	    if (m_CrossValidation.getOutputBuffer() != null)
	      m_OutputBuffer.append(m_CrossValidation.getOutputBuffer().toString());
	    if (m_AlwaysUseContainer)
	      m_OutputToken = new Token(new WekaEvaluationContainer(m_CrossValidation.getEvaluation(), null, m_Output.getBuffer().toString()));
	    else
	      m_OutputToken = new Token(m_Output.getBuffer().toString());
	  }
	}
	else {
	  m_OutputToken = new Token(new WekaEvaluationContainer(m_CrossValidation.getEvaluation()));
	}
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to cross-validate classifier: ", e);
    }

    if (m_OutputToken != null) {
      if ((indices != null) && (m_OutputToken.getPayload() instanceof WekaEvaluationContainer))
	((WekaEvaluationContainer) m_OutputToken.getPayload()).setValue(WekaEvaluationContainer.VALUE_ORIGINALINDICES, indices);
      updateProvenance(m_OutputToken);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_CrossValidation != null)
      m_CrossValidation.stopExecution();
    super.stopExecution();
  }
}
