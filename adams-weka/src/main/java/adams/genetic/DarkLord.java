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
 * DarkLord.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.genetic;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.io.FileReader;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4322 $
 */
public class DarkLord
  extends AbstractClassifierBasedGeneticAlgorithm {

  /** for serialization. */
  private static final long serialVersionUID = 4822397823362084867L;

  /**
   * A job class specific to The Dark Lord.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static class DarkLordJob
    extends ClassifierBasedGeneticAlgorithmJob<DarkLord> {

    /** for serialization. */
    private static final long serialVersionUID = 8259167463381721274L;

    /**
     * Initializes the job.
     *
     * @param g		the algorithm object this job belongs to
     * @param num	the number of chromsomes
     * @param w		the initial weights
     */
    public DarkLordJob(DarkLord g, int num, int[] w) {
      super(g, num, w);
    }

    /**
     * Returns the "mask" of attributes as range string.
     *
     * @return		the mask
     */
    public String getMaskAsString(){
      String ret = "[";
      int pos = 0;
      int last = -1;
      boolean thefirst = true;
      for(int a = 0; a < getInstances().numAttributes(); a++)
      {
        if(a == getInstances().classIndex())
          continue;
        if(m_weights[a] == 0)
        {
          if(last == -1)
            continue;
          if(thefirst)
            thefirst = false;
          else
            ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
          if(pos - last > 1)
            ret = (new StringBuilder(String.valueOf(ret))).append(last).append("-").append(pos).toString();
          else
          if(pos - last == 1)
            ret = (new StringBuilder(String.valueOf(ret))).append(last).append(",").append(pos).toString();
          else
            ret = (new StringBuilder(String.valueOf(ret))).append(last).toString();
          last = -1;
        }
        if(m_weights[a] != 0)
        {
          if(last == -1)
            last = a;
          pos = a;
        }
      }

      if(last != -1)
      {
        if(!thefirst)
          ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
        if(pos - last > 1)
          ret = (new StringBuilder(String.valueOf(ret))).append(last).append("-").append(pos).toString();
        else
        if(pos - last == 1)
          ret = (new StringBuilder(String.valueOf(ret))).append(last).append(",").append(pos).toString();
        else
          ret = (new StringBuilder(String.valueOf(ret))).append(last).toString();
      }
      return (new StringBuilder(String.valueOf(ret))).append("]").toString();

    }

    /**
     * Generates a range string of attributes to keep (= one has to use
     * the inverse matching sense with the Remove filter).
     *
     * @return		the range of attributes to keep
     */
    public String getRemoveAsString(){
      String ret = "";
      int pos = 0;
      int last = -1;
      boolean thefirst = true;
      for(int a = 0; a < getInstances().numAttributes(); a++)
      {
        if(m_weights[a] == 0 && a != getInstances().classIndex())
        {
          if(last == -1)
            continue;
          if(thefirst)
            thefirst = false;
          else
            ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
          if(pos - last > 1)
            ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append("-").append(pos + 1).toString();
          else
          if(pos - last == 1)
            ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append(",").append(pos + 1).toString();
          else
            ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).toString();
          last = -1;
        }
        if(m_weights[a] != 0 || a == getInstances().classIndex())
        {
          if(last == -1)
            last = a;
          pos = a;
        }
      }

      if(last != -1)
      {
        if(!thefirst)
          ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
        if(pos - last > 1)
          ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append("-").append(pos + 1).toString();
        else
        if(pos - last == 1)
          ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append(",").append(pos + 1).toString();
        else
          ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).toString();
      }
      return ret;

    }

    /**
     * Assembles the data for the textual setup output.
     *
     * @param fitness	the current fitness
     * @param cls	the current classifier
     * @return		the data
     */
    protected List<String> assembleSetup(double fitness, Classifier cls) {
      List<String> result;

      result = super.assembleSetup(fitness, cls);
      result.add("Mask: " + getMaskAsString());

      return result;
    }

    /**
     * Calculates the new fitness.
     */
    @Override
    public void calcNewFitness(){
      try {
        getLogger().fine((new StringBuilder("calc for:")).append(weightsToString()).toString());

        // was measure already calculated for this attribute setup?
        Double cc = getGenetic().getResult(weightsToString());
        if (cc != null){
          getLogger().info((new StringBuilder("Already present: ")).append(Double.toString(cc.doubleValue())).toString());
          m_fitness = cc;
          return;
        }
        // set the weights
        Instances newInstances = new Instances(getInstances());
        for (int i = 0; i < getInstances().numInstances(); i++) {
          Instance in = newInstances.instance(i);
          int cnt = 0;
          for (int a = 0; a < getInstances().numAttributes(); a++) {
            if (a == getInstances().classIndex())
              continue;
            if (m_weights[cnt++] == 0){
              in.setValue(a,0);
            }else {
              in.setValue(a,in.value(a));
            }
          }
        }

	// evaluate classifier
	Classifier newClassifier = AbstractClassifier.makeCopy(m_genetic.getClassifier());
	double measure = evaluateClassifier(newClassifier, newInstances);

        // process fitness
        m_fitness = measure;
        if (m_genetic.setNewFitness(m_fitness)) {
	  outputDataset(m_fitness, newInstances);
	  outputSetup(m_fitness, newClassifier);
          // notify the listeners
          m_genetic.notifyFitnessChangeListeners(getMeasure().adjust(measure));
        }
        else {
          getLogger().fine(getMaskAsString());
        }

        getGenetic().addResult(weightsToString(), m_fitness);
      }
      catch(Exception e){
        getLogger().log(Level.SEVERE, "Error: ", e);
        m_fitness = null;
      }
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Dark Lord.";
  }

  /**
   * Calculates the fitness of the population.
   */
  @Override
  public void calcFitness() {
    JobRunner<DarkLordJob> runner = new JobRunner<DarkLordJob>();
    JobList<DarkLordJob> jobs = new JobList<DarkLordJob>();
    for (int i = 0; i < getNumChrom(); i++) {
      int[] weights = new int[getNumGenes()];
      for (int j = 0; j < getNumGenes(); j++)  {
        int weight = 0;
        for (int k = 0; k < getBitsPerGene(); k++){
          weight <<= 1;
          if (getGene(i, (j*getBitsPerGene())+k))
            weight += 1;
        }
        weights[j] = weight;
      }
      jobs.add(new DarkLordJob(this, i, weights));
    }
    runner.add(jobs);
    runner.start();
    runner.stop();

    for (int i = 0; i < jobs.size(); i++) {
      DarkLordJob job = jobs.get(i);
      // success? If not, just add the header of the original data
      if (job.getFitness() == null) {
        m_Fitness[job.getNumChrom()] = Double.NEGATIVE_INFINITY;
      }
      else {
        m_Fitness[job.getNumChrom()] = job.getFitness();
      }
      job.cleanUp();
    }
  }

  /**
   * Generates a Properties file that stores information on the setup of
   * the genetic algorithm. E.g., it backs up the original relation name.
   * The generated properties file will be used as new relation name for
   * the data.
   *
   * @param data	the data to create the setup for
   * @param job		the associated job
   * @see		#PROPS_RELATION
   * @return		the generated setup
   */
  @Override
  protected Properties storeSetup(Instances data, GeneticAlgorithmJob job) {
    Properties		result;
    DarkLordJob		jobDL;
    Remove		remove;

    result = super.storeSetup(data, job);
    jobDL  = (DarkLordJob) job;

    // mask string
    result.setProperty(PROPS_MASK, jobDL.getMaskAsString());

    // remove filter setup
    remove = new Remove();
    remove.setAttributeIndices(jobDL.getRemoveAsString());
    remove.setInvertSelection(true);
    result.setProperty(PROPS_FILTER, OptionUtils.getCommandLine(remove));

    return result;
  }

  /**
   * Some more initializations.
   */
  @Override
  protected void preRun() {
    FileReader	reader;
    int		classIndex;

    super.preRun();

    // loading the dataset
    try {
      reader      = new FileReader(m_Dataset.getAbsolutePath());
      m_Instances = new Instances(reader);
      reader.close();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read: " + m_Dataset, e);
      throw new IllegalStateException("Error loading dataset '" + m_Dataset + "': " + e);
    }

    // class index
    if (m_ClassIndex.equals("first"))
      classIndex = 0;
    else if (m_ClassIndex.equals("last"))
      classIndex = m_Instances.numAttributes() - 1;
    else
      classIndex = Integer.parseInt(m_ClassIndex);
    m_Instances.setClassIndex(classIndex);

    // does the measure handle the data?
    if (!m_Measure.isValid(m_Instances))
      throw new IllegalArgumentException(
        "Measure '" + m_Measure + "' cannot process class of type '"
          + m_Instances.classAttribute().type() + "'!");

    if (m_BestRange.getRange().length() != 0)
      m_BestRange.setMax(m_Instances.numAttributes());

    // setup structures
    init(20,m_Instances.numAttributes() * m_BitsPerGene);

    // reset timestamp of notification
    m_LastNotificationTime = null;

    // clear cache
    clearResults();
  }
}
