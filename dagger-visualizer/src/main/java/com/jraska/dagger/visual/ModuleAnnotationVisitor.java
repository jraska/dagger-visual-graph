package com.jraska.dagger.visual;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Stack;

import static com.jraska.dagger.visual.InjectAnnotationVisitor.hasAnnotation;

final class ModuleAnnotationVisitor extends VoidVisitorAdapter<Object> {
  private final Stack<Boolean> isInModuleStack = new Stack<>();
  private final DependencyGraph.Builder builder;

  public static ModuleAnnotationVisitor create(DependencyGraph.Builder builder) {
    Preconditions.notNull(builder);
    return new ModuleAnnotationVisitor(builder);
  }

  private ModuleAnnotationVisitor(DependencyGraph.Builder builder) {
    this.builder = builder;
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration declaration, Object arg) {
    isInModuleStack.push(isDaggerModule(declaration));
    super.visit(declaration, arg);
    isInModuleStack.pop();
  }

  @Override
  public void visit(MethodDeclaration method, Object arg) {
    if (isInModuleStack.isEmpty() || !isInModuleStack.peek()) {
      return;
    }

    if (!hasProvideAnnotation(method)) {
      return;
    }

    String type = method.getType().toString();
    if (hasIntoSetAnnotation(method)) {
      type = "Set<" + type + ">";
    }

    for (Parameter parameter : method.getParameters()) {
      builder.addDependency(type, parameter.getType().toString());
    }
  }

  private boolean hasProvideAnnotation(BodyDeclaration declaration) {
    return hasAnnotation(declaration, "Provides");
  }

  static boolean isDaggerModule(ClassOrInterfaceDeclaration declaration) {
    return hasAnnotation(declaration, "Module");
  }

  static boolean hasIntoSetAnnotation(BodyDeclaration declaration) {
    return hasAnnotation(declaration, "IntoSet");
  }
}