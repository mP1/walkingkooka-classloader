package walkingkooka.classloader;

import walkingkooka.Binary;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A {@link ClassLoaderResourceProvider} that scans a jar file for resources.
 */
final class JarFileClassLoaderResourceProvider implements ClassLoaderResourceProvider {

    /**
     * Creates a {@link JarFileClassLoaderResourceProvider}
     */
    static JarFileClassLoaderResourceProvider with(final JarFile file) {
        return new JarFileClassLoaderResourceProvider(
                Objects.requireNonNull(file, "file")
        );
    }

    /**
     * Private constructor use factory
     */
    private JarFileClassLoaderResourceProvider(final JarFile file) {
        super();
        this.file = file;
    }

    @Override
    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
        Objects.requireNonNull(path, "path");

        final JarFile file = this.file;

        try {
            ClassLoaderResource resource = null;

            // drop the leading slash from path#value
            final JarEntry entry = file.getJarEntry(
                    path.value()
                            .substring(1)
            );
            if ((null != entry) && (false == entry.isDirectory())) {
                resource = ClassLoaderResource.with(
                        Binary.with(
                                file.getInputStream(entry)
                                        .readAllBytes()
                        )
                );
            }

            return Optional.ofNullable(resource);
        } catch (final IOException cause) {
            throw new ClassFormatError("Error reading " + path + " from jar file, " + cause.getMessage());
        }
    }

    /**
     * The jar file
     */
    private final JarFile file;

    // Object...........................................................................................................

    /**
     * Returns the jar file name.
     */
    @Override
    public String toString() {
        return this.file.getName();
    }
}
