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
import walkingkooka.text.LineEnding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ClassLoaderResourceProvidersTest implements PublicStaticHelperTesting<ClassLoaderResourceProviders>,
        ClassLoaderResourceProviderTesting {

    // jarFileWithLibs..................................................................................................

    private final static LineEnding EOL = LineEnding.NL;

    @Test
    public void testJarFileWithLibsWithNullInputStreamFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResourceProviders.jarFileWithLibs(
                        null,
                        EOL
                )
        );
    }

    @Test
    public void testJarFileWithLibsWithNullLineEndingFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResourceProviders.jarFileWithLibs(
                        new JarInputStream(
                                new ByteArrayInputStream(
                                        new byte[0]
                                )
                        ),
                        null
                )
        );
    }

    @Test
    public void testJarFileWithManifest() throws IOException {
        // Manifest will always end with empty line
        final String manifest = "Manifest-Version: 1.0\r\nKey1: Value1\r\n\r\n";

        final byte[] jar = createJar(
                manifest,
                Maps.empty()
        );

        final ClassLoaderResourceProvider provider = ClassLoaderResourceProviders.jarFileWithLibs(
                new JarInputStream(
                        new ByteArrayInputStream(jar)
                ),
                EOL
        );

        this.loadAndCheck(
                provider,
                ClassLoaderResourcePath.MANIFEST,
                ClassLoaderResource.with(
                        Binary.with(
                                manifest.getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }

    @Test
    public void testJarFileWithManifestDifferentCase() throws IOException {
        // Manifest will always end with empty line
        final String manifest = "Manifest-Version: 1.0\r\nKey1: Value1\r\n\r\n";

        final byte[] jar = createJar(
                manifest,
                Maps.empty()
        );

        final ClassLoaderResourceProvider provider = ClassLoaderResourceProviders.jarFileWithLibs(
                new JarInputStream(
                        new ByteArrayInputStream(jar)
                ),
                EOL
        );

        this.loadAndCheck(
                provider,
                ClassLoaderResourcePath.parse("/meta-inf/manifest.MF"),
                ClassLoaderResource.with(
                        Binary.with(
                                manifest.getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }

    @Test
    public void testJarFileWithLibsWithNoLibs() throws IOException {
        final String manifest = "Manifest-Version: 1.0\r\nKey1: Value1\r\n";

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
                manifest,
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
                ),
                EOL
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
                "Manifest-Version: 1.0",
                Maps.of(
                        "test/test-resource111.txt",
                        resource1,
                        "test/test-resource222.txt",
                        "ignored".getBytes(StandardCharsets.UTF_8)
                )
        );

        final byte[] jar = createJar(
                "Manifest-Version: 1.0",
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
                ),
                EOL
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

    private static byte[] createJar(final String manifest,
                                    final Map<String, byte[]> contents) throws IOException {
        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            final Manifest manifest1 = new Manifest();
            manifest1.read(
                    new ByteArrayInputStream(
                            manifest.getBytes(Charset.defaultCharset())
                    )
            );

            final JarOutputStream jarOut = new JarOutputStream(
                    bytes,
                    manifest1
            );

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
