package derivedresources.internal;

import static derivedresources.preferences.Preferences.FOLDER_NAMES;
import static derivedresources.preferences.Preferences.SCAN_IN_BACKGROUND;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import derivedresources.folder.SearchDerivedFolderJob;
import derivedresources.preferences.PreferenceConverter;

/**
 * {@link BundleActivator} of derived resouces bundle.
 *
 * @author Björn Michael
 * @since 1.2
 */
public class DerivedResourcePlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "derivedresources";

  static DerivedResourcePlugin bundle;

  /**
   * @return single instance of {@link DerivedResourcePlugin} or {@code null} if plugin is {@link #stop(BundleContext) stopped}.
   */
  public static DerivedResourcePlugin getInstance() {
    return bundle;
  }

  private static void init() {
    final Boolean doBackgroundScan = SCAN_IN_BACKGROUND.getValue();
    if (doBackgroundScan) {
      final Iterable<String> folderNames = PreferenceConverter.fromString(FOLDER_NAMES.getValue(), ";");
      SearchDerivedFolderJob.schedule(folderNames);
    }
  }

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    bundle = this;

    init();
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    bundle = null;
    super.stop(context);
  }

}
