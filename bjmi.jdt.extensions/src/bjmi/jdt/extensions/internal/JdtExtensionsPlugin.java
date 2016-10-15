package bjmi.jdt.extensions.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Björn Michael
 * @since 1.0
 */
public class JdtExtensionsPlugin extends AbstractUIPlugin {

    // The shared instance
    private static JdtExtensionsPlugin plugin;

    /**
     * @since 1.1
     */
    public JdtExtensionsPlugin() {
        super();
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static JdtExtensionsPlugin getInstance() {
        return plugin;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

}
