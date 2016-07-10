package com.jraska.dagger.visual;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ModuleAnnotationVisitorTest {
  static final String MODULE_SRC = "@Module\n" +
          "class TestModule{\n" +
          "  class EmptyNestedClass{\n" +
          "  }" +
          "  @Provides\n" +
          "  File provideFile(String name){\n" +
          "    return new File(name);\n" +
          "  }\n" +
          "\n" +
          "  String notAnnotatedMethod(Object value){\n" +
          "    return value.toString();\n" +
          "  }\n" +
          "}";

  static final String NESTED_MODULE_SRC = "class WrappingClass {\n" +
          "    int x;\n" +
          "\n" +
          "    void method(){}\n" +
          "    \n" +
          "    int getter(){return x;}\n" +
          "class EmptyNestedModule{\n" +
          "}" +
          MODULE_SRC +
          "  }";


  @Test
  public void whenVisitsModule_thenFindsAnnotatedMethod() throws ParseException {
    processTestModule(MODULE_SRC);
  }

  @Test
  public void whenVisitsNestedModule_thenFindsAnnotatedMethod() throws ParseException {
    processTestModule(NESTED_MODULE_SRC);
  }

  @SneakyThrows
  static void processTestModule(String src) {
    DependencyGraph.Builder graphBuilder = DependencyGraph.builder();
    ModuleAnnotationVisitor annotationVisitor = ModuleAnnotationVisitor.create(graphBuilder);

    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src.getBytes());
    CompilationUnit compilationUnit = JavaParser.parse(byteArrayInputStream);

    annotationVisitor.visit(compilationUnit, null);

    DependencyGraph graph = graphBuilder.build();
    assertTestGraph(graph);
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private static void assertTestGraph(DependencyGraph graph) {
    Optional<Node> fileNode = graph.node("File");
    assertThat(fileNode).isPresent();

    Optional<Node> stringNode = graph.node("String");
    assertThat(stringNode).isPresent();
    assertThat(fileNode.get().dependencies()).containsExactly(stringNode.get());
    assertThat(stringNode.get().dependencies()).isEmpty();
  }
}