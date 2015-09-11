package bjmi.jdt.extensions.filters;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import bjmi.jdt.extensions.core.DeprecatedMembers;

/**
 * This filter can exclude deprecated members of the jdt outline view.
 *
 * @author Björn Michael
 * @since 1.0
 */
public class DeprecatedMembersFilter extends ViewerFilter {

  /**
   * Creates a new filter instance.
   */
  public DeprecatedMembersFilter() {
    // instantiated by org.eclipse.jdt.ui.javaElementFilters extension
    super();
  }

  @Override
  public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

    if (element instanceof IMember) {
      return !DeprecatedMembers.isDeprecated((IMember) element);
    }

    return true;
  }

}
