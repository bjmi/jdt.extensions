package bjmi.jdt.extensions.filters;

import bjmi.jdt.extensions.core.JdtMembers;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * This filter can exclude members of {@link java.lang.Object} in JDT outline view.
 *
 * @author Björn Michael
 * @since 1.2
 */
public class JavaLangObjectMembersFilter extends ViewerFilter {

  /**
   * Creates a new filter instance.
   */
  public JavaLangObjectMembersFilter() {
    // instantiated by org.eclipse.jdt.ui.javaElementFilters extension
    super();
  }

  @Override
  public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

    if (element instanceof IMember) {
      return !JdtMembers.isFromJavaLangObject((IMember) element);
    }

    return true;
  }

}
