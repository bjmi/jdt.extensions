package bjmi.derivedresources.preferences;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * A utility class for dealing with preferences whose values are data types not covered by preference store. The static
 * methods on this class handle the conversion between these data types and their string representations.
 *
 * @author Björn Michael
 * @since 1.4
 */
public final class PreferenceConverter {

  private PreferenceConverter() {
    // prevent instantiation
    throw new UnsupportedOperationException("invocation of sealed constructor");
  }

  public static Collection<String> fromString(final String prefValue, final String delimiter) {
    return Splitter.on(delimiter).omitEmptyStrings().trimResults().splitToList(prefValue);
  }

  public static String toString(final Iterable<String> prefValue, final String delimiter) {
    return Joiner.on(delimiter).join(prefValue);
  }

}
