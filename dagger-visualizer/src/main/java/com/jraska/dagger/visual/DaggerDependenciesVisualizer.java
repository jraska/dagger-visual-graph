package com.jraska.dagger.visual;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class DaggerDependenciesVisualizer {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Exactly one parameter for root path expected. Provided parameters: " + Arrays.toString(args));
      return;
    }

    Path rootPath = Paths.get(args[0]);
    if (!rootPath.toFile().exists()) {
      System.out.printf("Path %s does not exist.", rootPath)
              .println();
      return;
    }

    PrintStreamGraphPrinter printer = PrintStreamGraphPrinter.create(System.out);
    DependenciesExtractor extractor = DependenciesExtractor.create();
    DaggerDependenciesVisualizer visualizer = new DaggerDependenciesVisualizer(extractor, printer);

    visualizer.visualize(rootPath);
  }

  private final DependenciesExtractor dependenciesExtractor;
  private final PrintStreamGraphPrinter printer;

  public DaggerDependenciesVisualizer(DependenciesExtractor dependenciesExtractor, PrintStreamGraphPrinter printer) {
    Preconditions.notNull(dependenciesExtractor);
    Preconditions.notNull(printer);

    this.dependenciesExtractor = dependenciesExtractor;
    this.printer = printer;
  }

  public void visualize(Path path) {
    DependencyGraph graph = dependenciesExtractor.extract(path);
    printer.print(graph);
  }
}
