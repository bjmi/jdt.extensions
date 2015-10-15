package bjmi.derivedresources.preferences;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Constant definitions for plug-in preferences.
 *
 * @author Björn Michael
 * @since 1.3
 */
public final class Preferences {

  /**
   * Preference for default folder name to mark as derived.
   */
  public static Preference<String> FOLDER_NAMES = Preference.of("folderNames", "target");

  /**
   * Preference for scan folders in background.
   */
  public static Preference<Boolean> SCAN_IN_BACKGROUND = Preference.of("scanInBackground", false);

  /**
   * Preference for scan folders in background period in minutes.
   *
   * @since TODO
   */
  public static Preference<Integer> SCAN_IN_BACKGROUND_PERIOD = Preference.of("scanInBackgroundPeriod", 60);

  /**
   * Also add new constants here!
   */
  static List<Preference<?>> PREFS = ImmutableList.<Preference<?>> of( //
      FOLDER_NAMES, //
      SCAN_IN_BACKGROUND, //
      SCAN_IN_BACKGROUND_PERIOD //
  );

  private Preferences() {
    // prevent instantiation
    throw new AssertionError("invocation of sealed constructor");
  }

}
