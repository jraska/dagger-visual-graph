package com.jraska.dagger.visual;

import java.util.*;

final class DependencyGraph {
  private final Map<String, Node> nodes;

  private DependencyGraph(Map<String, Node> nodes) {
    this.nodes = nodes;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Collection<Node> nodes() {
    return nodes.values();
  }

  public Optional<Node> node(String name) {
    return Optional.ofNullable(nodes.get(name));
  }

  public Node nodeOrThrow(String name) {
    Node node = nodes.get(name);
    if (node == null) {
      throw new IllegalArgumentException("Node '" + name + "' not found");
    }

    return node;
  }

  public static class Builder {
    private final Map<String, Node> nodes = new HashMap<>();

    Builder addDependency(String className, String dependencyType) {
      Node node = getNode(className);

      String pureClassName = extractClassName(dependencyType);
      Node dependencyNode = getNode(pureClassName);

      node.addDependency(dependencyNode);
      return this;
    }

    private String extractClassName(String dependencyName) {
      if (dependencyName.startsWith("Lazy<")) {
        return dependencyName.substring("Lazy<".length(), dependencyName.length() - 1);
      }

      if (dependencyName.startsWith("Provider<")) {
        return dependencyName.substring("Provider<".length(), dependencyName.length() - 1);
      }

      return dependencyName;
    }

    private Node getNode(String className) {
      Node node = nodes.get(className);
      if (node == null) {
        node = Node.create(className);
        nodes.put(className, node);
      }
      return node;
    }

    public DependencyGraph build() {
      return new DependencyGraph(Collections.unmodifiableMap(nodes));
    }
  }
}
