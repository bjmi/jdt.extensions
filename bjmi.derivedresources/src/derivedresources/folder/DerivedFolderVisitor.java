package derivedresources.folder;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

import com.google.common.collect.Iterables;

import derivedresources.core.DerivedPredicates;

/**
 * This resource visitor can be applied to any {@link IResource#accept(IResourceVisitor)}. For every folder with given folder name that is
 * currently not set derived is forwarded to given {@link VisitorStrategy}.
 *
 * @author Björn Michael
 * @since 1.1
 */
final class DerivedFolderVisitor implements IResourceVisitor {

  public interface VisitorStrategy {

    void apply(IFolder folder);

  }

  private final Iterable<String> folderNames;

  private final VisitorStrategy visitorStrategy;

  public DerivedFolderVisitor(final Iterable<String> folderNames, final VisitorStrategy visitorStrategy) {
    this.folderNames = checkNotNull(folderNames);
    this.visitorStrategy = checkNotNull(visitorStrategy);
  }

  @Override
  public boolean visit(final IResource resource) {
    if (resource instanceof IProject) {
      final IProject project = (IProject) resource;
      if (!project.isOpen()) {
        // project must be open otherwise the visitor causes a CoreException
        return false;
      }
    }

    if (resource instanceof IFolder) {
      final IFolder folder = (IFolder) resource;
      if (Iterables.any(folderNames, DerivedPredicates.equalIgnoreCase(folder.getName()))) {
        if (folder.exists() && !folder.isDerived()) {
          visitorStrategy.apply(folder);
        }
        return false;
      }
    }
    return true;
  }

}
