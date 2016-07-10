package com.jraska.dagger.visual;

import java.io.PrintStream;
import java.util.Collection;

final class PrintStreamGraphPrinter {
  private final PrintStream printStream;

  public static PrintStreamGraphPrinter create(PrintStream printStream){
    return new PrintStreamGraphPrinter(printStream);
  }

  private PrintStreamGraphPrinter(PrintStream printStream) {
    Preconditions.notNull(printStream);

    this.printStream = printStream;
  }

  public void print(DependencyGraph dependencyGraph){
    Collection<Node> nodes = dependencyGraph.nodes();

    for (Node node : nodes) {
      for (Node dependency : node.dependencies()) {
        printStream.printf("%s,%s", node.name, dependency.name)
                .println();
      }
    }
  }
}
