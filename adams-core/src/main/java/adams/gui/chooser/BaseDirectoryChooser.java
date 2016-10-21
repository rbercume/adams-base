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
 * BaseDirectoryChooser.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.Placeholders;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.gui.core.GUIHelper;
import adams.gui.core.OneTouchPanel;
import adams.gui.core.OneTouchPanel.Location;
import com.jidesoft.swing.FolderChooser;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.File;

/**
 * Extended version of the com.jidesoft.swing.FolderChooser to
 * handle PlaceholderFile objects.
 *
 * @author FracPete (fracpete at waikat dot ac dot nz)
 */
public class BaseDirectoryChooser
  extends FolderChooser {

  /** for serialization. */
  private static final long serialVersionUID = -7252242971482953986L;
  
  /** the bookmarks. */
  protected FileChooserBookmarksPanel m_PanelBookmarks;

  /** the panel for showing/hiding the bookmarks. */
  protected OneTouchPanel m_OneTouchPanel;

  /**
   * Creates a BaseDirectoryChooser pointing to the user's home directory.
   */
  public BaseDirectoryChooser() {
    super();
    initialize();
  }

  /**
   * Creates a BaseDirectoryChooser using the given File as the path.
   *
   * @param currentDirectory	the directory to start in
   */
  public BaseDirectoryChooser(File currentDirectory) {
    super(currentDirectory.getAbsoluteFile());
    initialize();
  }

  /**
   * Creates a BaseDirectoryChooser using the given current directory and
   * FileSystemView.
   *
   * @param currentDirectory	the directory to start in
   * @param fsv			the view to use
   */
  public BaseDirectoryChooser(File currentDirectory, FileSystemView fsv) {
    super(currentDirectory.getAbsoluteFile(), fsv);
    initialize();
  }

  /**
   * Creates a BaseDirectoryChooser using the given FileSystemView.
   *
   * @param fsv			the view to use
   */
  public BaseDirectoryChooser(FileSystemView fsv) {
    super(fsv);
    initialize();
  }

  /**
   * Creates a BaseDirectoryChooser using the given path.
   *
   * @param currentDirectoryPath	the directory to start in
   */
  public BaseDirectoryChooser(String currentDirectoryPath) {
    super(new PlaceholderFile(currentDirectoryPath).getAbsolutePath());
    initialize();
  }

  /**
   * Creates a BaseDirectoryChooser using the given path and FileSystemView.
   *
   * @param currentDirectoryPath	the directory to start in
   * @param fsv				the view to use
   */
  public BaseDirectoryChooser(String currentDirectoryPath, FileSystemView fsv) {
    super(new PlaceholderFile(currentDirectoryPath).getAbsolutePath(), fsv);
    initialize();
  }

  /**
   * For initializing some stuff.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void initialize() {
    JComponent		accessory;
    int			width;
    int			height;

    setRecentListVisible(false);
   
    accessory = createAccessoryPanel();
    if (accessory != null)
      setAccessory(accessory);
    
    showBookmarks(GUIHelper.getBoolean("BaseDirectoryChooser.ShowBookmarks", false));

    width  = GUIHelper.getInteger("BaseDirectoryChooser.Width", 400);
    height = GUIHelper.getInteger("BaseDirectoryChooser.Height", 500);
    if ((width != -1) && (height != -1))
      setPreferredSize(new Dimension(width, height));
  }

  /**
   * Creates an accessory panel displayed next to the files.
   * 
   * @return		the panel or null if none available
   */
  protected JComponent createAccessoryPanel() {
    m_PanelBookmarks = new FileChooserBookmarksPanel();
    m_PanelBookmarks.setOwner(this);

    m_OneTouchPanel = new OneTouchPanel(Location.TOP);
    m_OneTouchPanel.getContentPanel().add(m_PanelBookmarks, BorderLayout.CENTER);
    m_OneTouchPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    m_OneTouchPanel.setToolTipVisible("Click to hide bookmarks");
    m_OneTouchPanel.setToolTipHidden("Click to show bookmarks");

    return m_OneTouchPanel;
  }

  /**
   * Either displays or hides the bookmarks.
   * 
   * @param value	true if to show bookmarks
   */
  protected void showBookmarks(boolean value) {
    m_OneTouchPanel.setContentVisible(value);
  }
  
  /**
   * Does nothing.
   *
   * @param filter	ignored
   */
  @Override
  public void addChoosableFileFilter(FileFilter filter) {
  }

  /**
   * Sets the selected file. If the file's parent directory is
   * not the current directory, changes the current directory
   * to be the file's parent directory.
   *
   * @beaninfo
   *   preferred: true
   *       bound: true
   *
   * @see #getSelectedFile
   *
   * @param file the selected file
   */
  @Override
  public void setSelectedFile(File file) {
    File	selFile;

    selFile = null;

    if (file != null)
      selFile = new File(file.getAbsolutePath());

    super.setSelectedFile(selFile);
  }

  /**
   * Returns the selected file. This can be set either by the
   * programmer via <code>setFile</code> or by a user action, such as
   * either typing the filename into the UI or selecting the
   * file from a list in the UI.
   *
   * @return the selected file
   */
  @Override
  public File getSelectedFile() {
    File	result;
    int		pos;

    result = super.getSelectedFile();
    if (result != null) {
      // Unfortunately, JFileChooser automatically adds the current directory
      // to the filename. In case we have a placeholder in the name, we just
      // remove the part of the string preceding the placeholder start
      if ((pos = result.getPath().lastIndexOf(Placeholders.PLACEHOLDER_START)) > -1)
	result = new PlaceholderFile(result.getPath().substring(pos));
      else
	result = new PlaceholderFile(result);
    }

    return result;
  }

  /**
   * Returns the selected directory.
   *
   * @return the selected directory
   */
  public PlaceholderDirectory getSelectedDirectory() {
    PlaceholderDirectory	result;
    File			file;
    
    result = null;
    file   = getSelectedFile();
    if (file != null)
      result = new PlaceholderDirectory(file);
    
    return result;
  }

  /**
   * Sets the list of selected files if the file chooser is
   * set to allow multiple selection.
   *
   * @param selectedFiles	the files to select initially
   * @beaninfo
   *       bound: true
   * description: The list of selected files if the chooser is in multiple selection mode.
   */
  @Override
  public void setSelectedFiles(File[] selectedFiles) {
    File[]	files;
    int		i;

    files = null;
    if (selectedFiles != null) {
      files = new File[selectedFiles.length];
      for (i = 0; i < selectedFiles.length; i++)
	files[i] = new File(selectedFiles[i].getAbsolutePath());
    }

    super.setSelectedFiles(files);
  }

  /**
   * Returns a list of selected files if the file chooser is
   * set to allow multiple selection.
   *
   * @return the selected file
   */
  @Override
  public File[] getSelectedFiles() {
    File[]	result;
    int		i;
    int		pos;

    result = super.getSelectedFiles();
    for (i = 0; i < result.length; i++) {
      // Unfortunately, JFileChooser automatically adds the current directory
      // to the filename. In case we have a placeholder in the name, we just
      // remove the part of the string preceding the placeholder start
      if ((pos = result[i].getPath().indexOf(Placeholders.PLACEHOLDER_START)) > -1)
	result[i] = new PlaceholderFile(result[i].getPath().substring(pos));
      else
	result[i] = new PlaceholderFile(result[i]);
    }

    return result;
  }

  /**
   * Returns the selected directories.
   *
   * @return the selected directories
   */
  public PlaceholderDirectory[] getSelectedDirectories() {
    PlaceholderDirectory[]	result;
    File[]			files;
    int				i;
    
    result = new PlaceholderDirectory[0];
    files  = getSelectedFiles();
    if (files.length > 0) {
      result = new PlaceholderDirectory[files.length];
      for (i = 0; i < files.length; i++)
	result[i] = new PlaceholderDirectory(files[i]);
    }
    
    return result;
  }

  /**
   * Returns the current directory.
   *
   * @return the current directory, as PlaceholderFile
   * @see #setCurrentDirectory
   */
  @Override
  public File getCurrentDirectory() {
    File	current;

    current = super.getCurrentDirectory();
    if (current == null)
      return null;
    else
      return new PlaceholderFile(current);
  }

  /**
   * Sets the current directory. Passing in <code>null</code> sets the
   * file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * If the file passed in as <code>currentDirectory</code> is not a
   * directory, the parent of the file will be used as the currentDirectory.
   * If the parent is not traversable, then it will walk up the parent tree
   * until it finds a traversable directory, or hits the root of the
   * file system.
   *
   * @param dir the current directory to point to
   * @see #getCurrentDirectory
   */
  @Override
  public void setCurrentDirectory(File dir) {
    if (dir == null)
      super.setCurrentDirectory(null);
    else
      super.setCurrentDirectory(new PlaceholderFile(dir).getAbsoluteFile());
  }

  /**
   * Displays the dialog.
   *
   * @param   parent  the parent component of the dialog;
   *			can be <code>null</code>
   * @param   approveButtonText the text of the <code>ApproveButton</code>
   * @return  the return state of the file chooser on popdown
   * @throws HeadlessException 	if GraphicsEnvironment.isHeadless()
   * 				returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  @Override
  public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
    m_PanelBookmarks.reload();
    return super.showDialog(parent, approveButtonText);
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    BaseDirectoryChooser chooser = new BaseDirectoryChooser();
    chooser.setCurrentDirectory(new PlaceholderFile("${TMP}"));
    if (chooser.showOpenDialog(null) == BaseDirectoryChooser.APPROVE_OPTION)
      System.out.println(chooser.getSelectedFile());
  }
}
