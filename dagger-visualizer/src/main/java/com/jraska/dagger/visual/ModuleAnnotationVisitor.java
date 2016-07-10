package com.jraska.dagger.visual;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.Stack;

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
  public void visit(MethodDeclaration n, Object arg) {
    if (isInModuleStack.isEmpty() || !isInModuleStack.peek()) {
      return;
    }

    if (!hasProvideAnnotation(n)) {
      return;
    }

    String type = n.getType().toString();

    for (Parameter parameter : n.getParameters()) {
      builder.addDependency(type, parameter.getType().toString());
    }
  }

  private boolean hasProvideAnnotation(MethodDeclaration n) {
    for (AnnotationExpr annotationExpr : n.getAnnotations()) {
      if ("Provides".equals(annotationExpr.getName().getName())) {
        return true;
      }
    }

    return false;
  }

  private boolean isDaggerModule(ClassOrInterfaceDeclaration declaration) {
    List<AnnotationExpr> annotations = declaration.getAnnotations();
    for (AnnotationExpr annotation : annotations) {
      if ("Module".equals(annotation.getName().getName())) {
        return true;
      }
    }

    return false;
  }
}