package com.jraska.dagger.visual;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class FilesIterable {
  private FilesIterable() {
  }

  @SneakyThrows
  public static Iterable<Path> allJavaFiles(Path root) {
    final List<Path> files = new ArrayList<>();
    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!attrs.isDirectory() && file.toFile().getName().endsWith(".java")) {
          files.add(file);
        }
        return FileVisitResult.CONTINUE;
      }
    });

    return files;
  }
}