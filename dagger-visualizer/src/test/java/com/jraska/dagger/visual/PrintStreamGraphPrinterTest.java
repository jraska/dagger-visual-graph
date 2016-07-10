package com.jraska.dagger.visual;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PrintStreamGraphPrinterTest {
  @Test
  public void whenGraphPassed_thenPrintsOneLineForEachDependency() {
    DependencyGraph dependencyGraph = DependencyGraph.builder()
            .addDependency("A", "B")
            .addDependency("B", "C")
            .addDependency("A", "C")
            .addDependency("A", "C")
            .build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream testStream = new PrintStream(outputStream);

    PrintStreamGraphPrinter graphPrinter = PrintStreamGraphPrinter.create(testStream);
    graphPrinter.print(dependencyGraph);

    assertThat(outputStream.toString())
            .contains("A,B")
            .contains("B,C")
            .containsOnlyOnce("A,C");
  }
}