package org.ajikhoji.passwordmanager.service;

import java.nio.file.Path;
import java.util.Collection;

public interface ExportService<T> {

    void export(Collection<T> data, Path destination);

}
