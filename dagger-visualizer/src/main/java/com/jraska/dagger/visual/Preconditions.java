package com.jraska.dagger.visual;

public final class Preconditions {
  private Preconditions() {
  }

  public static void notNull(Object parameter) {
    if (parameter == null) {
      throw new IllegalArgumentException("parameter cannot be null");
    }
  }
}
