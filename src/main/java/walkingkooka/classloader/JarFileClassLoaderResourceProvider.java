package walkingkooka.classloader;

import walkingkooka.Binary;
import walkingkooka.collect.iterable.Iterables;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.text.LineEnding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    static JarFileClassLoaderResourceProvider with(final JarFile file,
                                                   final LineEnding lineEnding) {
        return new JarFileClassLoaderResourceProvider(
                Objects.requireNonNull(file, "file"),
                Objects.requireNonNull(lineEnding, "lineEnding")
        );
    }

    /**
     * Private constructor use factory
     */
    private JarFileClassLoaderResourceProvider(final JarFile file,
                                               final LineEnding lineEnding) {
        super();
        this.file = file;
        this.lineEnding = lineEnding;
    }

    @Override
    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
        Objects.requireNonNull(path, "path");

        try {
            ClassLoaderResource resource = null;

            // drop the leading slash from path#value
            final JarEntry entry = this.file.getJarEntry(
                    path.value()
                            .substring(1)
            );
            if (null != entry) {
                if (entry.isDirectory()) {
                    resource = listing(path);
                } else {
                    resource = resource(entry);
                }
            }

            return Optional.ofNullable(resource);
        } catch (final IOException cause) {
            throw new ClassFormatError("Error reading " + path + " from jar file, " + cause.getMessage());
        }
    }

    /**
     * Loops over all JAR entries and builds a text file with child files in the parent path.
     */
    private ClassLoaderResource listing(final ClassLoaderResourcePath parent) throws IOException {
        final JarFile file = this.file;

        final StringBuilder listing = new StringBuilder();
        final LineEnding lineEnding = this.lineEnding;

        for (final JarEntry entry : Iterables.iterator(Iterators.enumeration(file.entries()))) {
            final String name = entry.getName();
            try {
                final ClassLoaderResourcePath entryPath = ClassLoaderResourcePath.parse(
                        ClassLoaderResourcePath.SEPARATOR.string() + name
                );
                if (parent.equals(
                        entryPath.parent()
                                .orElse(null)
                )) {
                    listing.append(
                            entryPath.name()
                                    .value()
                    ).append(lineEnding);
                }
            } catch (final Exception ignore) {
                // ignore entry must have bad filename
            }
        }

        return ClassLoaderResource.with(
                Binary.with(
                        listing.toString()
                                .getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private final LineEnding lineEnding;

    private ClassLoaderResource resource(final JarEntry entry) throws IOException {
        return ClassLoaderResource.with(
                Binary.with(
                        file.getInputStream(entry)
                                .readAllBytes()
                )
        );
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
