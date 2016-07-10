package com.jraska.dagger.visual;

import org.junit.Test;

public class PreconditionsTest {
  @Test
  public void whenNonNullParameterPassed_thenNothingHappens() {
    Preconditions.notNull(new Object());
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenNullParameterPassed_thenIllegalArgumentException() {
    Preconditions.notNull(null);
  }
}