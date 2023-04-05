package ATM.view.windows;

import ATM.view.controllers.AtmSceneControllerBase;
import ATM.view.events.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;

public class WindowsManager {

    //string or enum?
    private final HashMap<Windows, WindowType> sceneLoaderMap;
    private final Stage primaryStage;

    private WindowType currentWindow;

    public WindowsManager(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.sceneLoaderMap = new HashMap<>();
    }

    public void addWindow(Windows window, URL url,
                          AtmSceneControllerBase controllerBase,
                          Class<? extends WindowType> windowsType)  {
        //TODO: check that window doesnt already exist
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(url);
            loader.setController(controllerBase);

            loader.load();
            controllerBase.setLoader(loader);
            Constructor<?> ctr = windowsType.getConstructor(Stage.class, AtmSceneControllerBase.class, Windows.class);
            Object type = ctr.newInstance(this.primaryStage, controllerBase, window);
            System.out.println(type);
            this.sceneLoaderMap.put(window, (WindowType)type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void registerController(EventManager callbackInterface) {
        for(WindowType type : sceneLoaderMap.values()) {
            type.getController().attachCallbackInterface(callbackInterface);
        }
    }

    public WindowType getWindow(Windows window) {
        if (this.sceneLoaderMap.containsKey(window)) {
            return this.sceneLoaderMap.get(window);
        }
        return null;
    }

    public void setCurrentWindow(WindowType windowType) {
        currentWindow = windowType;
    }

    public void changeWindow(WindowType windowType) {
        currentWindow.close();
        currentWindow = windowType;
        windowType.open();
    }

    public WindowType getCurrentWindow() {
        return this.currentWindow;
    }


}
