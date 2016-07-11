package com.jraska.dagger.visual;

import com.github.javaparser.ParseException;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.Optional;

import static com.jraska.dagger.visual.InjectAnnotationVisitorTest.visitSrc;
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

  static final String MODULE_INTO_SET_SRC = "@Module\n" +
          "class IntoSetModule{\n" +
          "  @Provides @IntoSet\n" +
          "  File provideFile(String name){\n" +
          "    return new File(name);\n" +
          "  }\n" +
          "  @Provides\n" +
          "  @IntoSet\n" +
          "  File provideSecondFileFile(Object name){\n" +
          "    return new File(name.toString);\n" +
          "  }\n" +
          "\n" +
          "  @Provides\n" +
          "  OkHttpClient provideClient(Set<File> values){\n" +
          "    return create(values);\n" +
          "  }\n" +
          "}";


  @Test
  public void whenVisitsModule_thenFindsAnnotatedMethod() throws ParseException {
    processTestModule(MODULE_SRC);
  }

  @Test
  public void whenVisitsNestedModule_thenFindsAnnotatedMethod() throws ParseException {
    processTestModule(NESTED_MODULE_SRC);
  }

  @Test
  public void whenIntoSetProivde_thenDependencyAsSet() {
    DependencyGraph.Builder graphBuilder = DependencyGraph.builder();
    ModuleAnnotationVisitor annotationVisitor = ModuleAnnotationVisitor.create(graphBuilder);

    visitSrc(MODULE_INTO_SET_SRC, annotationVisitor);

    DependencyGraph graph = graphBuilder.build();
    assertThat(graph.nodes()).hasSize(4);
    assertThat(graph.nodeOrThrow("Set<File>").dependencies())
            .containsExactlyInAnyOrder(graph.nodeOrThrow("Object"), graph.nodeOrThrow("String"));
    assertThat(graph.nodeOrThrow("OkHttpClient").dependencies())
            .containsExactly(graph.nodeOrThrow("Set<File>"));
  }

  @SneakyThrows
  static void processTestModule(String src) {
    DependencyGraph.Builder graphBuilder = DependencyGraph.builder();
    ModuleAnnotationVisitor annotationVisitor = ModuleAnnotationVisitor.create(graphBuilder);

    visitSrc(src, annotationVisitor);

    DependencyGraph graph = graphBuilder.build();
    assertTestGraph(graph);
  }

  private static void assertTestGraph(DependencyGraph graph) {
    Node fileNode = graph.nodeOrThrow("File");
    Node stringNode = graph.nodeOrThrow("String");
    assertThat(fileNode.dependencies()).containsExactly(stringNode);
    assertThat(stringNode.dependencies()).isEmpty();
  }
}