package com.clinica.service;
public class Errors {
  public static class NotFound extends RuntimeException { public NotFound(String m){ super(m);} }
  public static class BusinessRule extends RuntimeException { public BusinessRule(String m){ super(m);} }
  public static class Conflict extends RuntimeException { public Conflict(String m){ super(m);} }
}
