package bjmi.derivedresources.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * @author Björn Michael
 * @since 1.4
 */
public final class DerivedResourcesMessagesTest {

  @Test
  public void initializeMessages() throws Exception {
    String missingPrefix = "NLS missing message"; // from org.eclipse.osgi.util.NLS
    boolean atLeastOneFieldFound = false;

    for (Field field : DerivedResourcesMessages.class.getFields()) {
      int modifiers = field.getModifiers();
      if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
        atLeastOneFieldFound |= true;
        String message = (String) field.get(null);
        assertNotNull("missing NLS for field: " + field.getName(), message);
        assertFalse(message.startsWith(missingPrefix));
      }
    }
    assertTrue(atLeastOneFieldFound);
  }

}
