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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.LineEnding;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MapClassLoaderResourceProviderTest implements ClassLoaderResourceProviderTesting,
        ClassTesting<MapClassLoaderResourceProvider> {

    private final static LineEnding EOL = LineEnding.NL;

    @Test
    public void testWithNullJarFileFails() {
        assertThrows(
                NullPointerException.class,
                () -> MapClassLoaderResourceProvider.with(
                        null,
                        LineEnding.NL
                )
        );
    }

    @Test
    public void testWithNullLineEndingFails() {
        assertThrows(
                NullPointerException.class,
                () -> MapClassLoaderResourceProvider.with(
                        Maps.empty(),
                        null
                )
        );
    }

    @Test
    public void testLoadUnknown() {
        this.loadAndCheck(
                this.classLoaderResourceProvider(),
                ClassLoaderResourcePath.parse("/walkingkooka/classloader/UNKNOWN.txt")
        );
    }

    @Test
    public void testLoadResource() {
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

    // new String(this.getClass().getClassLoader().getResourceAsStream("walkingkooka/plugin").readAllBytes())
    //
    // ClassLoaderPluginProviderTest.class
    // ClassLoaderPluginProviderTest$1.class
    // ClassLoaderPluginProviderTest$TestPlugin.class
    // ClassLoaderPluginProviderTest$TestPluginImpl.class
    // ClassLoaderPluginProviderTest$TestPluginProvider.class
    // PluginInfoTest.class
    // PluginNameTest.class
    // PluginProviderNameTest.class
    // PluginProvidersTest.class
    // PluginProviderTestingTest.class
    // PluginProviderTestingTest$TestPlugin1.class
    // PluginProviderTestingTest$TestPlugin2.class
    // PluginProviderTestingTest$TestPluginProvider.class
    @Test
    public void testLoadDirectory() {
        this.loadAndCheck(
                this.classLoaderResourceProvider(),
                ClassLoaderResourcePath.parse("/walkingkooka/classloader"),
                ClassLoaderResource.with(
                        Binary.with(
                                (
                                        "test-resource-123.txt" + EOL +
                                                "test-resource-234.txt" + EOL
                                ).getBytes(StandardCharsets.UTF_8)
                        )
                )
        );
    }

    private MapClassLoaderResourceProvider classLoaderResourceProvider() {
        return MapClassLoaderResourceProvider.with(
                Maps.of(
                        ClassLoaderResourcePath.parse("/walkingkooka/classloader/test-resource-123.txt"),
                        ClassLoaderResource.with(
                                Binary.with(
                                        new byte[]{
                                                '1',
                                                '2',
                                                '3'
                                        }
                                )
                        ),
                        ClassLoaderResourcePath.parse("/walkingkooka/classloader/test-resource-234.txt"),
                        ClassLoaderResource.with(
                                Binary.with(
                                        new byte[]{
                                                '2',
                                                '3',
                                                '4'
                                        }
                                )
                        ),
                        ClassLoaderResourcePath.parse("/walkingkooka/classloader/test/test-resource-456.txt"),
                        ClassLoaderResource.with(
                                Binary.with(
                                        new byte[]{
                                                '4',
                                                '5',
                                                '6'
                                        }
                                )
                        )
                ),
                EOL
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<MapClassLoaderResourceProvider> type() {
        return MapClassLoaderResourceProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
