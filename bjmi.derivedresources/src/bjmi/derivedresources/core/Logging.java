package bjmi.derivedresources.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;

import bjmi.derivedresources.internal.DerivedResourcePlugin;

/**
 * TODO(michael) document me
 *
 * @author Björn Michael
 * @since 1.4
 */
public final class Logging {

    private Logging() {
        throw new AssertionError("invocation of sealed constructor"); // prevent instantiation
    }

    /**
     * Logs an entry with {@link DerivedResourcePlugin} logger.
     *
     * @param status being used as log entry
     * @since 1.4.3
     */
    public static void log(final IStatus status) {
        DerivedResourcePlugin.getInstance().getLog().log(status);
    }

    /**
     * @param plugin logger being used
     * @param status being used as log entry
     */
    public static void log(final Plugin plugin, final IStatus status) {
        plugin.getLog().log(status);
    }

}
