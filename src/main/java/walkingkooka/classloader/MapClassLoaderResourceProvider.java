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

import walkingkooka.collect.map.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ClassLoaderResourceProvider} that uses the given path as a key to the provided {@link Map}.
 */
final class MapClassLoaderResourceProvider implements ClassLoaderResourceProvider {

    static MapClassLoaderResourceProvider with(final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource) {
        Objects.requireNonNull(pathToResource, "pathToResource");

        return new MapClassLoaderResourceProvider(
                Maps.immutable(pathToResource)
        );
    }

    private MapClassLoaderResourceProvider(final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource) {
        this.pathToResource = pathToResource;
    }

    @Override
    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
        return Optional.ofNullable(
                this.pathToResource.get(path)
        );
    }

    private final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource;

    @Override
    public String toString() {
        return this.pathToResource.toString();
    }
}
