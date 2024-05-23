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
import walkingkooka.text.CharSequences;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Uses a {@link java.net.URLClassLoader} to satisfy requests for a resource. Classes are not loaded but their *.class files
 * may be fetched.
 */
final class UrlClassLoaderClassLoaderResourceProvider implements ClassLoaderResourceProvider {

    static UrlClassLoaderClassLoaderResourceProvider with(final URLClassLoader urlClassLoader) {
        Objects.requireNonNull(urlClassLoader, "urlClassLoader");

        return new UrlClassLoaderClassLoaderResourceProvider(urlClassLoader);
    }

    private UrlClassLoaderClassLoaderResourceProvider(final URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }

    @Override
    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
        // ClassLoader.getResourceAsStream dont need leading slash
        final String classPathPath = path.value()
                .substring(1);

        final InputStream inputStream = this.urlClassLoader.getResourceAsStream(classPathPath);
        return Optional.ofNullable(
                null != inputStream ?
                        this.classLoaderResource(
                                inputStream,
                                classPathPath
                        ) :
                        null
        );
    }

    private ClassLoaderResource classLoaderResource(final InputStream inputStream,
                                                    final String path) {
        try {
            return ClassLoaderResource.with(
                    Binary.with(
                            inputStream.readAllBytes()
                    )
            );
        } catch (final IOException cause) {
            throw new IllegalStateException("Unable to load resource " + CharSequences.quoteAndEscape(path), cause);
        }
    }

    private final URLClassLoader urlClassLoader;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return Arrays.stream(this.urlClassLoader.getURLs())
                .map(URL::toString)
                .collect(Collectors.joining(","));
    }
}
