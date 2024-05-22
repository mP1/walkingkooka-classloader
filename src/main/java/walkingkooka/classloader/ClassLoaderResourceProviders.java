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

import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

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
     * {@see JarFileClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider jarFile(final JarFile file) {
        return JarFileClassLoaderResourceProvider.with(file);
    }

    /**
     * {@see FakeClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider fake() {
        return new FakeClassLoaderResourceProvider();
    }

    /**
     * {@see MapClassLoaderResourceProvider}
     */
    public static ClassLoaderResourceProvider map(final Map<ClassLoaderResourcePath, ClassLoaderResource> pathToResource) {
        return MapClassLoaderResourceProvider.with(pathToResource);
    }

    /**
     * Stop creation
     */
    private ClassLoaderResourceProviders() {
        throw new UnsupportedOperationException();
    }
}
