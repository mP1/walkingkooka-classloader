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

import walkingkooka.Cast;
import walkingkooka.io.FileExtension;
import walkingkooka.naming.Name;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Optional;

/**
 * The name portion of a class path resource path.
 */
public final class ClassLoaderResourceName implements Name,
        Comparable<ClassLoaderResourceName> {

    /**
     * Resource names are case-sensitive.
     */
    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    /**
     * Factory that creates a new {@link ClassLoaderResourceName}
     */
    public static ClassLoaderResourceName with(final String name) {
        return new ClassLoaderResourceName(
                CharSequences.failIfNullOrEmpty(name, "name")
        );
    }

    private ClassLoaderResourceName(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Returns the file extension without the '.' if one was present.
     */
    public Optional<FileExtension> fileExtension() {
        if (null == this.fileExtension) {
            this.fileExtension = FileExtension.extract(this.name);
        }
        return this.fileExtension;
    }

    /**
     * A cached copy of the extracted {@link FileExtension}.
     */
    transient Optional<FileExtension> fileExtension;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof ClassLoaderResourceName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final ClassLoaderResourceName other) {
        return CASE_SENSITIVITY.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ......................................................................................................

    @Override
    public int compareTo(final ClassLoaderResourceName other) {
        return CASE_SENSITIVITY.comparator()
                .compare(this.name, other.name);
    }

    // HasCaseSensitivity...............................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }
}
