package bjmi.jdt.extensions.core;

import bjmi.jdt.extensions.internal.JdtExtensionsPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Utility class pertaining evaluation of deprecated members.
 *
 * @author Björn Michael
 * @since 1.1 (formerly DeprecatedMembers)
 */
public final class JdtMembers {

  private JdtMembers() {
    throw new AssertionError("invocation of sealed constructor"); // prevent instantiation
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
        } catch (final JavaModelException e) {
          final Plugin instance = JdtExtensionsPlugin.getInstance();
          final Status status = new Status(IStatus.INFO, instance.getBundle().getSymbolicName(),
              "can't determine if member is deprecated due unretrievable flags", e);

          instance.getLog().log(status);
        }
    }
    return false;
  }

  /**
   * @param member of a java class
   * @return true if {@code member} stems from {@link java.lang.Object}
   * @since 1.2
   */
  public static boolean isFromJavaLangObject(final IMember member) {
    switch (member.getElementType()) {
    case IJavaElement.METHOD :
        System.out.println(member);
        IClassFile classFile = member.getClassFile();
        if (classFile != null) {
          IType type = classFile.getType();
          String fullyQualifiedName = type.getFullyQualifiedName();
          if ("java.lang.Object".equals(fullyQualifiedName)) {
            return true;
          }
        }
      }
      /*
      catch (final JavaModelException e) {
        final Plugin instance = JdtExtensionsPlugin.getInstance();
        final Status status = new Status(IStatus.ERROR, instance.getBundle().getSymbolicName(), "type could not be inferred", e);

        instance.getLog().log(status);
      }
      */
    return false;
  }

}
