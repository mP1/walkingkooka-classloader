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

import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ClassLoaderResourceProvider} that tries each of the given {@link ClassLoaderResourceProvider} one by one
 * until success. This has one major limitation the {@link ClassLoader#getResources(String)} will only ever return the first
 * and not all resources with the same name.
 * <br>
 * This will be particularly useful when assembling a {@link ClassLoaderResourceProvider} that supports a JAR file
 * with a lib directory, with all resources including the contents of the lib dir being searched.
 */
final class CascadingClassLoaderResourceProvider implements ClassLoaderResourceProvider {

    static ClassLoaderResourceProvider with(final List<ClassLoaderResourceProvider> providers) {
        Objects.requireNonNull(providers, "providers");

        final List<ClassLoaderResourceProvider> copy = Lists.immutable(providers);
        final ClassLoaderResourceProvider result;

        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Empty ClassLoaderResourceProvider");
            case 1:
                result = copy.get(0);
                break;
            default:
                result = new CascadingClassLoaderResourceProvider(
                        copy
                );
        }

        return result;
    }

    private CascadingClassLoaderResourceProvider(final List<ClassLoaderResourceProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
        Optional<ClassLoaderResource> resource = Optional.empty();

        for (final ClassLoaderResourceProvider provider : this.providers) {
            resource = provider.load(path);
            if (resource.isPresent()) {
                break;
            }
        }

        return resource;
    }

    private final List<ClassLoaderResourceProvider> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
