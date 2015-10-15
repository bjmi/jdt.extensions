package bjmi.derivedresources.decoration;
import static org.eclipse.core.resources.IResource.CHECK_ANCESTORS;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A contribution to extension point {@code org.eclipse.ui.navigator.navigatorContent}. Thereby derived
 * {@link IResource resources} could be hidden e.g. in Project Explorer view.
 * 
 * @author Björn Michael
 * @since 1.0
 */
public class DerivedResourceViewerFilter extends ViewerFilter {

  /**
   * {@inheritDoc}
   * 
   * Filters {@link IResource resources} that are {@link IResource#isDerived() derived}.
   */
  @Override
  public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

    if (element instanceof IResource) {
      if (((IResource) element).isDerived(CHECK_ANCESTORS)) {
        // make resource invisible
        return false;
      }
    }

    return true;
  }

}
