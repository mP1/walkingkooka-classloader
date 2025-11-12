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
import walkingkooka.reflect.ClassName;
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Path} that wraps a {@link String} which may contain any character.
 * Note that the path to the manifest.mf is special-cased and not case sensitive during comparisons/equality checks.
 */
final public class ClassLoaderResourcePath
        implements Path<ClassLoaderResourcePath, ClassLoaderResourceName>,
        Comparable<ClassLoaderResourcePath> {

    /**
     * {@link PathSeparator} instance
     */
    public final static PathSeparator SEPARATOR = PathSeparator.requiredAtStart('/');

    private final static String MANIFEST_STRING = "/META-INF/MANIFEST.MF";

    public final static ClassLoaderResourcePath MANIFEST = new ClassLoaderResourcePath(
            MANIFEST_STRING,
            ClassLoaderResourceName.with("MANIFEST.MF"),
            Optional.of(
                    new ClassLoaderResourcePath(
                            "/META-INF",
                            ClassLoaderResourceName.with("META-INF"),
                            Optional.empty()
                    )
            )
    );

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

        final ClassLoaderResourcePath classLoaderResourcePath;

        switch (path) {
            case "/":
                classLoaderResourcePath = ROOT;
                break;
            case MANIFEST_STRING:
                classLoaderResourcePath = MANIFEST;
                break;
            default:
                classLoaderResourcePath = parseNonManifest(path);
                break;
        }

        return classLoaderResourcePath;
    }

    private static ClassLoaderResourcePath parseNonManifest(final String path) {
        boolean dontWrap = false;

        try {
            ClassLoaderResourcePath result = ROOT;

            if (path.length() > 1) {
                for (final String component : path.substring(1).split(SEPARATOR.string())) {
                    switch(component) {
                        case CURRENT:
                            break;
                        case PARENT:
                            dontWrap = true;
                            result = result.parent()
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid path " + CharSequences.quoteAndEscape(path)));
                            dontWrap = false;
                            break;
                        default:
                            result = result.append(
                                    ClassLoaderResourceName.with(component)
                            );
                            break;
                    }
                }
            }
            return result;
        } catch (final IllegalArgumentException cause) {
            if(dontWrap) {
                throw cause;
            }
            throw new IllegalArgumentException("Failed to parse " + CharSequences.quote(path) + ", message: " + cause.getMessage(), cause);
        }
    }

    /**
     * Creates a {@link ClassLoaderResourcePath} for the given {@link ClassName}.
     */
    public static ClassLoaderResourcePath from(final ClassName name) {
        Objects.requireNonNull(name, "name");

        return parse(
                SEPARATOR.string() +
                        name.value()
                                .replace('.', SEPARATOR.character()) +
                        ".class"
        );
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

    // value............................................................................................................

    @Override
    public String value() {
        return this.path;
    }

    private final String path;

    // append...........................................................................................................

    @Override
    public ClassLoaderResourcePath append(final ClassLoaderResourceName name) {
        Objects.requireNonNull(name, "name");

        final ClassLoaderResourcePath appended;

        switch (name.value()) {
            case "/":
                appended = this;
                break;
            default:
                appended = this.appendNonRootName(name);
                break;
        }

        return appended;
    }

    private ClassLoaderResourcePath appendNonRootName(final ClassLoaderResourceName name) {
        final StringBuilder path = new StringBuilder();
        path.append(this.path);
        if (false == this.isRoot()) {
            path.append(SEPARATOR);
        }
        path.append(name.value());

        return new ClassLoaderResourcePath(
                path.toString(), // path
                name,
                Optional.of(this) // parent
        );
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
    public int compareTo(final ClassLoaderResourcePath other) {
        Objects.requireNonNull(other, "other");

        return ClassLoaderResourceName.CASE_SENSITIVITY.comparator()
                .compare(
                        this.path,
                        other.path
                );
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
        return this.path.equalsIgnoreCase(MANIFEST_STRING) ?
                this.path.equalsIgnoreCase(other.path) :
                ClassLoaderResourceName.CASE_SENSITIVITY.equals(this.path, other.path);
    }

    @Override
    public String toString() {
        return this.path;
    }
}
