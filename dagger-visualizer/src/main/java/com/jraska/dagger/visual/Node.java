package com.jraska.dagger.visual;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class Node {
  public final String name;

  private final Set<Node> dependencies = new HashSet<>();

  static Node create(String name) {
    Preconditions.notNull(name);
    return new Node(name);
  }

  private Node(String name) {
    this.name = name;
  }

  boolean addDependency(Node node) {
    return dependencies.add(node);
  }

  public Set<Node> dependencies() {
    return Collections.unmodifiableSet(dependencies);
  }
}
