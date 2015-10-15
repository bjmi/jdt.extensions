package bjmi.derivedresources.folder;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * TODO document me
 *
 * @author Björn Michael
 * @since 1.4.3
 */
public abstract class ForwardingResourceVisitor implements IResourceVisitor {

  protected IResourceVisitor delegate;

  protected ForwardingResourceVisitor(final IResourceVisitor target) {
    delegate = checkNotNull(target);
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || delegate.equals(obj);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean visit(final IResource resource) throws CoreException {
    return delegate.visit(resource);
  }

}
