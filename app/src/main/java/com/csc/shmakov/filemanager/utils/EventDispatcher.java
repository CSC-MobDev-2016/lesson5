package com.csc.shmakov.filemanager.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventDispatcher<E extends Event>{
	private List<Observer<E>> observers = new CopyOnWriteArrayList<Observer<E>>();

	public void addObserver(Observer<E> observer){if(!observers.contains(observer)) observers.add(observer);}
	public void removeObserver(Observer<E> observer){observers.remove(observer);}
	
	public void dispatchEvent(E event){
		for(Observer<E> observer:observers){
			observer.onEvent(event);
		}
	}
}