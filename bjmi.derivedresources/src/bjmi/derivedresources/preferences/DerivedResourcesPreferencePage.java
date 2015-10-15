package bjmi.derivedresources.preferences;

import static bjmi.derivedresources.preferences.Preferences.FOLDER_NAMES;
import static bjmi.derivedresources.preferences.Preferences.SCAN_IN_BACKGROUND;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bjmi.derivedresources.folder.SearchDerivedFolderJob;
import bjmi.derivedresources.internal.DerivedResoucesMessages;
import bjmi.derivedresources.internal.DerivedResourcePlugin;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main
 * plug-in class. That way, preferences can be accessed directly via the preference store. TODO docu
 * 
 * @author Björn Michael
 * @since 1.2
 */
public class DerivedResourcesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  /**
   * Extension point id of this PreferencePage.
   */
  public static final String PREFERENCE_PAGE_ID = "deriveddecoration.preferences.DerivedResourcesPreferencePage";

  public DerivedResourcesPreferencePage() {
    super(GRID);
    setPreferenceStore(DerivedResourcePlugin.getInstance().getPreferenceStore());
    setDescription(DerivedResoucesMessages.DerivedResourcesPreferencePage_Description);

  }

  @Override
  public void createFieldEditors() {
    final StringFieldEditor stringEditor = new StringFieldEditor(FOLDER_NAMES.prefKey,
        DerivedResoucesMessages.DerivedResourcesPreferencePage_FolderName_Label, getFieldEditorParent());
    stringEditor.getLabelControl(getFieldEditorParent()).setToolTipText(
        DerivedResoucesMessages.DerivedResourcesPreferencePage_FolderName_Label_Description);
    addField(stringEditor);

    final BooleanFieldEditor booleanEditor = new BooleanFieldEditor(SCAN_IN_BACKGROUND.prefKey,
        DerivedResoucesMessages.DerivedResourcesPreferencePage_ScanInBackground_Label,
        BooleanFieldEditor.SEPARATE_LABEL, getFieldEditorParent());
    booleanEditor.getLabelControl(getFieldEditorParent()).setToolTipText(
        DerivedResoucesMessages.DerivedResourcesPreferencePage_ScanInBackground_Label_Description);
    addField(booleanEditor);
  }

  @Override
  public void init(final IWorkbench theWorkbench) {
    // nothing needed at the moment
  }

  // TODO check for valid foldername with DerivedPredicates.folderName()
  @Override
  public boolean performOk() {
    boolean canContinue = true;

    canContinue &= super.performOk();

    if (SCAN_IN_BACKGROUND.getValue()) {
      final Iterable<String> folderNames = PreferenceConverter.fromString(FOLDER_NAMES.getValue(), ";");
      SearchDerivedFolderJob.schedule(folderNames);
    }

    return canContinue;
  }

}
