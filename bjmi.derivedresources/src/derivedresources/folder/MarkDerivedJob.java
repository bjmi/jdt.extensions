package derivedresources.folder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import derivedresources.core.Logging;
import derivedresources.folder.DerivedFolderVisitor.VisitorStrategy;
import derivedresources.internal.DerivedResoucesMessages;
import derivedresources.internal.DerivedResourcePlugin;

/**
 * Search for folders that aren't marked as derived.
 *
 * @author Björn Michael
 * @since 1.1
 */
public class MarkDerivedJob extends WorkspaceJob {

  /**
   * @author Björn Michael
   * @since 1.0
   */
  static final class CountAndMarkDerivedStrategy implements VisitorStrategy {

    private final IProgressMonitor monitor;
    private int count = 0;

    CountAndMarkDerivedStrategy(final IProgressMonitor progressMonitor) {
      monitor = progressMonitor;
    }

    @Override
    public void apply(final IFolder folder) {
      try {
        folder.setDerived(true, monitor);
        count++;
      } catch (final CoreException exc) {
        asyncExec(new Runnable() {
          @Override
          public void run() {
            MessageDialog.openError(null, DerivedResoucesMessages.MarkDerivedAction_Error_Title,
                NLS.bind(DerivedResoucesMessages.MarkDerivedAction_Error_Message, exc.getMessage()));
          }
        });
      }
    }

  }

  private final Iterable<String> folderNames;
  private CancelableVisitor visitor;

  private MarkDerivedJob(final String jobName, final Iterable<String> folderNames) {
    super(jobName);
    this.folderNames = folderNames;
  }

  /**
   * Creates and schedules a new job to be run.
   *
   * @param folderNames
   *          that should be marked as derived
   */
  public static synchronized void schedule(final Iterable<String> folderNames) {
    new MarkDerivedJob(DerivedResoucesMessages.MarkDerivedJob_Job_Name, folderNames).schedule();
  }

  private static void asyncExec(final Runnable runnable) {
    PlatformUI.getWorkbench().getDisplay().asyncExec(runnable);
  }

  @Override
  public IStatus runInWorkspace(final IProgressMonitor monitor) {
    monitor.beginTask(DerivedResoucesMessages.MarkDerivedJob_Search, IProgressMonitor.UNKNOWN);

    final CountAndMarkDerivedStrategy strategy = new CountAndMarkDerivedStrategy(monitor);
    try {
      visitor = new CancelableVisitor(new LoggingVisitor(new DerivedFolderVisitor(folderNames, strategy), monitor));
      ResourcesPlugin.getWorkspace().getRoot().accept(visitor);
    } catch (final CoreException exc) {
      Logging.log(DerivedResourcePlugin.getInstance(), exc.getStatus());
      // TODO reconcile messages
      // MessageDialog.openError(null, DerivedResoucesMessages.MarkDerivedAction_Error_Title,
      // NLS.bind(DerivedResoucesMessages.MarkDerivedAction_Error_Message, exc.getMessage()));
      return exc.getStatus();
    } finally {
      monitor.setTaskName(DerivedResoucesMessages.MarkDerivedJob_Search_Complete);
      monitor.done();
      visitor = null;
    }
    if (strategy.count > 0) {
      asyncExec(new Runnable() {
        @Override
        public void run() {
          MessageDialog.openInformation(null, DerivedResoucesMessages.MarkDerivedAction_Result_Title,
              NLS.bind(DerivedResoucesMessages.MarkDerivedAction_Result_Message, strategy.count));
        }
      });
    }
    return Status.OK_STATUS;
  }

  @Override
  protected void canceling() {
    if (visitor != null) {
      final Status status = new Status(IStatus.CANCEL, DerivedResourcePlugin.PLUGIN_ID, DerivedResoucesMessages.MarkDerivedJob_Cancel);
      visitor.cancelWith(status);
    }
    super.canceling();
  }

}
