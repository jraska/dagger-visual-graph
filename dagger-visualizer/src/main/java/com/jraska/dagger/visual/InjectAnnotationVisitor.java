package com.jraska.dagger.visual;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Stack;

final class InjectAnnotationVisitor extends VoidVisitorAdapter<Object> {
  private final Stack<String> classNameStack = new Stack<>();
  private final DependencyGraph.Builder builder;

  public static InjectAnnotationVisitor create(DependencyGraph.Builder builder) {
    Preconditions.notNull(builder);

    return new InjectAnnotationVisitor(builder);
  }

  private InjectAnnotationVisitor(DependencyGraph.Builder builder) {
    this.builder = builder;
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, Object arg) {
    classNameStack.push(n.getName());
    super.visit(n, arg);
    classNameStack.pop();
  }

  @Override
  public void visit(FieldDeclaration field, Object arg) {
    if (hasInjectAnnotation(field)) {
      builder.addDependency(classNameStack.peek(), field.getType().toString());
    }
  }

  @Override
  public void visit(ConstructorDeclaration constructor, Object arg) {
    if (hasInjectAnnotation(constructor)) {
      for (Parameter parameter : constructor.getParameters()) {
        builder.addDependency(classNameStack.peek(), parameter.getType().toString());
      }
    }
  }

  static boolean hasInjectAnnotation(BodyDeclaration bodyDeclaration) {
    return hasAnnotation(bodyDeclaration, "Inject");
  }

  static boolean hasAnnotation(BodyDeclaration bodyDeclaration, String annotation) {
    return bodyDeclaration.getAnnotations()
            .stream()
            .anyMatch((a) -> annotation.equals(a.getName().getName()));
  }
}
