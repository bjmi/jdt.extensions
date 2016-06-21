package bjmi.jdt.extensions.decoratations;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import bjmi.jdt.extensions.core.JdtMembers;

/**
 * A {@link ILightweightLabelDecorator LightweightLabelDecorator} that strikes through text.
 *
 * @author Björn Michael
 * @since 1.1
 */
public class DeprecatedMembersLabelDecorator extends BaseLabelProvider implements ILightweightLabelDecorator {

  private Font strikeThroughFont;

  public DeprecatedMembersLabelDecorator() {
    initializeFonts();
  }

  @Override
  public void addListener(final ILabelProviderListener listener) {
    // nobody has to be informed
  }

  @Override
  public void decorate(final Object element, final IDecoration decoration) {
    // strike through style is not supported on current platform
    if (strikeThroughFont == null) {
      return;
    }

    final IMember member = (IMember) element;
    if (JdtMembers.isDeprecated(member)) {
      decoration.setFont(strikeThroughFont);
    }
  }

  @Override
  public void dispose() {
    // XXX dispose font, but when?
  }

  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return true;
  }

  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // nobody has to be informed
  }

  // only win32 has strike-through font
  private void initializeFonts() {
    final FontData[] defaultData = JFaceResources.getDefaultFont().getFontData();

    if (defaultData != null && defaultData.length == 1) {
      final FontData data = new FontData(defaultData[0].getName(), defaultData[0].getHeight(), defaultData[0].getStyle());

      if ("win32".equals(SWT.getPlatform())) { //$NON-NLS-1$
        // NOTE: Windows only, for: data.data.lfStrikeOut = 1;
        try {
          data.data.lfStrikeOut = 1;
          strikeThroughFont = new Font(Display.getCurrent(), data);
        } catch (final Throwable t) {
          // ignore
        }
      }
    }
  }

}
