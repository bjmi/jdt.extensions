package bjmi.jdt.extensions.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

import bjmi.jdt.extensions.internal.JdtExtensionsPlugin;

/**
 * Utility class pertaining evaluation of deprecated members.
 *
 * @author Björn Michael
 * @since 1.1 (formerly DeprecatedMembers)
 */
public final class JdtMembers {

    private JdtMembers() {
		throw new AssertionError("invocation of sealed constructor"); // Prevent instantiation
    }

    /**
     * @param member e.g. method, field etc. in a java class
     * @return {@code true} if the element is marked as deprecated, {@code false} otherwise or not determinable
     */
    public static boolean isDeprecated(final IMember member) {
        try {
            return Flags.isDeprecated(member.getFlags());
        } catch (final JavaModelException e) {
            final Plugin instance = JdtExtensionsPlugin.getInstance();
            final Status status = new Status(IStatus.INFO, instance.getBundle().getSymbolicName(),
                            "can't determine if member is deprecated due unretrievable flags", e);

            instance.getLog().log(status);
        }

        return false;
    }

    /**
     * @param type being tested
     * @return {@code true} if the element is marked as deprecated, {@code false} otherwise or not determinable
     * @since 1.3
     */
    public static boolean isDeprecated(final IType type) {
        try {
            return Flags.isDeprecated(type.getFlags());
        } catch (final JavaModelException e) {
            final Plugin instance = JdtExtensionsPlugin.getInstance();
            final Status status = new Status(IStatus.INFO, instance.getBundle().getSymbolicName(),
                            "can't determine if member is deprecated due unretrievable flags", e);

            instance.getLog().log(status);
        }

        return false;
    }

    /**
     * @param type being tested
     * @return {@code true} if the element is marked as deprecated, {@code false} otherwise or not determinable
     * @since 1.3
     */
    public static boolean isDeprecated(final ITypeRoot type) {
        IType primaryType = type.findPrimaryType();
        if (primaryType != null) {
            try {
                return Flags.isDeprecated(primaryType.getFlags());
            } catch (final JavaModelException e) {
                final Plugin instance = JdtExtensionsPlugin.getInstance();
                final Status status = new Status(IStatus.INFO, instance.getBundle().getSymbolicName(),
                                "can't determine if member is deprecated due unretrievable flags", e);

                instance.getLog().log(status);
            }
        }

        return false;
    }

}
