package bjmi.jdt.extensions.decoratations;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import bjmi.jdt.extensions.core.JdtMembers;

/**
 * A {@link ILightweightLabelDecorator LightweightLabelDecorator} that strikes through text.
 *
 * @author Björn Michael
 * @since 1.1
 */
public class DeprecatedMembersLabelDecorator extends BaseLabelProvider implements ILightweightLabelDecorator, IStyledLabelProvider {

    private ResourceManager resourceManager;

    private Font strikeThroughFont;

    private boolean isInitialized = false;

    /**
     * Creates a new label decorator instance.
     */
    public DeprecatedMembersLabelDecorator() {
        super(); // instantiated by org.eclipse.ui.decorators extension point
    }

    @Override
    public void addListener(final ILabelProviderListener listener) {
        // nobody has to be informed
    }

    @Override
    public StyledString getStyledText(Object element) {
        // see org.eclipse.ui.decorators <enablement> section
        if (element instanceof IMember) {
            if (JdtMembers.isDeprecated((IMember) element)) {
//                decoration.setFont(strikeThroughFont);
                StyledString styledString = new StyledString();


                TextStyle ts = new TextStyle();
                ts.strikeout = true;

                Styler styler = StyledString.createColorRegistryStyler(null, null);
                styler.applyStyles(ts);

                styledString.append("hustensaft", styler);
                return styledString;
            }
        }

        return null;
    }

    @Override
    public void decorate(final Object element, final IDecoration decoration) {
        initializeFont();
        // strike through style is not supported on current platform
        if (strikeThroughFont == null) {
            return;
        }

        if (element instanceof IPackageFragment) {
            IPackageFragment pf = (IPackageFragment) element;
            IJavaElement primaryElement = pf.getPrimaryElement();
            String elementName = primaryElement.getElementName();
            IJavaModel javaModel = pf.getJavaModel();
            ICompilationUnit packageInfo = pf.getCompilationUnit("package-info.java");
            try {
                IPackageDeclaration iPackageDeclaration = packageInfo.getPackageDeclarations()[0];
                IAnnotation annotation = iPackageDeclaration.getAnnotation(Deprecated.class.getName());
                if (annotation != null) {
                    decoration.setFont(strikeThroughFont);
                }
                IJavaElement[] children = pf.getChildren();
                return;
            } catch (JavaModelException e) {
                if (!e.isDoesNotExist()) {
                    e.printStackTrace();
                }
            }
            // TODO
            return;
        }

        if (element instanceof ITypeRoot) {
            if (JdtMembers.isDeprecated((ITypeRoot) element)) {
                decoration.setFont(strikeThroughFont);
            }
            return;
        }

        if (element instanceof IType) {
            if (JdtMembers.isDeprecated((IType) element)) {
                decoration.setFont(strikeThroughFont);
            }
            return;
        }

        // see org.eclipse.ui.decorators <enablement> section
        if (element instanceof IMember) {
            if (JdtMembers.isDeprecated((IMember) element)) {
//                decoration.setFont(strikeThroughFont);
            }
            return;
        }
    }

    @Override
    public void dispose() {
        // XXX dispose font, but when?
        if (resourceManager != null) {
            resourceManager.dispose();
            resourceManager = null;
        }
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return true;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        // nobody has to be informed
    }

    protected ResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new LocalResourceManager(JFaceResources.getResources());
        }
        return resourceManager;
    }

    private void initializeFont() {
        if (!isInitialized) {
            initializeFonts();
            isInitialized = true;
        }
    }

    // only win32 has strike-through font; inspired by org.eclipse.mylyn.commons.ui.compatibility.CommonFonts.init()
    private void initializeFonts() {
        // final FontData[] defaultData = JFaceResources.getDefaultFont().getFontData();
        FontData[] fontData = FontDescriptor.copy(JFaceResources.getDefaultFont().getFontData());

        if (fontData != null && fontData.length == 1) {

            // final FontData data = new FontData(defaultData[0].getName(), defaultData[0].getHeight(), defaultData[0].getStyle());

            if ("win32".equals(SWT.getPlatform())) { //$NON-NLS-1$
                // NOTE: Windows only, for: data.data.lfStrikeOut = 1;
                try {
                    fontData[0].data.lfStrikeOut = 1;
                    strikeThroughFont = getResourceManager().createFont(FontDescriptor.createFrom(fontData));
                    // strikeThroughFont = new Font(Display.getCurrent(), data);
                } catch (final Throwable t) {
                    // ignore
                }
            }
        }
    }


    @Override
    public Image getImage(Object element) {
        return null;
    }

}
