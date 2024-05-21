package walkingkooka.classloader;

import walkingkooka.reflect.ClassName;

import java.util.Optional;

/**
 * A provider that tries to locate any given {@link ClassName}. At runtime there will potentially be several of these
 * constructed in order to support a class loading preference.
 */
public interface ClassLoaderResourceProvider {

    /**
     * Returns the class for the given {@link ClassName}.
     */
    Optional<ClassLoaderResource> classFile(final ClassName name);
}
