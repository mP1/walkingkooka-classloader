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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.LineEnding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JarFileClassLoaderResourceProviderTest implements ClassLoaderResourceProviderTesting,
        ClassTesting<JarFileClassLoaderResourceProvider> {

    private final static String TEST_JAR_FILE = "./src/test/resources/JarFileClassLoaderResourceProviderTest.jar";

    private final static LineEnding EOL = LineEnding.NL;

    @Test
    public void testWithNullJarFileFails() {
        assertThrows(
                NullPointerException.class,
                () -> JarFileClassLoaderResourceProvider.with(
                        null,
                        EOL
                )
        );
    }

    @Test
    public void testWithNullLineEndingFails() {
        assertThrows(
                NullPointerException.class,
                () -> JarFileClassLoaderResourceProvider.with(
                        new JarFile(TEST_JAR_FILE),
                        null
                )
        );
    }

    @Test
    public void testLoadUnknown() throws IOException {
        this.loadAndCheck(
                this.classLoaderResourceProvider(),
                ClassLoaderResourcePath.parse("/walkingkooka/classloader/UNKNOWN.txt")
        );
    }

    @Test
    public void testLoadResource() throws IOException {
        this.loadAndCheck(
                this.classLoaderResourceProvider(),
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
    public void testLoadDirectory() throws IOException {
        this.loadAndCheck(
                this.classLoaderResourceProvider(),
                ClassLoaderResourcePath.parse("/walkingkooka/classloader"),
                ClassLoaderResource.with(
                        Binary.with(
                                "test-resource-123.txt\n".getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }

    private JarFileClassLoaderResourceProvider classLoaderResourceProvider() throws IOException {
        return JarFileClassLoaderResourceProvider.with(
                new JarFile(TEST_JAR_FILE),
                EOL
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<JarFileClassLoaderResourceProvider> type() {
        return JarFileClassLoaderResourceProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
