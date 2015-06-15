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
 * MTAbstractGeneticAlgorithm.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.genetic;

import adams.core.ClassLister;
import adams.core.Pausable;
import adams.core.Properties;
import adams.core.Randomizable;
import adams.core.Range;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.multiprocess.Job;
import weka.core.Instances;

import java.util.BitSet;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Base class for genetic algorithms.
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class MTAbstractGeneticAlgorithm
  extends AbstractOptionHandler
  implements Randomizable, StoppableWithFeedback, Pausable {

  /** for serialization. */
  private static final long serialVersionUID = 2823734145266194843L;
  /**
   * A job class specific to genetic algorithms.
   *
   * @author  dale
   * @version $Revision$
   */
  public static abstract class GeneticAlgorithmJob
    extends Job {

    /** for serialization. */
    private static final long serialVersionUID = -4974865548501195622L;

    /** the weights of the chromosomes (0 = turned off, 1 = turned on). */
    protected int[] m_weights;

    /** the algorithm object this job belongs to. */
    protected MTAbstractGeneticAlgorithm m_genetic;

    /** the current fitness. */
    protected Double m_fitness;

    /** the number of chromosomes. */
    protected int m_chrom_num;

    /**
     * Initializes the job.
     *
     * @param g		the algorithm object this job belongs to
     * @param num	the number of chromsomes
     * @param w		the initial weights
     */
    public GeneticAlgorithmJob(MTAbstractGeneticAlgorithm g, int num, int[] w) {
      super();

      m_weights   = w;
      m_genetic   = g;
      m_chrom_num = num;
      m_fitness   = null;
    }

    /**
     * Returns the algorithm this job belongs to.
     *
     * @return		the owner
     */
    public MTAbstractGeneticAlgorithm getGenetic() {
      return m_genetic;
    }

    /**
     * Returns the current weights of the attributes.
     *
     * @return		the weights
     */
    public int[] getWeights() {
      return m_weights;
    }

    /**
     * Returns the number of chromosomes.
     *
     * @return		the number of chromosomes
     */
    public int getNumChrom() {
      return m_chrom_num;
    }

    /**
     * Returns the fitness.
     *
     * @return		the fitness
     */
    public Double getFitness() {
      return m_fitness;
    }

    /**
     * Turns the weights into a string representation.
     *
     * @return		the weights as string
     */
    public String weightsToString() {
      String ret = "";
      for (int i = 0; i < m_weights.length; i++) {
	if (m_weights[i] == 0) {
	  ret += "0";
	} else {
	  ret += "1";
	}
      }
      return ret;
    }

    /**
     * Calculates the new fitness.
     */
    public abstract void calcNewFitness();

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_genetic == null)
	return("Doesn't belong to genetic algorithm!");
      else
	return null;
    }

    /**
     * Does the actual execution of the job.
     * 
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      calcNewFitness();
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		always null
     */
    @Override
    protected String postProcessCheck() {
      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the weights and the genetic algorithm to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_weights = null;
      m_genetic = null;
    }

    /**
     * Returns a string representation of the job.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      return
          m_genetic.getClass().getName()
        + ",#chrom=" + m_chrom_num
        + ",fitness=" + m_fitness
        + ",weights=" + weightsToString();
    }
  }

  /** the key for the relation name in the generated properties file.
   * @see #storeSetup(Instances,GeneticAlgorithmJob). */
  public final static String PROPS_RELATION = "relation";

  /** the key for a filter setup in the setup properties. */
  public final static String PROPS_FILTER = "filter";

  /** the key for the mask in the setup properties. */
  public final static String PROPS_MASK = "mask";

  /** number of genes per chromosome.
   * NB: must be initialized by the algorithm! */
  protected int m_NumGenes;

  /** number of chromosomes. */
  protected int m_NumChrom;

  /** number of iterations to perform. */
  protected int m_NumIterations;

  /** the genes. */
  protected BitSet[] m_Genes;

  /** the fitness of the genes. */
  protected double[] m_Fitness;

  /** stores the best range of attribtues. */
  protected Range m_BestRange;

  /** whether to favor 0s instead of 1s. */
  protected boolean m_FavorZeroes;

  /** the seed value. */
  protected long m_Seed;

  /** the random number generator. */
  protected Random m_Random;

  /** whether the algorithm is still running. */
  protected boolean m_Running;

  /** whether the algorithm got stopped. */
  protected boolean m_Stopped;

  /** whether the algorithm is paused. */
  protected boolean m_Paused;

  /** the maximum number of seconds to train. */
  protected int m_MaxTrainTime;

  /** the time when training commenced. */
  protected long m_TrainStart;

  public abstract Vector<int[]> getInitialSetups();

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_NumGenes  = 0;  // must be set by the algorithm itself, e.g., in preRun()
    m_BestRange = new Range();
    m_Paused    = false;
  }

  /**
   * Resets the genetic algorihtm.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Random  = new Random(m_Seed);
    m_Running = false;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-chrom", "numChrom",
	    50);

    m_OptionManager.add(
	    "num-iter", "numIterations",
	    10000000);

    m_OptionManager.add(
	    "seed", "seed",
	    1);

    m_OptionManager.add(
	    "favor-zeroes", "favorZeroes",
	    false);

    m_OptionManager.add(
	    "best", "bestRange",
	    "-none-");

    m_OptionManager.add(
	    "max-train", "maxTrainTime",
	    0);
  }

  /**
   * Sets the best range of attributes.
   *
   * @param value	the range
   */
  protected void setBestRange(Range value) {
    m_BestRange = value;
    reset();
  }

  /**
   * Sets the best range of attributes.
   *
   * @param value	the range
   */
  public void setBestRange(String value) {
    if (value.equals("-none-"))
      setBestRange(new Range());
    else
      setBestRange(new Range(value));
  }

  /**
   * Returns the best range of attributes.
   *
   * @return		the range
   */
  public String getBestRange() {
    if (m_BestRange.getRange().length() == 0)
      return "-none-";
    else
      return m_BestRange.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bestRangeTipText() {
    return "The range of the best attributes.";
  }

  /**
   * Sets the seed value to use, resets the random number generator.
   *
   * @param value	the seed to use
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the current seed value.
   *
   * @return		the seed value
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
    return "The seed value for the random number generator.";
  }

  /**
   * Sets whether 0s are favored over 1s.
   *
   * @param value	if true then 0s are favored over 1s
   */
  public void setFavorZeroes(boolean value) {
    m_FavorZeroes = value;
    reset();
  }

  /**
   * Returns whether 0s are favored over 1s.
   *
   * @return		true if 0s are favored over 1s
   */
  public boolean getFavorZeroes() {
    return m_FavorZeroes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String favorZeroesTipText() {
    return "Whether to favor 0s instead of 1s.";
  }

  /**
   * Returns the number of genes to use.
   *
   * @return		the number
   */
  public int getNumGenes() {
    return m_NumGenes;
  }

  /**
   * Sets the number of chromosomes to use.
   *
   * @param value	the number
   */
  public void setNumChrom(int value) {
    m_NumChrom = value;
    reset();
  }

  /**
   * Returns the number of chromosomes to use.
   *
   * @return		the number
   */
  public int getNumChrom() {
    return m_NumChrom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numChromTipText() {
    return "The number of chromosomes, ie, the population size.";
  }

  /**
   * Sets the number of iterations to perform.
   *
   * @param value	the number
   */
  public void setNumIterations(int value) {
    m_NumIterations = value;
    reset();
  }

  /**
   * Returns the number of iterations to perform.
   *
   * @return		the number
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText() {
    return "The number of iterations to perform.";
  }

  /**
   * Sets the maximum number of seconds to perform training.
   *
   * @param value	the number of seconds
   */
  public void setMaxTrainTime(int value) {
    m_MaxTrainTime = value;
    reset();
  }

  /**
   * Returns the maximum number of seconds to perform training.
   *
   * @return		the seed value
   */
  public int getMaxTrainTime() {
    return m_MaxTrainTime;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTrainTimeTipText() {
    return "The maximum number of seconds to training time (0 = unlimited time).";
  }

  /**
   * Stops the execution of the algorithm.
   */
  public void stopExecution() {
    m_Running = false;
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_Running)
      m_Paused = true;
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return m_Running && m_Paused;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_Running)
      m_Paused = false;
  }

  /**
   * Returns whether the algorithm is still running.
   *
   * @return		true if the algorithm is still running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns the current fitness values.
   *
   * @return		the fitness values
   */
  public double[] getFitness() {
    return m_Fitness;
  }

  /**
   * Initializes the algorithm.
   *
   * @param ch		the chromosome
   * @param genes	the number of genes
   */
  protected void init(int ch, int genes) {
    m_NumChrom=ch;
    m_NumGenes = genes;
    getLogger().info("#chrom=" + ch + ", #gene=" + genes);
    m_Genes = new BitSet[m_NumChrom];
    Vector<int[]> setups=getInitialSetups();
    for (int i = 0; i < m_NumChrom; i++) {
      m_Genes[i] = new BitSet(m_NumGenes);

      if (i < setups.size()) {
	int[] gene=setups.get(i);
	for (int j = 0; j < m_NumGenes; j++) {
	  if (gene[j] == 1) {
	    m_Genes[i].set(j);
	  }
	}
	continue;
      }
      for (int j = 0; j < m_NumGenes; j++) {
	double set = m_Random.nextDouble();
	if (set < 0.5) {
	  m_Genes[i].set(j);
	}
      }

    }

    m_Fitness = new double[m_NumChrom];
    for (int f = 0; f < m_NumChrom; f++)
      m_Fitness[f] = 0;
    sort();
  }

  /**
   * Returns the value of the specified gene.
   *
   * @param chromosome	the chromosome index
   * @param gene	the gene index
   * @return		true if gene is active
   */
  public boolean getGene(int chromosome, int gene) {
    return m_Genes[chromosome].get(gene);
  }

  /**
   * Sets the value of the specified gene.
   *
   * @param chromosome	the chromosome index
   * @param gene	the gene index
   * @param value	the value to set, 0=clear, otherwise activate it
   */
  public void setGene(int chromosome, int gene, int value) {
    setGene(chromosome, gene, (value != 0));
  }

  /**
   * Sets the value of the specified gene.
   *
   * @param chromosome	the chromosome index
   * @param gene	the gene index
   * @param value	false=clear, true=activate it
   */
  public void setGene(int chromosome, int gene, boolean value) {
    if (value)
      m_Genes[chromosome].set(gene);
    else
      m_Genes[chromosome].clear(gene);
  }

  /**
   * return if a has more zeroes than b.
   *
   * @param a		the first gene
   * @param b		the second gene
   * @return		true if a has more zeroes than b
   */
  public boolean hasMoreZeroes(BitSet a, BitSet b) {
    return (a.cardinality() < b.cardinality());
  }

  /**
   * Sorts genes and fitness arrays according to fitness.
   */
  public void sort() {
    BitSet btemp;
    for (int c = 0; c < m_NumChrom; c++) {
      for (int d = (m_NumChrom - 2); d >= c; d--) {
	if (m_Fitness[d] < m_Fitness[d+1]) {
	  btemp = m_Genes[d];
	  double x = m_Fitness[d];
	  m_Genes[d] = m_Genes[d+1];
	  m_Fitness[d] = m_Fitness[d+1];
	  m_Genes[d+1] = btemp;
	  m_Fitness[d+1] = x;
	} else if (m_Fitness[d] == m_Fitness[d+1] && hasMoreZeroes(m_Genes[d+1],m_Genes[d])) {
	  btemp = m_Genes[d];
	  double x = m_Fitness[d];
	  m_Genes[d] = m_Genes[d+1];
	  m_Fitness[d] = m_Fitness[d+1];
	  m_Genes[d+1] = btemp;
	  m_Fitness[d+1] = x;
	}
      }
    }
  }

  /**
   * Performs cross-over.
   */
  public void doCrossovers()  {
    for (int m = 0; m < m_NumChrom / 4; m++) {
      copyGene(m + m_NumChrom*3/4, m);
      // copies first chromosome half over last half
    }

    // copy the 2 best genes so that their
    // genetic material is replicated frequently:
    if (m_NumChrom > 4) {
      for (int i = 0; i < m_NumGenes; i++) {
	setGene(m_NumChrom - 1, i, getGene(0, i));
	// Seems redundant but slightly different...
	setGene(m_NumChrom - 2, i, getGene(0, i));
	// when compared with the CopyGene command.
	setGene(m_NumChrom - 3, i, getGene(0, i));
	// Do one or the other.
	setGene(m_NumChrom - 4, i, getGene(1, i));
	setGene(m_NumChrom - 5, i, getGene(1, i));
      }
    }

    int num = m_NumChrom / 4; //originally set to /4
    for (int i = 0; i < num; i++) {
      int c1 = 2+ (int)((m_NumChrom - 2) * m_Random.nextDouble() * 0.99);
      int c2 = 2 + (int)((m_NumChrom - 2) * m_Random.nextDouble() * 0.99);
      // finds two different chromosomes
      if (c1 != c2) {
	int locus = 2 + (int)((m_NumGenes - 3) * m_Random.nextDouble());
	// finds a place within
	for (int g = 0; g < locus; g++) {
	  boolean temp = getGene(c1, i);
	  // and recombines by switching genes
	  setGene(c1, i, getGene(c2, i));
	  setGene(c2, i, temp);
	}
      }
    }
  }

  /**
   * Copies the values of one gene to another.
   *
   * @param to		the gene to copy to
   * @param from	the gene to copy from
   */
  protected void copyGene(int to, int from) {
    for (int i = 0; i < m_NumGenes; i++) {
      if (getGene(from, i))
	setGene(to, i, 1);
      else
	setGene(to, i, 0);
    }
  }

  /**
   * Performs mutations.
   */
  public void doMutations() {
    for(int i = 0; i < m_NumChrom*0.5; i++) {
      int c = 0 + (int)((m_NumChrom - 2) * m_Random.nextDouble() * 0.95); //0-->2
      int g = (int)(m_NumGenes * m_Random.nextDouble() * 0.95);       //saves good genes
      if (getGene(c, g))
	setGene(c, g, 0);
      // switch gene from its present position
      else
	setGene(c, g, 1);
    }
  }

  /**
   * Performs mutations.
   */
  public void doMutations2() {
    for(int i = 0; i < m_NumChrom*0.5; i++) {
      int c = 2 + (int)((m_NumChrom - 2) * m_Random.nextDouble() * 0.99); //0-->2

      double thresh=1.0/(double)m_NumGenes;
      for (int gn=0;gn<m_NumGenes;gn++) {
	if (m_Random.nextDouble() < thresh) {
	  if (getGene(c, gn))
	    setGene(c, gn, 0);
	  // switch gene from its present position
	  else
	    setGene(c, gn, 1);
	}
      }
    }
  }
  /**
   * Override the following function in sub-classes.
   */
  public abstract void calcFitness();

  /**
   * Generates a Properties file that stores information on the setup of
   * the genetic algorithm. E.g., it backs up the original relation name.
   * The generated properties file will be used as new relation name for
   * the data. Derived classes can add additional parameters to this
   * properties file.
   *
   * @param data	the data to create the setup for
   * @param job		the associated job
   * @see		#PROPS_RELATION
   * @return		the generated setup
   */
  protected Properties storeSetup(Instances data, GeneticAlgorithmJob job) {
    Properties	result;

    result = new Properties();

    // relation name
    result.setProperty(PROPS_RELATION, data.relationName());

    // filter (default is empty)
    result.setProperty(PROPS_FILTER, "");

    return result;
  }

  /**
   * Creates a new dataset, with the setup as the new relation name.
   *
   * @param data	the data to replace the relation name with the setup
   * @param job		the associated job
   * @return		the updated dataset
   */
  public Instances updateHeader(Instances data, GeneticAlgorithmJob job) {
    Properties 	props;

    props = storeSetup(data, job);
    data.setRelationName(props.toString());

    return data;
  }

  /**
   * Further initializations in derived classes.
   */
  protected void preRun() {
    m_Running    = true;
    m_TrainStart = System.currentTimeMillis();
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
      getLogger().fine("Size preRun: " + sizeOf());
  }

  /**
   * Runs the genetic algorithm.
   *
   * @return		true if successfully finished, false when interrupted
   */
  public boolean run() {
    int		i;
    int		cx;
    int 	po;
    boolean	result;

    result = true;

    try {
      preRun();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error on preRun", e);
      result = false;
    }

    if (result) {
      try {
	for (i = 0; i < getNumIterations(); i++) {
	  if (isPaused() && !isStopped()) {
	    Utils.wait(this, this, 1000, 100);
	    continue;
	  }

	  if (i % 100 == 0)
	    getLogger().info("Iteration " + (i+1) + "/" + m_NumIterations);
	  calcFitness();
	  sort();

	  if (isLoggingEnabled()) {
	    getLogger().info("Generation " + String.valueOf(i));
	    StringBuilder info = new StringBuilder();
	    for (cx = 0; cx < getNumChrom(); cx++) {
	      info.append(" Fitness for chromosome ");
	      if (cx != -1) {
		for (po = 0; po < getNumGenes(); po++) {
		  if (getGene(cx,po)) {
		    info.append("1");
		  }
		  else{
		    info.append("0");
		  }
		}
	      }
	      info.append("--->" + getFitness()[cx]);
	      getLogger().info(info.toString());
	    }
	  }

	  doCrossovers();
	  doMutations2();

	  // time limit exceeded?
	  if (m_MaxTrainTime > 0) {
	    if ((double) (System.currentTimeMillis() - m_TrainStart) / 1000.0 >= m_MaxTrainTime) {
	      getLogger().info("Training time limit of " + m_MaxTrainTime + " seconds exceeded - stopping.");
	      break;
	    }
	  }

	  if (!isRunning()) {
	    getLogger().severe("Interrupted!");
	    break;
	  }
	}
      }
      catch (Exception e) {
	result = false;
	getLogger().log(Level.SEVERE, "Error in iteration", e);
      }
    }

    try {
      postRun();
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Error in postRun", e);
    }

    return result;
  }

  /**
   * Further clean-ups in derived classes.
   *
   * @throws Exception	if something goes wrong
   */
  protected void postRun() throws Exception {
    m_Running = false;
    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
      getLogger().fine("Size postRun: " + sizeOf());
  }

  /**
   * Runs the genetic algorithm with the given options.
   *
   * @param env		the environment class to use
   * @param genetic	the genetic algorithm to run
   * @param options	the options to set
   */
  public static void runGeneticAlgorithm(Class env, Class genetic, String[] options) {
    MTAbstractGeneticAlgorithm	geneticInst;

    Environment.setEnvironmentClass(env);

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	geneticInst = forName(genetic.getName(), new String[0]);
	System.out.println("\n" + OptionUtils.list(geneticInst));
      }
      else {
	geneticInst = forName(genetic.getName(), options);
	geneticInst.run();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a list with classnames of genetic algorithms.
   *
   * @return		the genetic algorithm classnames
   */
  public static String[] getGeneticAlgorithms() {
    return ClassLister.getSingleton().getClassnames(MTAbstractGeneticAlgorithm.class);
  }

  /**
   * Instantiates the genetic algorithm with the given options.
   *
   * @param classname	the classname of the genetic algorithm to instantiate
   * @param options	the options for the genetic algorithm
   * @return		the instantiated genetic algorithm or null if an error occurred
   */
  public static MTAbstractGeneticAlgorithm forName(String classname, String[] options) {
    MTAbstractGeneticAlgorithm	result;

    try {
      result = (MTAbstractGeneticAlgorithm) OptionUtils.forName(MTAbstractGeneticAlgorithm.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the genetic algorithm from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			genetic algorithm to instantiate
   * @return		the instantiated genetic algorithm
   * 			or null if an error occurred
   */
  public static MTAbstractGeneticAlgorithm forCommandLine(String cmdline) {
    return (MTAbstractGeneticAlgorithm) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
