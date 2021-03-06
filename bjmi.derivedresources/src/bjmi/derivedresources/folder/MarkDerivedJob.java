package bjmi.derivedresources.folder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import bjmi.derivedresources.core.Logging;
import bjmi.derivedresources.folder.DerivedFolderVisitor.VisitorStrategy;
import bjmi.derivedresources.internal.DerivedResourcePlugin;
import bjmi.derivedresources.internal.DerivedResourcesMessages;

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
        private final List<String> paths = new LinkedList<>();

        CountAndMarkDerivedStrategy(final IProgressMonitor progressMonitor) {
            monitor = progressMonitor;
        }

        @Override
        public void apply(final IFolder folder) {
            try {
                folder.setDerived(true, monitor);
                paths.add(folder.getFullPath().toString());
            } catch (final CoreException exc) {
                asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        MessageDialog.openError(null, DerivedResourcesMessages.MarkDerivedAction_Error_Title,
                                        NLS.bind(DerivedResourcesMessages.MarkDerivedAction_Error_Message, exc.getMessage()));
                    }
                });
            }
        }

    }

    private final Collection<String> folderNames;
    private CancelableVisitor visitor;

    private MarkDerivedJob(final String jobName, final Collection<String> folderNames) {
        super(jobName);
        this.folderNames = folderNames;
    }

    /**
     * Creates and schedules a new job to be run.
     *
     * @param folderNames
     *            that should be marked as derived
     */
    public static synchronized void schedule(final Collection<String> folderNames) {
        new MarkDerivedJob(DerivedResourcesMessages.MarkDerivedJob_Job_Name, folderNames).schedule();
    }

    private static void asyncExec(final Runnable runnable) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(runnable);
    }

    @Override
    public IStatus runInWorkspace(final IProgressMonitor monitor) {
        monitor.beginTask(DerivedResourcesMessages.MarkDerivedJob_Search, IProgressMonitor.UNKNOWN);

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
            monitor.setTaskName(DerivedResourcesMessages.MarkDerivedJob_Search_Complete);
            monitor.done();
            visitor = null;
        }
        if (!strategy.paths.isEmpty()) {
            asyncExec(new Runnable() {
                @Override
                public void run() {
                    MultiStatus ms = new MultiStatus(DerivedResourcePlugin.PLUGIN_ID, IStatus.INFO,
                                    NLS.bind(DerivedResourcesMessages.MarkDerivedAction_Result_Message, strategy.paths.size()), null);
                    for (String path : strategy.paths) {
                        ms.add(new Status(IStatus.INFO, DerivedResourcePlugin.PLUGIN_ID, path));
                    }
                    ErrorDialog.openError(null, DerivedResourcesMessages.MarkDerivedAction_Result_Title, //
                                    null, // set null to show message from multistatus
                                    ms);
                }
            });
        }
        return Status.OK_STATUS;
    }

    @Override
    protected void canceling() {
        if (visitor != null) {
            final Status status = new Status(IStatus.CANCEL, DerivedResourcePlugin.PLUGIN_ID,
                            DerivedResourcesMessages.MarkDerivedJob_Cancel);
            visitor.cancelWith(status);
        }
        super.canceling();
    }

}
