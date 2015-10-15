package bjmi.derivedresources.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/**
 * Predefinied predicates.
 *
 * @author Björn Michael
 * @since 1.2
 */
public final class DerivedPredicates {

  private static final class EqualIgnoreCase implements Predicate<String> {

    private final String target;

    EqualIgnoreCase(final String target) {
      this.target = checkNotNull(target);
    }

    @Override
    public boolean apply(final String input) {
      // foldername is correct.
      return target.equalsIgnoreCase(input);
    }

    /**
     * @since TODO
     */
    @Override
    public String toString() {
      return DerivedPredicates.class.getSimpleName() + ".equalIgnoreCase(\"" + target + "\")";
    }

  }

  private enum NotNullNorEmpty implements Predicate<String> {

    INSTANCE;

    @Override
    public boolean apply(final String input) {
      // foldername is correct.
      return !Strings.isNullOrEmpty(input);
    }

    /**
     * @since TODO
     */
    @Override
    public String toString() {
      return DerivedPredicates.class.getSimpleName() + ".notNullNorEmpty()";
    }

  }

  private DerivedPredicates() {
    // prevent instantiation
    throw new UnsupportedOperationException("invocation of sealed constructor");
  }

  /**
   * @param to
   *          given string is equal ignoring case
   * @return a predicate that evaluates wheather a string is equal ignoring case.
   * @since 1.4
   */
  public static Predicate<String> equalIgnoreCase(final String to) {
    return new EqualIgnoreCase(to);
  }

  /**
   * @return a predicate that evaluates wheather a string is not null nor empty.
   */
  public static Predicate<String> notNullNorEmpty() {
    return NotNullNorEmpty.INSTANCE;
  }

}
