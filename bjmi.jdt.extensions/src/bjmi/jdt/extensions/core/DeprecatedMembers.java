package bjmi.jdt.extensions.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import bjmi.jdt.extensions.internal.JdtExtensionsPlugin;

/**
 * Utility class pertaining evaluation of deprecated members.
 *
 * @author Björn Michael
 * @since 1.1
 */
public final class DeprecatedMembers {

  private DeprecatedMembers() {
    // prevent instantiation
    throw new AssertionError("invocation of sealed constructor");
  }

  /**
   * @param member
   *          e.g. method or field in a java class
   * @return true if the element is marked as deprecated
   */
  public static boolean isDeprecated(final IMember member) {
    switch (member.getElementType()) {
      case IJavaElement.FIELD :
      case IJavaElement.METHOD :
        try {
          return Flags.isDeprecated(member.getFlags());
        } catch (final JavaModelException exc) {
          final Plugin instance = JdtExtensionsPlugin.getInstance();
          final Status status = new Status(IStatus.ERROR, instance.getBundle().getSymbolicName(), "type could not be inferred", exc);

          instance.getLog().log(status);
        }
    }
    return false;
  }

}
