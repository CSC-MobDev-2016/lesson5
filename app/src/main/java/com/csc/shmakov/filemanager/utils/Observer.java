
package com.csc.shmakov.filemanager.utils;


public interface Observer<E extends Event>{
    void onEvent(E event);
}
