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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.PublicStaticHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public final class ClassLoaderResourceProviders implements PublicStaticHelper {

    /**
     * {@see CascadingClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider cascading(final List<ClassLoaderResourceProvider> providers) {
        return CascadingClassLoaderResourceProvider.with(providers);
    }

    /**
     * {@see ClassLoaderResourceProviderClassLoader}
     */
    public static ClassLoader classLoader(final ClassLoader parent,
                                          final ClassLoaderResourceProvider provider) {
        return ClassLoaderResourceProviderClassLoader.with(
                parent,
                provider
        );
    }

    /**
     * {@see FakeClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider fake() {
        return new FakeClassLoaderResourceProvider();
    }

    /**
     * {@see JarFileClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider jarFile(final JarFile file) {
        return JarFileClassLoaderResourceProvider.with(file);
    }

    /**
     * Supports reading a JAR file including support for searching a lib directory.
     * This is intended to support JAR file archive with required libraries in a single archive.
     * Note libs ordering is dependent on the order archives appear in the JAR file.
     */
    public static ClassLoaderResourceProvider jarFileWithLibs(final JarInputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");

        final List<ClassLoaderResourceProvider> libs = Lists.array();
        final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource = Maps.sorted();
        final byte[] buffer = new byte[1000];

        for (; ; ) {
            final JarEntry entry = inputStream.getNextJarEntry();
            if (null == entry) {
                break;
            }
            if (entry.isDirectory()) {
                continue;
            }

            final String name = entry.getName();
            final ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();

            for (; ; ) {
                final int count = inputStream.read(buffer);
                if (count < 0) {
                    break;
                }
                bytesOutputStream.write(buffer, 0, count);
            }
            final byte[] bytes = bytesOutputStream.toByteArray();

            if (name.startsWith("libs/")) {
                try (final JarInputStream libJarInputStream = new JarInputStream(new ByteArrayInputStream(bytes))) {
                    libs.add(
                            jarFileWithLibs(libJarInputStream)
                    );
                }
            } else {
                pathToResource.put(
                        ClassLoaderResourcePath.parse("/" + name),
                        ClassLoaderResource.with(
                                Binary.with(bytes)
                        )
                );
            }
        }

        final List<ClassLoaderResourceProvider> all = Lists.array();
        all.add(
                map(pathToResource)
        );
        all.addAll(libs);

        return cascading(all);
    }

    /**
     * {@see MapClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider map(final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource) {
        return MapClassLoaderResourceProvider.with(pathToResource);
    }

    /**
     * {@see UrlClassLoaderClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider urlClassLoader(final URLClassLoader urlClassLoader) {
        return UrlClassLoaderClassLoaderResourceProvider.with(urlClassLoader);
    }

    /**
     * Stop creation
     */
    private ClassLoaderResourceProviders() {
        throw new UnsupportedOperationException();
    }
}
