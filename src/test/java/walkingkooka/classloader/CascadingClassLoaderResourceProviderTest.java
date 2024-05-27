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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.LineEnding;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CascadingClassLoaderResourceProviderTest implements ClassLoaderResourceProviderTesting,
        ClassTesting<CascadingClassLoaderResourceProvider> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> CascadingClassLoaderResourceProvider.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CascadingClassLoaderResourceProvider.with(Lists.empty())
        );
    }

    @Test
    public void testWithOne() {
        final ClassLoaderResourceProvider provider = ClassLoaderResourceProviders.fake();

        assertSame(
                provider,
                CascadingClassLoaderResourceProvider.with(
                        Lists.of(
                                provider
                        )
                )
        );
    }

    @Test
    public void testLoad() {
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse("/resource2.txt");
        final ClassLoaderResource resource = ClassLoaderResource.with(
                Binary.with(
                        new byte[]{
                                '1',
                                '2',
                                '3'
                        }
                )
        );

        this.loadAndCheck(
                CascadingClassLoaderResourceProvider.with(
                        Lists.of(
                                ClassLoaderResourceProviders.map(
                                        Maps.empty(),
                                        LineEnding.NL
                                ),
                                ClassLoaderResourceProviders.map(
                                        Maps.of(
                                                path,
                                                resource
                                        ),
                                        LineEnding.NL
                                ),
                                ClassLoaderResourceProviders.fake()
                        )
                ),
                path,
                resource
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<CascadingClassLoaderResourceProvider> type() {
        return CascadingClassLoaderResourceProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
