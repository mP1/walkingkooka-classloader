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

import walkingkooka.reflect.PublicStaticHelper;

public final class ClassLoaderResourceProviders implements PublicStaticHelper {

    /**
     * {@see ClassLoaderResourceProviderClassLoader}
     */
    public static ClassLoader with(final ClassLoader parent,
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
     * Stop creation
     */
    private ClassLoaderResourceProviders() {
        throw new UnsupportedOperationException();
    }
}
