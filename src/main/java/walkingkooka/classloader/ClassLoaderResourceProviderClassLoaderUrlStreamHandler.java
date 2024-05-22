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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * A {@link URLStreamHandler} that expected a single {@link URL} and returns a {@link InputStream}.
 */
final class ClassLoaderResourceProviderClassLoaderUrlStreamHandler extends URLStreamHandler {

    /**
     * Factory called by {@link ClassLoaderResourceProviderClassLoader}
     */
    static ClassLoaderResourceProviderClassLoaderUrlStreamHandler with(final String name, final InputStream input,
                                                                       final ClassLoaderResourceProviderClassLoader loader) {
        return new ClassLoaderResourceProviderClassLoaderUrlStreamHandler(name, input, loader);
    }

    /**
     * Private constructor use factory.
     */
    private ClassLoaderResourceProviderClassLoaderUrlStreamHandler(final String name, final InputStream input,
                                                                   final ClassLoaderResourceProviderClassLoader loader) {
        super();

        this.name = name;
        this.input = input;
        this.loader = loader;
    }

    /**
     * Returns the cached {@link InputStream} or retrieves it from the {@link ClassLoaderResourceProviderClassLoader} if this is the second time this has been
     * called.
     */
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        InputStream input = this.input;
        this.input = null;
        if (null == input) {
            input = this.loader.inputStreamOrNull(this.name);
        }
        return ClassLoaderResourceProviderClassLoaderUrlConnection.with(url, input);
    }

    /**
     * Ignores the {@link URL} and returns just the name.
     */
    @Override
    protected String toExternalForm(final URL url) {
        final String name = this.name;
        return ClassLoaderResourceProviderClassLoader.PREFIX + (name.charAt(0) == '/' ? name.substring(1) : name);
    }

    /**
     * Kept so {@link #toString()} is meaningful.
     */
    private final String name;

    /**
     * The original {@link InputStream} that is returned only once when opened.
     */
    private InputStream input;

    private final ClassLoaderResourceProviderClassLoader loader;

    @Override
    public String toString() {
        return this.name.toString();
    }
}
