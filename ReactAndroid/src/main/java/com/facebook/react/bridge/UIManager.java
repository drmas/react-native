/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.bridge;

import static com.facebook.infer.annotation.ThreadConfined.UI;

import android.view.View;
import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import com.facebook.infer.annotation.ThreadConfined;
import java.util.List;

public interface UIManager extends JSIModule, PerformanceCounter {

  /** Registers a new root view. @Deprecated call startSurface instead */
  @UiThread
  @ThreadConfined(UI)
  @Deprecated
  <T extends View> int addRootView(
      final T rootView, WritableMap initialProps, @Nullable String initialUITemplate);

  /** Registers a new root view with width and height. */
  @AnyThread
  <T extends View> int startSurface(
      final T rootView,
      final String moduleName,
      final WritableMap initialProps,
      int widthMeasureSpec,
      int heightMeasureSpec);

  /**
   * Stop a surface from running in JS and clears up native memory usage. Assumes that the native
   * View hierarchy has already been cleaned up. Fabric-only.
   */
  @AnyThread
  void stopSurface(final int surfaceId);

  /**
   * Updates the layout specs of the RootShadowNode based on the Measure specs received by
   * parameters. offsetX and offsetY are the position of the RootView within the screen.
   */
  @UiThread
  @ThreadConfined(UI)
  void updateRootLayoutSpecs(
      int rootTag, int widthMeasureSpec, int heightMeasureSpec, int offsetX, int offsetY);

  /**
   * Dispatches the commandId received by parameter to the view associated with the reactTag. The
   * command will be processed in the UIThread.
   *
   * <p>Receiving commands as ints is deprecated and will be removed in a future release.
   *
   * <p>Pre-Fabric, this is only called on the Native Module Thread.
   *
   * @param reactTag {@link int} that identifies the view that will receive this command
   * @param commandId {@link int} command id
   * @param commandArgs {@link ReadableArray} parameters associated with the command
   */
  void dispatchCommand(int reactTag, int commandId, @Nullable ReadableArray commandArgs);

  /**
   * Dispatches the commandId received by parameter to the view associated with the reactTag. The
   * command will be processed in the UIThread.
   *
   * <p>Pre-Fabric, this is only called on the Native Module Thread.
   *
   * @param reactTag {@link int} that identifies the view that will receive this command
   * @param commandId {@link String} command id
   * @param commandArgs {@link ReadableArray} parameters associated with the command
   */
  void dispatchCommand(int reactTag, String commandId, @Nullable ReadableArray commandArgs);

  /** @return the {@link EventDispatcher} object that is used by this class. */
  <T> T getEventDispatcher();

  /**
   * Used by native animated module to bypass the process of updating the values through the shadow
   * view hierarchy. This method will directly update native views, which means that updates for
   * layout-related propertied won't be handled properly. Make sure you know what you're doing
   * before calling this method :)
   *
   * @param reactTag {@link int} that identifies the view that will be updated
   * @param props {@link ReadableMap} props that should be immediately updated in view
   */
  @UiThread
  @ThreadConfined(UI)
  void synchronouslyUpdateViewOnUIThread(int reactTag, ReadableMap props);

  /**
   * Dispatch an accessibility event to a view asynchronously.
   *
   * <p>Pre-Fabric, this is only called on the Native Module Thread.
   *
   * @param reactTag
   * @param eventType
   */
  void sendAccessibilityEvent(int reactTag, int eventType);

  /**
   * Register a {@link UIManagerListener} with this UIManager to receive lifecycle callbacks.
   *
   * @param listener
   */
  void addUIManagerEventListener(UIManagerListener listener);

  /**
   * Unregister a {@link UIManagerListener} from this UIManager to stop receiving lifecycle
   * callbacks.
   *
   * @param listener
   */
  void removeUIManagerEventListener(UIManagerListener listener);

  /**
   * This method dispatches events from RN Android code to JS. The delivery of this event will not
   * be queued in EventDispatcher class.
   *
   * @param reactTag tag
   * @param eventName name of the event
   * @param event parameters
   */
  @Deprecated
  void receiveEvent(int reactTag, String eventName, @Nullable WritableMap event);

  /**
   * This method dispatches events from RN Android code to JS. The delivery of this event will not
   * be queued in EventDispatcher class.
   *
   * @param surfaceId
   * @param reactTag tag
   * @param eventName name of the event
   * @param event parameters
   */
  void receiveEvent(int surfaceId, int reactTag, String eventName, @Nullable WritableMap event);

  /** Resolves Direct Event name exposed to JS from the one known to the Native side. */
  @Deprecated
  @Nullable
  String resolveCustomDirectEventName(@Nullable String eventName);

  /**
   * Helper method to pre-initialize view managers. When using Native ViewConfigs this method will
   * also pre-compute the constants for a view manager. The purpose is to ensure that we don't block
   * for getting the constants for view managers during initial rendering of a surface.
   *
   * @deprecated this method will be removed in the future
   * @param viewManagerNames {@link List <String>} names of ViewManagers
   */
  @Deprecated
  void preInitializeViewManagers(List<String> viewManagerNames);
}
