package com.jraska.dagger.visual;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Stack;

final class InjectAnnotationVisitor extends VoidVisitorAdapter<Object> {
  private static final String INJECT = "Inject";
  private final Stack<String> classNameStack = new Stack<>();
  private final DependencyGraph.Builder builder;

  public static InjectAnnotationVisitor create(DependencyGraph.Builder builder){
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
    for (AnnotationExpr annotationExpr : field.getAnnotations()) {
      if (INJECT.equals(annotationExpr.getName().getName())) {
        builder.addDependency(classNameStack.peek(), field.getType().toString());
      }
    }
  }

  @Override
  public void visit(ConstructorDeclaration n, Object arg) {
    for (AnnotationExpr annotationExpr : n.getAnnotations()) {
      if (INJECT.equals(annotationExpr.getName().getName())) {
        for (Parameter parameter : n.getParameters()) {
          builder.addDependency(classNameStack.peek(), parameter.getType().toString());
        }
      }
    }
  }
}
