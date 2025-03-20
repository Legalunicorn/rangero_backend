package com.hiroc.rangero.exception;

public class UnauthorisedException extends RuntimeException {
  public UnauthorisedException(String message) {
    super(message);
  }

  public UnauthorisedException(){
    super("You do not have permission to perform this action.");
  }
}
