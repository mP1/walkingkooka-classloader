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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.enumeration.EnumerationTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.test.Testing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface ClassLoaderTesting<T extends ClassLoader> extends Testing,
        EnumerationTesting {

    // getResource......................................................................................................

    @Test
    default void testGetResourceWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createClassLoader()
                        .getResource(null)
        );
    }

    @Test
    default void testGetResourceWithUnknownResource() throws IOException {
        this.getResourceAndCheck(
                this.createClassLoader(),
                "/unknown-404.resource",
                null
        );
    }

    // public URL getResource(String name)
    default void getResourceAndCheck(final String resourceName,
                                     final byte... expected) throws IOException {
        this.getResourceAndCheck(
                this.createClassLoader(),
                resourceName,
                expected
        );
    }

    default void getResourceAndCheck(final ClassLoader classLoader,
                                     final String resourceName,
                                     final byte... expected) throws IOException {
        final URL url = classLoader.getResource(resourceName);

        byte[] actual = null;

        if (null != url) {
            final URLConnection connection = url.openConnection();
            connection.connect();

            final InputStream inputStream = connection.getInputStream();

            actual = inputStream.readAllBytes();

            inputStream.close();
        }

        this.checkEquals(
                expected,
                actual,
                "getResource " + resourceName + " inputStream bytes"
        );
    }

    // getResources.....................................................................................................

    @Test
    default void testGetResourcesWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createClassLoader()
                        .getResources(null)
        );
    }

    @Test
    default void testGetResourcesWithUnknownResourceFails() throws IOException {
        this.getResourcesAndCheck(
                this.createClassLoader(),
                "/unknown-404.resource"
        );
    }

    // public Enumeration<URL> getResources(String name)
    default void getResourcesAndCheck(final ClassLoader classLoader,
                                      final String resourceName,
                                      final byte... expected) throws IOException {
        this.getResourcesAndCheck(
                classLoader,
                resourceName,
                IntStream.range(0, expected.length)
                        .mapToObj(i -> expected[i])
                        .collect(Collectors.toList())
        );
    }

    default void getResourcesAndCheck(final String resourceName,
                                      final List<Byte> expected) throws IOException {
        this.getResourcesAndCheck(
                this.createClassLoader(),
                resourceName,
                expected
        );
    }

    default void getResourcesAndCheck(final ClassLoader classLoader,
                                      final String resourceName,
                                      final List<Byte> expected) throws IOException {
        final Enumeration<URL> urls = classLoader.getResources(resourceName);
        this.checkNotEquals(
                null,
                urls,
                () -> "getResources " + resourceName + " url"
        );

        final List<byte[]> resourceBytes = Lists.array();

        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();

            final URLConnection connection = url.openConnection();
            connection.connect();

            final InputStream inputStream = connection.getInputStream();
            resourceBytes.add(
                    inputStream.readAllBytes()
            );

            inputStream.close();
        }

        this.checkEquals(
                expected,
                resourceBytes,
                "getResources " + resourceName + " inputStream bytes"
        );
    }

    // getResourceAsStream..............................................................................................

    @Test
    default void testGetResourceAsStreamWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createClassLoader()
                        .getResourceAsStream(null)
        );
    }

    @Test
    default void testGetResourceAsStreamWithUnknownResource() {
        this.checkEquals(
                null,
                this.createClassLoader()
                        .getResourceAsStream("/unknown-404.resource")
        );
    }

    // public InputStream getResourceAsStream(String name)
    default void getResourceAsStreamAndCheck(final String resourceName,
                                             final byte[] expected) throws IOException {
        this.getResourceAsStreamAndCheck(
                this.createClassLoader(),
                resourceName,
                expected
        );
    }

    default void getResourceAsStreamAndCheck(final ClassLoader classLoader,
                                             final String resourceName,
                                             final byte[] expected) throws IOException {
        final InputStream inputStream = classLoader.getResourceAsStream(resourceName);

        this.checkEquals(
                expected,
                null != inputStream ?
                inputStream.readAllBytes() :
                null,
                "getResourceAsStream " + resourceName + " inputStream bytes"
        );

        if(null!= inputStream) {
            inputStream.close();
        }
    }

    // loadClass........................................................................................................

    @Test
    default void testLoadClassWithNullFails() {
        this.loadClassAndFail(
                this.createClassLoader(),
                null,
                NullPointerException.class
        );
    }

    default String loadClassAndFail(final ClassLoader loader,
                                    final String className,
                                    final Class<? extends Throwable> thrownType) {
        final Throwable thrown = assertThrows(
                thrownType,
                () -> loader.loadClass(className)
        );
        return thrown.getMessage();
    }

    default void loadClassAndFail(final ClassLoader loader,
                                  final String className,
                                  final Class<? extends Throwable> thrownType,
                                  final String message) {
        final String actual = loadClassAndFail(
                loader,
                className,
                thrownType
        );
        this.checkEquals(
                message,
                actual,
                () -> "load " + className
        );
    }

    // factory..........................................................................................................

    T createClassLoader();
}
