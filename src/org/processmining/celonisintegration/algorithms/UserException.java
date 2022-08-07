package org.processmining.celonisintegration.algorithms;

public class UserException extends Exception{
	public String message;

    public UserException(String message){
    	super(message);
        this.message = message;
    }
    public UserException(String message, Throwable cause) {
        super (message, cause);
    }
    public String toString() { 
        return "User error: " + this.message;
    } 
    

}
