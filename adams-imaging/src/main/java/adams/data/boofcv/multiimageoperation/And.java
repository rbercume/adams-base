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
 * And.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.multiimageoperation;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Performs a logical AND on the pixels of the images, i.e., if both are the same, the resulting pixel is black, otherwise white.<br/>
 * Converts images automatically to type UNSIGNED_INT_8.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class And
  extends AbstractBoofCVMultiImageOperation {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical AND on the pixels of the images, i.e., if both are the same, the resulting pixel is black, "
	+ "otherwise white.\n"
	+ "Converts images automatically to type " + BoofCVImageType.UNSIGNED_INT_8 + ".";
  }

  /**
   * Returns the number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means any number accepted
   */
  @Override
  public int numImagesRequired() {
    return 2;
  }

  /**
   * Checks the images.
   * <p/>
   * Default implementation only ensures that images are present.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(BoofCVImageContainer[] images) {
    super.check(images);

    if (!checkSameDimensions(images[0], images[1]))
      throw new IllegalStateException(
	"Both images need to have the same dimensions: "
	  + images[0].getWidth() + "x" + images[0].getHeight()
	  + " != "
	  + images[1].getWidth() + "x" + images[1].getHeight());
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doProcess(BoofCVImageContainer[] images) {
    BoofCVImageContainer[]	result;
    int				x;
    int				y;
    int				and;
    ImageUInt8			img0;
    ImageUInt8			img1;
    ImageUInt8			output;

    result    = new BoofCVImageContainer[1];
    img0      = (ImageUInt8) BoofCVHelper.toBoofCVImage(images[0], BoofCVImageType.UNSIGNED_INT_8);
    img1      = (ImageUInt8) BoofCVHelper.toBoofCVImage(images[1], BoofCVImageType.UNSIGNED_INT_8);
    output    = (ImageUInt8) BoofCVHelper.clone(img0);
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	and = (img0.get(x, y) == img1.get(x, y)) ? 0 : 1;
	output.set(x, y, and);
      }
    }
    result[0] = new BoofCVImageContainer();
    result[0].setImage(output);

    return result;
  }
}
