package bjmi.derivedresources.decoration;

import static org.eclipse.core.resources.IResource.CHECK_ANCESTORS;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
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

import bjmi.derivedresources.core.Logging;
import bjmi.derivedresources.internal.DerivedResourcePlugin;

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

    // http://www.vogella.com/tutorials/EclipseJFaceTree/article.html
    private ResourceManager resourceManager;

    private final LoadingCache<Image, Image> cache = CacheBuilder.newBuilder() //
                    .weakKeys() // when original image isn't in use anymore
                    .removalListener(new ImageRemovalListener()) //
                    .build(CacheLoader.from(DerivedResourceLabelDecorator::grayout));

    /**
     * Used by extension point registry.
     */
    public DerivedResourceLabelDecorator() {
        super();
//        register();
    }

    private static Image grayout(final Image original) {
        // redraw in gray
        return new Image(original.getDevice(), original, SWT.IMAGE_DISABLE);
    }

    // @javax.annotation.Nullable
    @Override
    public Image decorateImage(// javax.annotation.Nullable
                    final Image original, final Object element) {
        if (original == null) {
            return null; // nothing to grayout
        }

//        return decorateImage0(original, (IResource) element);
        return decorateImage1(original, (IResource) element);
    }

    // @javax.annotation.Nullable
    @Override
    public String decorateText(final String text, final Object element) {
        return null; // no text decoration should be set
    }

    @Override
    public void dispose() {
        super.dispose();
        if (resourceManager != null) {
            resourceManager.dispose();
            resourceManager = null;
        }
    }

    @Override
    public void addListener(final ILabelProviderListener listener) {
        super.addListener(listener);
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        super.removeListener(listener);
    }

    private void register() {
        // http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2FresAdv_events.htm
        ResourcesPlugin.getWorkspace().addResourceChangeListener(e -> {
            // 1. step
            if ((e.getDelta().getFlags() & IResourceDelta.DERIVED_CHANGED) != 0) { // TODO children has derived changed flag
                fireListenersInUIThread(e.getResource());
            }
        }, IResourceChangeEvent.POST_CHANGE);
    }

    protected ResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new LocalResourceManager(JFaceResources.getResources());
        }
        return resourceManager;
    }

    private Image decorateImage0(final Image original, final IResource resource) {
        if (resource.isDerived(CHECK_ANCESTORS)) {
            return cache.getUnchecked(original); // image should be grayed out
        }

        return null; // apply no decoration
    }

    private Image decorateImage1(final Image original, final IResource resource) {
        if (resource.isDerived(CHECK_ANCESTORS)) {
            ImageDescriptor origDesc = ImageDescriptor.createFromImage(original);
            ImageDescriptor grayDesc = ImageDescriptor.createWithFlags(origDesc, SWT.IMAGE_DISABLE);

            return getResourceManager().createImage(grayDesc); // image should be grayed out
        }

        return null; // apply no decoration
    }

    /**
     * Fire any listeners from the UIThread. Used for cases where this may be invoked outside of the UI by the public API.
     *
     * @param resource whose label needs to be updated
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
