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
import walkingkooka.Binary;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

public final class UrlClassLoaderClassLoaderResourceProviderTest implements ClassLoaderResourceProviderTesting,
        ClassTesting<UrlClassLoaderClassLoaderResourceProvider>,
        ToStringTesting<UrlClassLoaderClassLoaderResourceProvider> {

    @Test
    public void testLoadResource() throws Exception {
        this.loadAndCheck(
                UrlClassLoaderClassLoaderResourceProvider.with(
                        URLClassLoader.newInstance(
                                new URL[]{
                                        new File("./test/resources/JarFileClassLoaderResourceProviderTest.jar").toURL()
                                }
                        )
                ),
                ClassLoaderResourcePath.parse("/walkingkooka/classloader/test-resource-123.txt"),
                ClassLoaderResource.with(
                        Binary.with(
                                new byte[]{
                                        '1',
                                        '2',
                                        '3'
                                }
                        )
                )
        );
    }

    @Test
    public void testLoadDirectoryListing() throws Exception {
        this.loadAndCheck(
                UrlClassLoaderClassLoaderResourceProvider.with(
                        URLClassLoader.newInstance(
                                new URL[]{
                                        new File("./test/resources/JarFileClassLoaderResourceProviderTest.jar").toURL()
                                }
                        )
                ),
                ClassLoaderResourcePath.parse("/walkingkooka/classloader"),
                ClassLoaderResource.with(
                        Binary.with(
                                (
                                        "CascadingClassLoaderResourceProviderTest.class\n" +
                                                "ClassLoaderResourceNameTest.class\n" +
                                                "ClassLoaderResourcePathTest.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$1.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$10.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$2.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$3.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$4.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$5.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$6.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$7.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$8.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderTest$9.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderUrlConnectionTest.class\n" +
                                                "ClassLoaderResourceProviderClassLoaderUrlStreamHandlerTest.class\n" +
                                                "ClassLoaderResourceProvidersTest.class\n" +
                                                "ClassLoaderResourceTest.class\n" +
                                                "JarFileClassLoaderResourceProviderTest.class\n" +
                                                "MapClassLoaderResourceProviderTest.class\n" +
                                                "TestClass.class\n" +
                                                "TestInterface.class\n" +
                                                "test-resource-123.txt\n" +
                                                "UrlClassLoaderClassLoaderResourceProviderTest.class\n"
                                ).getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }

    @Test
    public void testToString() throws Exception {
        final URL url = new File("./test/resources/JarFileClassLoaderResourceProviderTest.jar")
                .toURL();

        this.toStringAndCheck(
                UrlClassLoaderClassLoaderResourceProvider.with(
                        URLClassLoader.newInstance(
                                new URL[]{
                                        url
                                }
                        )
                ),
                url.toString()
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<UrlClassLoaderClassLoaderResourceProvider> type() {
        return UrlClassLoaderClassLoaderResourceProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
