package bjmi.derivedresources.folder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import bjmi.derivedresources.core.Logging;
import bjmi.derivedresources.folder.DerivedFolderVisitor.VisitorStrategy;
import bjmi.derivedresources.internal.DerivedResourcePlugin;
import bjmi.derivedresources.internal.DerivedResourcesMessages;
import bjmi.derivedresources.preferences.Preferences;

/**
 * Search for folders that aren't marked as derived.
 *
 * @author Björn Michael
 * @since 1.1 (renamed from MarkDerivedJob)
 */
public class SearchDerivedFoldersJob extends WorkspaceJob implements VisitorStrategy {

    private static SearchDerivedFoldersJob instance;

    private final Collection<String> folderNames;
    private CancelableVisitor visitor;

    private SearchDerivedFoldersJob(final String jobName, final Collection<String> folderNames) {
        super(jobName);
        this.folderNames = folderNames;

        // hide this job from user in the UI
        setSystem(true);
    }

    /**
     * Schedules this job to be run. An existing job will be canceled and a new job will be created.
     *
     * @param folderNames
     *            that should be marked as derived.
     */
    public static synchronized void schedule(final Collection<String> folderNames) {
        if (instance != null) {
            instance.cancel();
        }

        instance = new SearchDerivedFoldersJob(DerivedResourcesMessages.MarkDerivedJob_Job_Name, folderNames);
        instance.schedule();
    }

    private static void aSyncExec(final Runnable runnable) {
        PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
    }

    private static void openDialog() {
        aSyncExec(new Runnable() {
            @Override
            public void run() {
                final IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
                try {
                    handlerService.executeCommand(MarkDerivedHandler.COMMAND_ID, null);
                } catch (final ExecutionException exc) {
                    Logging.log(DerivedResourcePlugin.getInstance(),
                                    new Status(IStatus.ERROR, DerivedResourcePlugin.PLUGIN_ID, "Command execution failed", exc));
                } catch (final NotDefinedException exc) {
                    Logging.log(DerivedResourcePlugin.getInstance(),
                                    new Status(IStatus.ERROR, DerivedResourcePlugin.PLUGIN_ID, "Command not defined", exc));
                } catch (final NotEnabledException exc) {
                    Logging.log(DerivedResourcePlugin.getInstance(),
                                    new Status(IStatus.ERROR, DerivedResourcePlugin.PLUGIN_ID, "Command not enabled", exc));
                } catch (final NotHandledException exc) {
                    Logging.log(DerivedResourcePlugin.getInstance(),
                                    new Status(IStatus.ERROR, DerivedResourcePlugin.PLUGIN_ID, "Command not handled", exc));
                }

            }
        });
    }

    @Override
    public void apply(final IFolder folder) {
        cancel();
        openDialog();
    }

    @Override
    public IStatus runInWorkspace(final IProgressMonitor monitor) {
        monitor.beginTask(DerivedResourcesMessages.MarkDerivedJob_Search, IProgressMonitor.UNKNOWN);
        try {
            visitor = new CancelableVisitor(new LoggingVisitor(new DerivedFolderVisitor(folderNames, this), monitor));
            ResourcesPlugin.getWorkspace().getRoot().accept(visitor);
        } catch (final CoreException exc) {
            Logging.log(DerivedResourcePlugin.getInstance(), exc.getStatus());
            // TODO reconcile messages
            // MessageDialog.openError(null, DerivedResoucesMessages.MarkDerivedAction_Error_Title,
            // NLS.bind(DerivedResoucesMlessages.MarkDerivedAction_Error_Message, exc.getMessage()));
            return exc.getStatus();
        } finally {
            monitor.setTaskName(DerivedResourcesMessages.MarkDerivedJob_Search_Complete);
            monitor.done();
            visitor = null;
            if (Preferences.SCAN_IN_BACKGROUND.getValue()) {
                schedule(TimeUnit.SECONDS.toMillis(Preferences.SCAN_IN_BACKGROUND_PERIOD.getValue()));
            }
        }
        return Status.OK_STATUS;
    }

    @Override
    protected void canceling() {
        if (visitor != null) {
            final Status status = new Status(IStatus.CANCEL, DerivedResourcePlugin.PLUGIN_ID, "Cancelling mark folders as derived job.");
            visitor.cancelWith(status);
        }
        super.canceling();
    }

}
