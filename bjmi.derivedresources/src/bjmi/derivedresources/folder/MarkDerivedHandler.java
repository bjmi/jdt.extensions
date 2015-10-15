package bjmi.derivedresources.folder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import bjmi.derivedresources.core.DerivedPredicates;
import bjmi.derivedresources.internal.DerivedResoucesMessages;
import bjmi.derivedresources.preferences.PreferenceConverter;
import bjmi.derivedresources.preferences.Preferences;

/**
 * After entering a folder name this {@link IHandler2} traverses all projects and sets the derived flag if the folder name is equal to the
 * entered name.
 *
 * @author Björn Michael
 * @since 1.0
 */
public final class MarkDerivedHandler extends AbstractHandler {

  /**
   * @author Björn Michael
   * @since 1.0
   */
  private static final class FolderNameValidator implements IInputValidator {

    private final Splitter SEMICOLON_SPLITTER = Splitter.on(';').omitEmptyStrings().trimResults();

    @Override
    public String isValid(final String txt) {
      final Iterable<String> folderNames = SEMICOLON_SPLITTER.split(txt);

      if (Iterables.isEmpty(folderNames)) {
        return DerivedResoucesMessages.MarkDerivedAction_Error_AtLeastOneFolder;
      }

      if (Iterables.all(folderNames, DerivedPredicates.notNullNorEmpty())) {
        // no error
        return null;
      }

      return DerivedResoucesMessages.MarkDerivedAction_Error_Folder;
    }

  }

  /**
   * Extension point id of the corresponding command.
   */
  public static final String COMMAND_ID = "derivedresources.folder.mark";

  @Override
  public Object execute(final ExecutionEvent event) {
    final String folderNamesFromPrefs = Preferences.FOLDER_NAMES.getValue();

    final InputDialog dialog = new InputDialog(null, DerivedResoucesMessages.MarkDerivedAction_Input_Title,
        DerivedResoucesMessages.MarkDerivedAction_Input_Description, folderNamesFromPrefs, new FolderNameValidator());
    if (dialog.open() == Window.OK) {
      final Iterable<String> folderNames = PreferenceConverter.fromString(dialog.getValue(), ";");
      MarkDerivedJob.schedule(folderNames);
    }

    return null;
  }

}
