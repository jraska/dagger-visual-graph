package com.jraska.dagger.visual;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeTest {
  @Test
  public void whenAddDependency_thenDependencyReturned() {
    Node node = Node.create("testNode");
    Node dependencyNode = Node.create("dependencyNode");

    node.addDependency(dependencyNode);

    assertThat(node.dependencies()).containsExactly(dependencyNode);
  }

  @Test
  public void whenAddSameDependencyManyTimes_thenOnlyOneDependencyReturned() {
    Node node = Node.create("testNode");
    Node dependencyNode = Node.create("dependencyNode");

    for (int i = 0; i < 10; i++) {
      node.addDependency(dependencyNode);
    }

    assertThat(node.dependencies()).containsExactly(dependencyNode);
  }
}