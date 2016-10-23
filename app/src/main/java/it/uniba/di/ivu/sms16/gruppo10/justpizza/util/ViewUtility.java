package it.uniba.di.ivu.sms16.gruppo10.justpizza.util;

import android.app.Activity;
import android.view.View;

/**
 * This is a utility method to avoid cast when referencing a View by its id
 */
public class ViewUtility {

  /*
   * Private constructor
   */
  private ViewUtility() {
    throw new AssertionError("Never instantiate me!! I'm an utility class!!!");
  }

  /**
   * This method returns the reference of the View with the given Id in the layout of the
   * Activity passed as parameter
   *
   * @param act    The Activity that is using the layout with the given View
   * @param viewId The id of the View we want to get a reference
   * @return The View with the given id and type
   */
  public static <T extends View> T findViewById(Activity act, int viewId) {
    // We get the VuewGroup of the Activity
    View containerView = act.getWindow().getDecorView();
    return findViewById(containerView, viewId);
  }

  /**
   * This method returns the reference of the View with the given Id in the view passed
   * as parameter
   *
   * @param containerView The container View
   * @param viewId        The id of the View we want to get a reference
   * @return The View with the given id and type
   */
  @SuppressWarnings("unchecked")
  public static <T extends View> T findViewById(View containerView, int viewId) {
    // We find the view with the given Id
    View foundView = containerView.findViewById(viewId);
    // We return the View with the given cast
    return (T) foundView;
  }

}
