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

import walkingkooka.collect.enumeration.Enumerations;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassName;
import walkingkooka.reflect.PackageName;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link ClassLoader} that uses a {@link ClassLoaderResourceProvider} to load class files which are then defined and materialized into a real {@link Class
 * classes}. Note that requests to {@link #getResource(String)} and {@link #getResources(String)} delegate to the parent {@link ClassLoader} without
 * attempting to use the {@link ClassLoaderResourceProvider}.
 */
final class ClassLoaderResourceProviderClassLoader extends ClassLoader {

    /**
     * The protocol returned by the created {@link URL#getProtocol()}.
     */
    final static String PROTOCOL = "walk";

    /**
     * Used purely for form a nicer looking URL.
     */
    final static String PREFIX = ClassLoaderResourceProviderClassLoader.PROTOCOL + "://";

    /**
     * Factory that creates a {@link ClassLoaderResourceProviderClassLoader} with a check if the {@link ClassLoader} already holds a {@link ClassLoaderResourceProvider}.
     */
    static ClassLoaderResourceProviderClassLoader with(final ClassLoader parent,
                                                       final ClassLoaderResourceProvider provider) {
        Objects.requireNonNull(parent, "parent");
        Objects.requireNonNull(provider, "provider");

        return new ClassLoaderResourceProviderClassLoader(
                parent,
                provider
        );
    }

    /**
     * Private constructor use static factory.
     */
    private ClassLoaderResourceProviderClassLoader(final ClassLoader parent,
                                                   final ClassLoaderResourceProvider provider) {
        super(parent);
        this.provider = provider;
    }

    /**
     * Loads and defines the identified class file. This method first attempts to load the class using the parent {@link ClassLoader} and if that fails
     * tries using the {@link ClassLoaderResourceProvider}.
     */
    @Override
    protected synchronized Class<?> loadClass(final String name,
                                              final boolean resolve) throws ClassNotFoundException {
        Objects.requireNonNull(name, "name");

        Class<?> klass = this.findLoadedClass(name);
        if (null == klass) {
            try {
                klass = super.loadClass(name, resolve);
            } catch (final ClassNotFoundException e) {
                if (null == klass) {
                    klass = this.loadClassUsingClassLoaderResourceProvider(
                            name,
                            resolve
                    );
                }
            }
        }

        return klass;
    }

    /**
     * Attempts to load and then define a {@link Class} using the {@link ClassLoaderResourceProvider} to retrieve the bytes.
     * If the class name is invalid the {@link IllegalArgumentException} thrown by {@link ClassName} is wrapped inside a {@link ClassNotFoundException}.
     */
    private Class<?> loadClassUsingClassLoaderResourceProvider(final String name,
                                                               final boolean resolve) throws ClassNotFoundException {
        final ClassName className = ClassName.with(name);

        try (final InputStream resource = this.getResourceAsStream(className.filename())) {
            if (null == resource) {
                throw new ClassNotFoundException(name);
            }
            return this.definePackagesClassAndMaybeResolve(
                    resource.readAllBytes(),
                    name,
                    resolve,
                    className
            );
        } catch (final IOException cause) {
            throw new ClassNotFoundException(
                    cause.getMessage(),
                    cause
            );
        }
    }

    /**
     * Defines outstanding packages then the class and possibly resolves it.
     */
    private Class<?> definePackagesClassAndMaybeResolve(final byte[] classFile,
                                                        final String name,
                                                        final boolean resolve,
                                                        final ClassName className) throws ClassFormatError {
        this.definePackages(className.parentPackage());

        final Class<?> klass = this.defineClass(name, classFile, 0, classFile.length);
        if (null == klass) {
            throw new ClassFormatError(name);
        }

        if (resolve) {
            this.resolveClass(klass);
        }
        return klass;
    }

    /**
     * Defines packages that do not already exist.
     */
    private void definePackages(final PackageName name) {
        if ((null != name) && PackageName.UNNAMED.equals(name)) {
            if (false == this.definedPackages.add(name)) {
                try {
                    this.definePackages(name.parent());
                    this.definePackage(name.value(), //
                            null /* specTitle */, //
                            null /* specVersion */,//
                            null /* specVendor */, //
                            null /* implTitle */, //
                            null /* implVersion */, //
                            null /* implVendor */,//
                            null /* seal url */);
                } catch (final IllegalArgumentException ignore) {
                }
            }
        }
    }

    /**
     * Attempts to locate the resource using the given {@link ClassLoader} and then asks the {@link ClassLoaderResourceProvider}
     */
    @Override
    public InputStream getResourceAsStream(final String name) {
        Objects.requireNonNull(name, "name");

        InputStream resource = super.getResourceAsStream(name);
        if (null == resource) {
            resource = this.inputStreamOrNull(name);
        }
        return resource;
    }

    /**
     * If the parent {@link ClassLoader} returns null then attempt this {@link ClassLoaderResourceProvider}. Null is returned if nothing is found by either.
     */
    @Override
    public URL getResource(final String name) {
        URL url = super.getResource(name);
        if (null == url) {
            final InputStream inputStream = this.inputStreamOrNull(name);
            if (null != inputStream) {
                url = this.createUrl(name, inputStream);
            }
        }
        return url;
    }

    /**
     * First queries the parent {@link ClassLoader} and then the wrapped {@link ClassLoaderResourceProvider} chaining them together as necessary.
     */
    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        Objects.requireNonNull(name, "name");

        Enumeration<URL> resources = super.getResources(name);

        // chain if ClassLoaderResourceProvider returns an InputStream.
        final InputStream input = this.inputStreamOrNull(name);
        if (null != input) {
            resources = Enumerations.chain(
                    Lists.of(
                            resources,
                            Enumerations.iterator(
                                    Iterators.one(
                                            this.createUrl(name, input)
                                    )
                            )
                    )
            );
        }
        return resources;
    }

    /**
     * Creates a {@link URL} with {@link ClassLoaderResourceProviderClassLoaderUrlStreamHandler} which also holds the {@link InputStream}.
     */
    private URL createUrl(final String name, final InputStream input) {
        try {
            return new URL(
                    ClassLoaderResourceProviderClassLoader.PROTOCOL,
                    "",
                    0,
                    name,
                    ClassLoaderResourceProviderClassLoaderUrlStreamHandler.with(
                            name,
                            input,
                            this
                    )
            );
        } catch (final MalformedURLException cause) {
            throw new RuntimeException("Unable to create Url for " + name, cause);
        }
    }

    /**
     * Attempts to resolve the given name to an {@link InputStream}. Null will be returned if it is unknown.
     */
    InputStream inputStreamOrNull(final String name) {
        InputStream resource = null;

        if (name.length() > 0) {
            ClassLoaderResourcePath path = null;
            try {
                path = ClassLoaderResourcePath.parse(
                        ClassLoaderResourceProviderClassLoader.ROOT != name.charAt(0) ?
                                ClassLoaderResourcePath.SEPARATOR.string()
                                        .concat(name)
                                : name
                );
                resource = this.provider.load(path)
                        .map(r -> r.value().inputStream())
                        .orElse(null);
            } catch (final IllegalArgumentException cause) {
                if (null != path) {
                    throw cause;
                }
            }
        }
        return resource;
    }

    private final static char ROOT = ClassLoaderResourcePath.SEPARATOR.character();

    /**
     * The provider that attempts to locate classes/resources
     */
    private final ClassLoaderResourceProvider provider;

    /**
     * A cache of previous defined packages.
     */
    private final Set<PackageName> definedPackages = Sets.hash();

    // toString.........................................................................................................

    /**
     * Dumps the wrapped parent {@link ClassLoader} and then {@link ClassLoaderResourceProvider}
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "->" + this.provider;
    }
}
