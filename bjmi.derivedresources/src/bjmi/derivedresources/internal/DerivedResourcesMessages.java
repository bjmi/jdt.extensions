package bjmi.derivedresources.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Eclipse messages for l10n.
 *
 * <strong>Messages key declaration must NOT contain final.</strong>
 *
 * @author Björn Michael
 * @since 1.0
 */
@SuppressWarnings("javadoc")
public final class DerivedResourcesMessages {

  public static String MarkDerivedAction_Error_Folder;

  /**
   * @since 1.4
   */
  public static String MarkDerivedAction_Error_AtLeastOneFolder;

  public static String MarkDerivedAction_Error_Message;

  public static String MarkDerivedAction_Error_Title;

  public static String MarkDerivedAction_Input_Description;

  public static String MarkDerivedAction_Input_Title;

  public static String MarkDerivedAction_Result_Message;

  public static String MarkDerivedAction_Result_Title;

  public static String MarkDerivedJob_Search;

  /**
   * @since TODO
   */
  public static String MarkDerivedJob_Cancel;

  /**
   * @since TODO
   */
  public static String MarkDerivedJob_Search_Complete;

  public static String MarkDerivedJob_Job_Name;

  public static String DerivedResourcesPreferencePage_Description;

  public static String DerivedResourcesPreferencePage_FolderName_Label;

  /**
   * @since 1.4
   */
  public static String DerivedResourcesPreferencePage_FolderName_Label_Description;

  public static String DerivedResourcesPreferencePage_ScanInBackground_Label;

  /**
   * @since 1.4
   */
  public static String DerivedResourcesPreferencePage_ScanInBackground_Label_Description;

  // name of properties file without file extension
  private static final String RESOURCE_BUNDLE_NAME = DerivedResourcesMessages.class.getName();

  static {
    NLS.initializeMessages(RESOURCE_BUNDLE_NAME, DerivedResourcesMessages.class);
  }

  private DerivedResourcesMessages() {
    // prevent instantiation
    throw new AssertionError("invocation of sealed constructor");
  }

}
