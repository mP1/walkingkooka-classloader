/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.Binary;
import walkingkooka.Value;

import java.util.Objects;

/**
 * A resource loaded by a {@link ClassLoaderResourceProvider}.
 */
public final class ClassLoaderResource implements Value<Binary> {

    /**
     * Creates a new {@link ClassLoaderResource}
     */
    public static ClassLoaderResource with(final Binary value) {
        return new ClassLoaderResource(
                Objects.requireNonNull(
                        value,
                        "value"
                )
        );
    }

    private ClassLoaderResource(final Binary value) {
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public Binary value() {
        return this.value;
    }

    private final Binary value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof ClassLoaderResource && this.equals0((ClassLoaderResource) other);
    }

    private boolean equals0(final ClassLoaderResource other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
