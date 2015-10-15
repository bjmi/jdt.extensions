package derivedresources.decoration;

import static org.eclipse.core.resources.IResource.CHECK_ANCESTORS;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.decorators.DecoratorManager;
import org.eclipse.ui.progress.WorkbenchJob;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import derivedresources.core.Logging;
import derivedresources.internal.DerivedResourcePlugin;

/**
 * A {@link ILabelDecorator LabelDecorator} that gray out icons.
 *
 * @author Björn Michael
 * @since 1.0
 */
public class DerivedResourceLabelDecorator extends BaseLabelProvider implements ILabelDecorator {

  private static class ImageRemovalListener implements RemovalListener<Image, Image> {

    @Override
    public void onRemoval(final RemovalNotification<Image, Image> notification) {
      final Status status = new Status(IStatus.INFO, DerivedResourcePlugin.PLUGIN_ID, "Dispose gray image " + notification);
      Logging.log(status);

      final Image value = notification.getValue();
      if (value != null) {
        value.dispose();
      }
    }

  }

  private final LoadingCache<Image, Image> cache = CacheBuilder.newBuilder() //
      .weakKeys() // when original image isn't in use anymore
      .removalListener(new ImageRemovalListener()) //
      .build(new CacheLoader<Image, Image>() {

        @Override
        public Image load(final Image original) {
          return grayout(original);
        }

      });

  /**
   * Extension point id {@value #DECORATOR_ID} of the this {@link ILabelDecorator LabelDecorator}.
   */
  public static final String DECORATOR_ID = "derivedresources.decoration.derivedResourceDecorator";

  /**
   * Used by extension point registry.
   */
  public DerivedResourceLabelDecorator() {
    super();
  }

  private static Image grayout(final Image original) {
    // redraw in gray
    return new Image(original.getDevice(), original, SWT.IMAGE_DISABLE);
  }

  @Override
  public void addListener(final ILabelProviderListener listener) {
    super.addListener(listener);
  }

  @Override
  // javax.annotation.Nullable
  public Image decorateImage(// javax.annotation.Nullable
      final Image original, final Object element) {
    if (original == null) {
      // nothing to grayout
      return null;
    }

    return decorateImage0(original, (IResource) element);

    // return decorateImageWithCaching(image, element);
  }

  @Override
  // javax.annotation.Nullable
  public String decorateText(final String text, final Object element) {
    // no text decoration should be set
    return null;
  }

  private Image decorateImage0(final Image original, final IResource resource) {

    if (resource.isDerived(CHECK_ANCESTORS)) {
      // image should be grayed out
      return cache.getUnchecked(original);
    }

    // apply no decoration
    return null;
  }

  /**
   * Fire any listeners from the UIThread. Used for cases where this may be invoked outside of the UI by the public API.
   *
   * @param resource
   *          whose label needs to be updated
   */
  private void fireListenersInUIThread(final IResource resource) {

    final LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, resource);

    // No updates if there is no UI
    if (!PlatformUI.isWorkbenchRunning()) {
      return;
    }

    final WorkbenchJob updateJob = new WorkbenchJob(WorkbenchMessages.DecorationScheduler_UpdateJobName) {

      @Override
      public boolean belongsTo(final Object family) {
        return DecoratorManager.FAMILY_DECORATE == family;
      }

      @Override
      public IStatus runInUIThread(final IProgressMonitor monitor) {
        fireLabelProviderChanged(event);
        return Status.OK_STATUS;
      }

    };

    updateJob.setSystem(true);
    updateJob.schedule();
  }

}
