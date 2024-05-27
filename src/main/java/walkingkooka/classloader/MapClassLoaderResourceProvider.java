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
import walkingkooka.collect.map.Maps;
import walkingkooka.text.LineEnding;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link ClassLoaderResourceProvider} that uses the given path as a key to the provided {@link Map}.
 */
final class MapClassLoaderResourceProvider implements ClassLoaderResourceProvider {

    static MapClassLoaderResourceProvider with(final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource,
                                               final LineEnding lineEnding) {
        Objects.requireNonNull(pathToResource, "pathToResource");
        Objects.requireNonNull(lineEnding, "lineEnding");

        return new MapClassLoaderResourceProvider(
                Maps.immutable(pathToResource),
                lineEnding
        );
    }

    private MapClassLoaderResourceProvider(final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource,
                                           final LineEnding lineEnding) {
        this.pathToResource = pathToResource;
        this.lineEnding = lineEnding;
    }

    @Override
    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
        final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource = this.pathToResource;

        ClassLoaderResource resource = pathToResource.get(path);
        if (null == resource) {
            final String lineEnding = this.lineEnding.toString();

            final String listing = pathToResource.keySet()
                    .stream()
                    .filter(e -> path.equals(
                            e.parent()
                                    .orElse(null)
                    )).map(e -> e.name().value())
                    .collect(
                            Collectors.joining(lineEnding)
                    );
            if (false == listing.isEmpty()) {
                resource = ClassLoaderResource.with(
                        Binary.with(
                                listing.concat(lineEnding)
                                        .getBytes(StandardCharsets.UTF_8)
                        )
                );
            }
        }

        return Optional.ofNullable(resource);
    }

    private final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource;

    private final LineEnding lineEnding;
}
