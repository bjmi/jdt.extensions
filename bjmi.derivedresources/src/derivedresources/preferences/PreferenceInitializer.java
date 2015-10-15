package derivedresources.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import derivedresources.internal.DerivedResourcePlugin;

/**
 * Class used to initialize default preference values.
 * 
 * @author Björn Michael
 * @since 1.2
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    final IPreferenceStore store = DerivedResourcePlugin.getInstance().getPreferenceStore();
    Preference.initDefaults(store);
  }

}
