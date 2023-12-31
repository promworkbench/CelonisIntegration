package org.processmining.celonisintegration.algorithms;

public class AccessException extends Exception{
	public String message;

    public AccessException(String message){
    	super(message);
        this.message = message;
    }
    public AccessException(String message, Throwable cause) {
        super (message, cause);
    }
    public String toString() { 
        return "Access error: " + this.message;
    } 
    

}
