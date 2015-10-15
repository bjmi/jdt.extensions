package bjmi.derivedresources.preferences;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.preference.IPreferenceStore;

import bjmi.derivedresources.internal.DerivedResourcePlugin;

/**
 * TODO (michael) document me
 *
 * @author Björn Michael
 * @param <T>
 *          type of preference value e.g. String or Boolean.
 * @since 1.2
 */
public abstract class Preference<T> {

  /**
   * Preference key.
   */
  final String prefKey;

  /**
   * Default value for this preference.
   */
  final T def;

  private Preference(final String key, final T defaultValue) {
    prefKey = checkNotNull(key);
    def = checkNotNull(defaultValue);
  }

  static IPreferenceStore getPreferenceStore() {
    return DerivedResourcePlugin.getInstance().getPreferenceStore();
  }

  static void initDefaults(final IPreferenceStore defaultStore) {
    for (final Preference<?> pref : Preferences.PREFS) {
      pref.setDefault(defaultStore);
    }
  }

  static Preference<Boolean> of(final String key, final Boolean defaultValue) {
    return new Preference<Boolean>(key, defaultValue) {

      @Override
      public Boolean getValue() {
        return getPreferenceStore().getBoolean(prefKey);
      }

      @Override
      void setDefault(final IPreferenceStore defaultStore) {
        defaultStore.setDefault(prefKey, def);
      }

    };
  }

  static Preference<Integer> of(final String key, final Integer defaultValue) {
    return new Preference<Integer>(key, defaultValue) {

      @Override
      public Integer getValue() {
        return getPreferenceStore().getInt(prefKey);
      }

      @Override
      void setDefault(final IPreferenceStore defaultStore) {
        defaultStore.setDefault(prefKey, def);
      }

    };
  }

  static Preference<String> of(final String key, final String defaultValue) {
    return new Preference<String>(key, defaultValue) {

      @Override
      public String getValue() {
        return getPreferenceStore().getString(prefKey);
      }

      @Override
      void setDefault(final IPreferenceStore defaultStore) {
        defaultStore.setDefault(prefKey, def);
      }

    };
  }

  /**
   * @return current value of this preference in plugin preference store.
   */
  public abstract T getValue();

  /**
   * @param defaultStore
   *          of a preference initializer
   */
  abstract void setDefault(final IPreferenceStore defaultStore);

}
