package org.ajikhoji.passwordmanager.service;

import java.nio.file.Path;
import java.util.List;

public interface ImportService<T> {
    List<T> importFrom(Path source);
}
