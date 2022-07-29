package org.processmining.celonisintegration.algorithms;

public class AccessException extends Exception{
	public String message;

    public AccessException(String message){
    	super(message);
        this.message = message;
    }

}
