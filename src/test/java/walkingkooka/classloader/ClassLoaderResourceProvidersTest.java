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
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ClassLoaderResourceProvidersTest implements PublicStaticHelperTesting<ClassLoaderResourceProviders>,
        ClassLoaderResourceProviderTesting {

    // jarFileWithLibs..................................................................................................

    @Test
    public void testJarFileWithLibsWithNullInputStreamFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResourceProviders.jarFileWithLibs(null)
        );
    }

    @Test
    public void testJarFileWithLibsWithNoLibs() throws IOException {
        final byte[] resource1 = new byte[]{
                '1',
                '1',
                '1'
        };

        final byte[] resource2 = new byte[]{
                '2',
                '2',
                '2'
        };

        final byte[] jar = createJar(
                Maps.of(
                        "test/test-resource111.txt",
                        resource1,
                        "test/test-resource222.txt",
                        resource2
                )
        );

        final ClassLoaderResourceProvider provider = ClassLoaderResourceProviders.jarFileWithLibs(
                new JarInputStream(
                        new ByteArrayInputStream(jar)
                )
        );

        this.loadAndCheck(
                provider,
                ClassLoaderResourcePath.parse("/test/test-resource111.txt"),
                ClassLoaderResource.with(
                        Binary.with(
                                resource1
                        )
                )
        );

        this.loadAndCheck(
                provider,
                ClassLoaderResourcePath.parse("/test/test-resource222.txt"),
                ClassLoaderResource.with(
                        Binary.with(
                                resource2
                        )
                )
        );
    }


    @Test
    public void testJarFileWithLibsWithLibs() throws IOException {
        final byte[] resource1 = new byte[]{
                '1',
                '1',
                '1'
        };

        final byte[] resource2 = new byte[]{
                '2',
                '2',
                '2'
        };

        final byte[] libs = createJar(
                Maps.of(
                        "test/test-resource111.txt",
                        resource1,
                        "test/test-resource222.txt",
                        "ignored".getBytes(StandardCharsets.UTF_8)
                )
        );

        final byte[] jar = createJar(
                Maps.of(
                        "test/test-resource222.txt",
                        resource2,
                        "libs/test.jar",
                        libs
                )
        );


        final ClassLoaderResourceProvider provider = ClassLoaderResourceProviders.jarFileWithLibs(
                new JarInputStream(
                        new ByteArrayInputStream(jar)
                )
        );

        this.loadAndCheck(
                provider,
                ClassLoaderResourcePath.parse("/test/test-resource111.txt"),
                ClassLoaderResource.with(
                        Binary.with(
                                resource1
                        )
                )
        );

        this.loadAndCheck(
                provider,
                ClassLoaderResourcePath.parse("/test/test-resource222.txt"),
                ClassLoaderResource.with(
                        Binary.with(
                                resource2
                        )
                )
        );
    }

    private static byte[] createJar(final Map<String, byte[]> contents) throws IOException {
        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            final JarOutputStream jarOut = new JarOutputStream(bytes);

            for (final Map.Entry<String, byte[]> mapEntry : contents.entrySet()) {
                final JarEntry jarEntry = new JarEntry(mapEntry.getKey());

                final byte[] resource = mapEntry.getValue();
                jarEntry.setSize(resource.length);
                jarOut.putNextEntry(jarEntry);
                jarOut.write(resource);
                jarOut.closeEntry();

            }

            jarOut.flush();
            jarOut.finish();
            jarOut.close();

            return bytes.toByteArray();
        }
    }

    // ClassFile........................................................................................................

    @Override
    public Class<ClassLoaderResourceProviders> type() {
        return ClassLoaderResourceProviders.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
