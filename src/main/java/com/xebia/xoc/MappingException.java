package com.xebia.xoc;

public class MappingException extends Exception {

  public MappingException() {
    super();
  }

  public MappingException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public MappingException(String msg, Object... params) {
    super(String.format(msg, params));
  }
  
  public MappingException(String msg) {
    super(msg);
  }

  public MappingException(Throwable cause) {
    super(cause);
  }

}
