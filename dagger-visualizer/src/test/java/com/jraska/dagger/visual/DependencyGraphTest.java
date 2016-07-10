package com.jraska.dagger.visual;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyGraphTest {
  @Test
  public void whenDependenciesCreated_thenCorrectlyLinked() {
    DependencyGraph dependencyGraph = DependencyGraph.builder()
            .addDependency("A", "B")
            .addDependency("B", "Lazy<C>")
            .addDependency("C", "Provider<D>")
            .build();

    assertThat(dependencyGraph.nodes()).hasSize(4);
    assertSingleDependency(dependencyGraph, "A", "B");
    assertSingleDependency(dependencyGraph, "B", "C");
    assertSingleDependency(dependencyGraph, "C", "D");
  }

  @Test
  public void whenOneDependencyAddedManyTimes_thenContainsEachNodeOnce() {
    DependencyGraph dependencyGraph = DependencyGraph.builder()
            .addDependency("C", "A")
            .addDependency("A", "C")
            .addDependency("A", "C")
            .build();

    ArrayList<Node> nodes = new ArrayList<>(dependencyGraph.nodes());
    assertThat(nodes).hasSize(2);
    assertThat(nodes.get(0).dependencies()).containsExactly(nodes.get(1));
    assertThat(nodes.get(1).dependencies()).containsExactly(nodes.get(0));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent") // assertions
  static void assertSingleDependency(DependencyGraph dependencyGraph, String from, String to){
    Optional<Node> fromNode = dependencyGraph.node(from);
    assertThat(fromNode).isPresent();

    Optional<Node> toNode = dependencyGraph.node(to);
    assertThat(toNode).isPresent();

    assertThat(fromNode.get().dependencies()).containsExactly(toNode.get());
  }
}