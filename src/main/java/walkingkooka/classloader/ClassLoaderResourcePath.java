/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.classloader;

import walkingkooka.naming.Path;
import walkingkooka.naming.PathSeparator;
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Path} that wraps a {@link String} which may contain any character.
 */
final public class ClassLoaderResourcePath
        implements Path<ClassLoaderResourcePath, ClassLoaderResourceName>,
        Comparable<ClassLoaderResourcePath> {

    /**
     * {@link PathSeparator} instance
     */
    public final static PathSeparator SEPARATOR = PathSeparator.requiredAtStart('/');

    final static ClassLoaderResourceName ROOT_NAME = ClassLoaderResourceName.with(
            ClassLoaderResourcePath.SEPARATOR.string()
    );

    /**
     * Convenient constant holding the root.
     */
    public final static ClassLoaderResourcePath ROOT = new ClassLoaderResourcePath(
            ClassLoaderResourcePath.SEPARATOR.string(),
            ClassLoaderResourcePath.ROOT_NAME,
            Optional.empty()
    );

    /**
     * Parses the {@link String} into a {@link ClassLoaderResourcePath}
     */
    public static ClassLoaderResourcePath parse(final String path) {
        SEPARATOR.checkBeginning(path);

        try {
            ClassLoaderResourcePath result = ROOT;

            if (path.length() > 1) {
                for (String component : path.substring(1).split(SEPARATOR.string())) {
                    result = result.append(ClassLoaderResourceName.with(component));
                }
            }
            return result;
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException("Failed to parse " + CharSequences.quote(path) + ", message: " + cause.getMessage(), cause);
        }
    }

    /**
     * Private constructor
     */
    private ClassLoaderResourcePath(final String path,
                                    final ClassLoaderResourceName name,
                                    final Optional<ClassLoaderResourcePath> parent) {
        super();
        this.path = path;
        this.name = name;
        this.parent = parent;
    }

    private final String path;

    // Path

    @Override
    public ClassLoaderResourcePath append(final ClassLoaderResourceName name) {
        Objects.requireNonNull(name, "name");

        if (ClassLoaderResourcePath.ROOT_NAME.equals(name)) {
            throw new IllegalArgumentException(ClassLoaderResourcePath.CANNOT_APPEND_ROOT_NAME);
        }

        final StringBuilder path = new StringBuilder();
        if (false == this.isRoot()) {
            path.append(this.path);
        }
        path.append(ClassLoaderResourcePath.SEPARATOR.character());
        path.append(name.value());

        return new ClassLoaderResourcePath(
                path.toString(),
                name,
                Optional.of(this)
        );
    }

    /**
     * Thrown when attempting to add the root name to this {@link ClassLoaderResourcePath}.
     */
    private final static String CANNOT_APPEND_ROOT_NAME = "Cannot append root name.";

    @Override
    public String value() {
        return this.path;
    }

    private final Optional<ClassLoaderResourcePath> parent;

    /**
     * Returns the parent {@link ClassLoaderResourcePath}.
     */
    @Override
    public Optional<ClassLoaderResourcePath> parent() {
        return this.parent;
    }

    private final ClassLoaderResourceName name;

    @Override
    public ClassLoaderResourceName name() {
        return this.name;
    }

    /**
     * {@link PathSeparator} getter.
     */
    @Override
    public PathSeparator separator() {
        return ClassLoaderResourcePath.SEPARATOR;
    }

    /**
     * Only returns true if this {@link ClassLoaderResourcePath} is the {@link #ROOT}.
     */
    @Override
    public boolean isRoot() {
        return this == ClassLoaderResourcePath.ROOT;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final ClassLoaderResourcePath path) {
        Objects.requireNonNull(path, "path");
        return this.path.compareTo(path.path);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return (this == other) ||
                ((other instanceof ClassLoaderResourcePath) && this.equals0((ClassLoaderResourcePath) other));
    }

    private boolean equals0(final ClassLoaderResourcePath other) {
        return this.path.equals(other.path);
    }

    @Override
    public String toString() {
        return this.path;
    }
}
