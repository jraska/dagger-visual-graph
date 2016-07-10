package com.jraska.dagger.visual;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectAnnotationVisitorTest {
  static final String CONSTRUCTOR_INJECT_SRC = "class Injected {\n" +
          "  private final File file;\n" +
          "  private final String string;\n" +
          "  class Nested {\n" +
          "  }" +
          "  \n" +
          "  @Inject\n" +
          "  Injected(File file, String string){\n" +
          "    this.file = file;\n" +
          "    this.string = string;\n" +
          "  }\n" +
          "}";

  static final String NESTED_CONSTRUCTOR_INJECT_SRC = "class WrappingClass {\n" +
          "    int x;\n" +
          "\n" +
          "    void method(){}\n" +
          "    \n" +
          "    int getter(){return x;}\n" +
          CONSTRUCTOR_INJECT_SRC +
          "  }";

  static final String FIELD_INJECT_SRC = "class Injected {\n" +
          "  @Inject @Beta String string;\n" +
          "  @Inject\n" +
          "  File file;\n" +
          "}";

  static final String NESTED_FIELD_INJECT_SRC = "class WrappingClass {\n" +
          "    int x;\n" +
          "\n" +
          "    void method(){}\n" +
          "    \n" +
          "    int getter(){return x;}\n" +
          "class EmptyNestedModule{\n" +
          "}" +
          FIELD_INJECT_SRC +
          "  }";


  @Test
  public void whenInjectingFields_thenValidGraph() {
    processTestInjected(FIELD_INJECT_SRC);
  }

  @Test
  public void whenInjectingNestedFields_thenValidGraph() {
    processTestInjected(NESTED_FIELD_INJECT_SRC);
  }

  @Test
  public void whenInjectingConstructor_thenValidGraph() {
    processTestInjected(CONSTRUCTOR_INJECT_SRC);
  }

  @Test
  public void whenInjectingNestedConstructor_thenValidGraph() {
    processTestInjected(NESTED_CONSTRUCTOR_INJECT_SRC);
  }

  @SneakyThrows
  private void processTestInjected(String src) {
    DependencyGraph.Builder graphBuilder = DependencyGraph.builder();
    InjectAnnotationVisitor annotationVisitor = InjectAnnotationVisitor.create(graphBuilder);

    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src.getBytes());
    CompilationUnit compilationUnit = JavaParser.parse(byteArrayInputStream);

    annotationVisitor.visit(compilationUnit, null);

    DependencyGraph graph = graphBuilder.build();
    assertTestGraph(graph);
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private static void assertTestGraph(DependencyGraph graph) {
    Optional<Node> injectNode = graph.node("Injected");
    assertThat(injectNode).isPresent();

    Optional<Node> stringNode = graph.node("String");
    assertThat(stringNode).isPresent();
    Optional<Node> fileNode = graph.node("File");
    assertThat(fileNode).isPresent();
    assertThat(injectNode.get().dependencies()).containsExactlyInAnyOrder(stringNode.get(), fileNode.get());
  }
}



