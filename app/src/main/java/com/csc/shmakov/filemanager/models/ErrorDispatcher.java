package com.csc.shmakov.filemanager.models;

import com.csc.shmakov.filemanager.utils.Event;
import com.csc.shmakov.filemanager.utils.EventDispatcher;

/**
 * Created by Pavel on 1/1/2016.
 */
public class ErrorDispatcher extends EventDispatcher<ErrorDispatcher.ErrorEvent> {
    public static final ErrorDispatcher INSTANCE = new ErrorDispatcher();
    private ErrorDispatcher() {}


    public void dispatchError(ErrorType type, Object... params) {
        dispatchEvent(new ErrorEvent(type, params));
    }

    public static class ErrorEvent extends Event {
        public final ErrorType type;
        public final Object[] params;

        public ErrorEvent(ErrorType type, Object[] params) {
            this.type = type;
            this.params = params;
        }
    }
}
