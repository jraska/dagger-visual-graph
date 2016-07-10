package com.jraska.dagger.visual;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import lombok.SneakyThrows;

import java.nio.file.Path;

final class DependenciesExtractor {
  public static DependenciesExtractor create() {
    return new DependenciesExtractor();
  }

  private DependenciesExtractor() {
  }

  @SneakyThrows
  public DependencyGraph extract(Path rootPath) {
    DependencyGraph.Builder dependencyGraphBuilder = DependencyGraph.builder();
    InjectAnnotationVisitor injectVisitor = InjectAnnotationVisitor.create(dependencyGraphBuilder);
    ModuleAnnotationVisitor moduleAnnotationVisitor = ModuleAnnotationVisitor.create(dependencyGraphBuilder);

    Iterable<Path> javaFiles = FilesIterable.allJavaFiles(rootPath);
    for (Path path : javaFiles) {
      CompilationUnit compilationUnit = JavaParser.parse(path.toFile());
      injectVisitor.visit(compilationUnit, null);
      moduleAnnotationVisitor.visit(compilationUnit, null);
    }

    return dependencyGraphBuilder.build();
  }
}
