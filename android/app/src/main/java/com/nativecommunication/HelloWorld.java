package com.nativecommunication;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by lyt9304 on 2018/7/27.
 */

public class HelloWorld extends ReactContextBaseJavaModule {
    public HelloWorld(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "HelloWorld";
    }

    /*
    方法导出，参数可包含(java -> javascript)
     Boolean -> Bool
     Integer -> Number
     Double -> Number
     Float -> Number
     String -> String
     Callback -> function
     ReadableMap -> Object
     ReadableArray -> Array
     */
    @ReactMethod
    public void greeting(String name, Callback finishCallback) {
        Log.i("lyttest", name + ", Hello World!");
        finishCallback.invoke(name);
    }

    @ReactMethod
    public void asyncGreeting(String name, Promise promise) {
        Log.i("lyttest", name + ", Hello World!");
        promise.resolve(name);
    }

    @ReactMethod
    public void eventGreeting(String name) {
        Log.i("lyttest", name + ", Hello World!");
        WritableMap params = Arguments.createMap();
        params.putString("name", name);
        sendEvent(getReactApplicationContext(), "lytNativeEvent", params);
    }

    // 发送事件给JS
    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    // 常量导出
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("MODULE_OWNER_NAME", "lyt");
        constants.put("MODULE_OWNER_AGE", "18");
        return constants;
    }
}
