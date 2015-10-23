package bjmi.derivedresources.decoration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.junit.Test;

/**
 * @author Björn Michael
 * @since 1.2.2
 */
public final class DerivedResourceLabelDecoratorTest {

  /**
   * TODO check whether listeners get only notified when image was modified
   */
  private static class LabelProviderListener implements ILabelProviderListener {

    private boolean eventsReceived = false;

    @Override
    public void labelProviderChanged(final LabelProviderChangedEvent event) {
      eventsReceived = true;
    }

  }

  private static boolean equal(final ImageData data1, final ImageData data2) {
    return Arrays.equals(data1.data, data2.data) && //
        Arrays.equals(data1.maskData, data2.maskData) && //
        Arrays.equals(data1.alphaData, data2.alphaData) && //
        data1.width == data2.width && //
        data1.height == data2.height && //
        data1.depth == data2.depth;
  }

  @Test
  public void alwaysDecorate() {
    // setup data
    final Image original = ImageDescriptor.getMissingImageDescriptor().createImage();
    assertNotNull(original);
    final ImageData imageData = original.getImageData();
    final IResource mockedResource = mock(IResource.class);
    when(mockedResource.isDerived(IResource.CHECK_ANCESTORS)).thenReturn(true);

    final DerivedResourceLabelDecorator sut = new DerivedResourceLabelDecorator();

    // exercise
    final Image result = sut.decorateImage(original, mockedResource);

    // verify
    assertNotNull(result);
    assertFalse(equal(imageData, result.getImageData()));
    assertNotSame(original, result);

    // exercise
    final Image secondRun = sut.decorateImage(result, mockedResource);

    // verify
    assertNotNull(secondRun);
    assertNotSame(original, secondRun);
    assertNotSame(result, secondRun);
  }

  @Test
  public void decorateImage() {
    // setup data
    final Image original = ImageDescriptor.getMissingImageDescriptor().createImage();
    assertNotNull(original);
    final ImageData imageData = original.getImageData();
    final IResource mockedResource = when(mock(IResource.class).isDerived(IResource.CHECK_ANCESTORS)).thenReturn(true)
        .getMock();

    final DerivedResourceLabelDecorator sut = new DerivedResourceLabelDecorator();
    final LabelProviderListener listener = new LabelProviderListener();
    sut.addListener(listener);

    // exercise
    final Image result = sut.decorateImage(original, mockedResource);

    // verify
    assertNotNull(result);
    assertFalse(equal(imageData, result.getImageData()));
    assertNotSame(original, result);
  }

  /**
   * @since 1.4.2
   */
  @Test
  public void deliverCachedImage() {
    // setup data
    final Image original = ImageDescriptor.getMissingImageDescriptor().createImage();
    assertNotNull(original);
    final ImageData imageData = original.getImageData();
    final IResource mockedResource = when(mock(IResource.class).isDerived(IResource.CHECK_ANCESTORS)).thenReturn(true)
        .getMock();

    final DerivedResourceLabelDecorator sut = new DerivedResourceLabelDecorator();

    // exercise
    final Image result = sut.decorateImage(original, mockedResource);

    // verify
    assertNotNull(result);
    assertFalse(equal(imageData, result.getImageData()));
    assertNotSame(original, result);

    // exercise
    final Image secondRun = sut.decorateImage(original, mockedResource);

    // verify
    assertNotNull(secondRun);
    assertSame(result, secondRun);
  }

  @Test
  public void noArgConstructorExist() {
    assertNotNull(new DerivedResourceLabelDecorator());
  }

  @Test
  public void noDecoration() {
    // setup data
    final Image original = ImageDescriptor.getMissingImageDescriptor().createImage();
    assertNotNull(original);
    final IResource res = mock(IResource.class);

    final DerivedResourceLabelDecorator sut = new DerivedResourceLabelDecorator();

    // exercise
    final Image result = sut.decorateImage(original, res);

    // verify
    assertNull(result);
  }

  /**
   * @since 1.4.2
   */
  @Test
  public void noOriginalImage() {
    // setup data
    final Image original = null;
    final IResource mockedResource = when(mock(IResource.class).isDerived(IResource.CHECK_ANCESTORS)).thenReturn(true)
        .getMock();

    final DerivedResourceLabelDecorator sut = new DerivedResourceLabelDecorator();

    // exercise
    final Image result = sut.decorateImage(original, mockedResource);

    // verify
    assertNull(result);
  }

  /**
   * @since 1.3
   */
  @Test
  public void noTextDecoration() {
    // setup data
    final DerivedResourceLabelDecorator sut = new DerivedResourceLabelDecorator();
    final IResource mockedResource = when(mock(IResource.class).isDerived(IResource.CHECK_ANCESTORS)).thenReturn(true)
        .getMock();

    // exercise
    final String text = sut.decorateText("a string", mockedResource);

    // verify
    assertNull(text);
  }

}
