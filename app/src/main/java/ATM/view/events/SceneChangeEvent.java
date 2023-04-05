package ATM.view.events;

import ATM.view.windows.Windows;

/**
 * A SceneChangeEvent is used to indicate that a new Window should be shown in the GUI. The Templated Window type
 * will be used to indicate which Window controller to change to. Can be triggered by any interface when a button is
 * pushed etc. The window controller must be registered with the window manager using the associated Window enum type
 * so that the manager knows which controller to load upon receiving the callback.
 *
 * Default behaviour is to simply change the window without any intermediate behaviour. To custom
 * behaviour, an EventHandler should be registered with a SceneChangeEvent(Window) (NOTE: the user is now responsible
 * for changing the window frame if necessary).
 *
 * SceneChangeEvent will work for ChangeWindows or PopupWindows as the opening of closing windows is defined
 * within the controller class.
 */
public class SceneChangeEvent extends Event<Windows> {

    /**
     * Makes a new SceneChangeEvent.
     * @param windows The Window type to change to. This window type must have an associated controller that
     *                is registered with the WindowManager.
     */
    public SceneChangeEvent(Windows windows) {
        super(windows);
    }
}
