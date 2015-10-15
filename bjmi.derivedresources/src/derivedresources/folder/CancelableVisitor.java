package derivedresources.folder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

/**
 * TODO (michael) document me
 *
 * This resource visitor throws a {@link CoreException} on next invocation of {@link #visit(IResource)} if {@link #cancelWith(IStatus)} was
 * called before.
 *
 * @author Björn Michael
 * @since 1.4.3
 */
final class CancelableVisitor extends ForwardingResourceVisitor {

  private IStatus status;

  public CancelableVisitor(final IResourceVisitor delegate) {
    super(delegate);
  }

  public void cancelWith(final IStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @Override
  public boolean visit(final IResource resource) throws CoreException {
    if (status != null) {
      throw new CoreException(status);
    }
    return delegate.visit(resource);
  }

}
