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
 * TrainTestSplit.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses a (random) percentage split to generate train/test.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrainTestSplit
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = -4460266467650893551L;

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the split percentage. */
  protected JTextField m_TextPercentage;

  /** whether to preserve the order. */
  protected JCheckBox m_CheckBoxPreserveOrder;

  /** the seed value. */
  protected JTextField m_TextSeed;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // dataset
    m_ModelDatasets    = new DefaultComboBoxModel<>();
    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    // percentage
    m_TextPercentage = new JTextField("" + props.getInteger("Classify.TrainPercentage", 1));
    m_TextPercentage.setToolTipText("Percentage for train set (0 < x < 100)");
    m_PanelParameters.addParameter("Percentage", m_TextPercentage);

    // preserve order?
    m_CheckBoxPreserveOrder = new JCheckBox();
    m_CheckBoxPreserveOrder.setSelected(props.getBoolean("Classify.PreserveOrder", false));
    m_CheckBoxPreserveOrder.setToolTipText("No randomization is performed if checked");
    m_PanelParameters.addParameter("Preserve order", m_CheckBoxPreserveOrder);

    // seed
    m_TextSeed = new JTextField("" + props.getInteger("Classify.Seed", 1));
    m_TextSeed.setToolTipText("The seed value for randomizing the data");
    m_PanelParameters.addParameter("Seed", m_TextSeed);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Train/test split";
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		true if possible
   */
  public boolean canEvaluate(Classifier classifier) {
    Instances	data;
    double	perc;

    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return false;

    if (!Utils.isInteger(m_TextSeed.getText()))
      return false;

    if (!Utils.isDouble(m_TextPercentage.getText()))
      return false;
    perc = Utils.toDouble(m_TextPercentage.getText());
    if ((perc <= 0) || (perc >= 100))
      return false;

    data = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    return classifier.getCapabilities().test(data);
  }

  /**
   * Evaluates the classifier and returns the generated evaluation object.
   *
   * @return		the evaluation
   * @throws Exception	if evaluation fails
   */
  @Override
  public Evaluation evaluate(Classifier classifier) throws Exception {
    Evaluation			result;
    Instances			data;
    Instances			train;
    Instances			test;
    RandomSplitGenerator	generator;
    WekaTrainTestSetContainer	cont;

    if (!canEvaluate(classifier))
      throw new IllegalArgumentException("Cannot evaluate classifier!");

    data   = getOwner().getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    if (m_CheckBoxPreserveOrder.isSelected())
      generator = new RandomSplitGenerator(data, Utils.toDouble(m_TextPercentage.getText()) / 100.0);
    else
      generator = new RandomSplitGenerator(data, Integer.parseInt(m_TextSeed.getText()), Utils.toDouble(m_TextPercentage.getText()) / 100.0);
    cont  = generator.next();
    train = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
    test  = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
    classifier = (Classifier) OptionUtils.shallowCopy(classifier);
    classifier.buildClassifier(train);
    result = new Evaluation(train);
    result.evaluateModel(classifier, test);

    return result;
  }

  /**
   * Updates the settings panel.
   */
  @Override
  public void update() {
    List<String>	datasets;
    int			i;
    String		oldDataset;
    int			index;
    DataContainer 	data;

    if (getOwner() == null)
      return;
    if (getOwner().getOwner() == null)
      return;

    oldDataset = (String) m_ComboBoxDatasets.getSelectedItem();
    if (oldDataset != null)
      oldDataset = oldDataset.replaceAll("^[0-9]]+: ", "");
    datasets = new ArrayList<>();
    index    = -1;
    for (i = 0; i < getOwner().getData().size(); i++) {
      data = getOwner().getData().get(i);
      datasets.add((i + 1) + ": " + data.getData().relationName());
      if ((oldDataset != null) && data.getData().relationName().equals(oldDataset))
	index = i;
    }
    m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
    m_ComboBoxDatasets.setModel(m_ModelDatasets);
    if ((index == -1) && (m_ModelDatasets.getSize() > 0))
      m_ComboBoxDatasets.setSelectedIndex(0);
    else if (index > -1)
      m_ComboBoxDatasets.setSelectedIndex(index);

    getOwner().updateButtons();
  }
}
