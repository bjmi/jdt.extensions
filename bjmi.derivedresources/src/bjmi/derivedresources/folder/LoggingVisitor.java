package bjmi.derivedresources.folder;

import java.util.Locale;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO (michael) document me
 *
 * @author Björn Michael
 * @since 1.4.3
 */
final class LoggingVisitor extends ForwardingResourceVisitor {

  private final IProgressMonitor monitor;

  public LoggingVisitor(final IResourceVisitor delegate, final IProgressMonitor monitor) {
    super(delegate);
    this.monitor = monitor;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  @Override
  public boolean visit(final IResource resource) throws CoreException {
    monitor.setTaskName(String.format(Locale.ENGLISH, "Scanning: %s", resource.getProjectRelativePath()));
    return delegate.visit(resource);
  }

}
