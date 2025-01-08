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
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassName;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class ClassLoaderResourceProviderClassLoaderTest implements ClassTesting<ClassLoaderResourceProviderClassLoader>,
        ClassLoaderTesting<ClassLoaderResourceProviderClassLoader> {

    // constants

    private final static ClassLoader PARENT_CLASS_LOADER = new ClassLoader() {

        @Override
        public Class<?> loadClass(final String name,
                                  final boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("java.") || name.equals(Value.class.getName())) {
                return ClassLoader.getSystemClassLoader()
                        .loadClass(name);
            }
            throw new ClassNotFoundException("Parent fails");
        }
    };

    @Test
    public void testWithNullParentFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResourceProviderClassLoader.with(
                        null,
                        ClassLoaderResourceProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResourceProviderClassLoader.with(
                        PARENT_CLASS_LOADER,
                        null
                )
        );
    }

    // getResource......................................................................................................

    @Test
    public void testGetResource() throws Exception {
        final String path = "walkingkooka/classloader/test-resource-123.txt";
        final byte[] value = new byte[]{
                '1',
                '2',
                '3'
        };

        this.getResourceAndCheck(
                ClassLoader.getSystemClassLoader(),
                path,
                value
        );

        this.getResourceAndCheck(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath p) {
                                checkEquals(ClassLoaderResourcePath.parse("/" + path), p);

                                return Optional.of(
                                        ClassLoaderResource.with(
                                                Binary.with(value)
                                        )
                                );
                            }
                        }
                ),
                path,
                value
        );
    }

    @Test
    public void testGetResource2() throws Exception {
        final String path = "custom-class-loader-resource.txt";
        final byte[] value = new byte[]{
                1,
                2,
                3
        };

        this.getResourceAndCheck(
                ClassLoader.getSystemClassLoader(),
                path,
                null
        );

        this.getResourceAndCheck(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath p) {
                                checkEquals(ClassLoaderResourcePath.parse("/" + path), p);

                                return Optional.of(
                                        ClassLoaderResource.with(
                                                Binary.with(value)
                                        )
                                );
                            }
                        }
                ),
                path,
                value
        );
    }

    // getResourceAsStream..............................................................................................

    @Test
    public void testGetResourceAsStream() throws Exception {
        final String path = "custom-class-loader-resource2.txt";
        final byte[] value = new byte[]{
                1,
                2,
                3
        };

        this.getResourceAsStreamAndCheck(
                ClassLoader.getSystemClassLoader(),
                path,
                null
        );

        this.getResourceAsStreamAndCheck(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath p) {
                                checkEquals(
                                        ClassLoaderResourcePath.parse("/" + path),
                                        p
                                );

                                return Optional.of(
                                        ClassLoaderResource.with(
                                                Binary.with(value)
                                        )
                                );
                            }
                        }
                ),
                path,
                value
        );
    }


    // getResources.....................................................................................................

    @Test
    public void testGetResources() throws Exception {
        final String path = "walkingkooka/classloader/test-resource-123.txt";
        final byte[] value = new byte[]{
                '1',
                '2',
                '3'
        };

        this.getResourcesAndCheck(
                ClassLoader.getSystemClassLoader(),
                path,
                value
        );

        this.getResourcesAndCheck(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath p) {
                                checkEquals(ClassLoaderResourcePath.parse("/" + path), p);

                                return Optional.of(
                                        ClassLoaderResource.with(
                                                Binary.with(value)
                                        )
                                );
                            }
                        }
                ),
                path,
                value,
                value // loaded by both system and custom
        );
    }

    @Test
    public void testGetResources2() throws Exception {
        final String path = "custom-class-loader-resource.txt";
        final byte[] value = new byte[]{
                1,
                2,
                3
        };

        this.getResourcesAndCheck(
                ClassLoader.getSystemClassLoader(),
                path,
                Lists.empty()
        );

        this.getResourcesAndCheck(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath p) {
                                checkEquals(ClassLoaderResourcePath.parse("/" + path), p);

                                return Optional.of(
                                        ClassLoaderResource.with(
                                                Binary.with(value)
                                        )
                                );
                            }
                        }
                ),
                path,
                value
        );
    }

    // loadClass........................................................................................................

    @Test
    public void testLoadClassSystem() throws Exception {
        assertSame(
                Object.class,
                ClassLoaderResourceProviderClassLoader.with(
                        PARENT_CLASS_LOADER,
                        ClassLoaderResourceProviders.fake()
                ).loadClass(Object.class.getName())
        );
    }

    @Test
    public void testLoadClassUnknownFails() {
        this.loadClassAndFail(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
                                return Optional.empty();
                            }
                        }
                ),
                "clasc.Unknown404",
                ClassNotFoundException.class
        );
    }

    @Test
    public void testLoadClassInvalidClassFileFails() {
        this.loadClassAndFail(
                ClassLoaderResourceProviderClassLoader.with(//
                        ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                        new ClassLoaderResourceProvider() {

                            @Override
                            public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
                                checkEquals("/test/InvalidClass.class", path.value(), "load");
                                return Optional.of(
                                        ClassLoaderResource.with(
                                                Binary.with(
                                                        new byte[10]
                                                )
                                        )
                                );
                            }
                        }
                ),
                "test.InvalidClass",
                ClassFormatError.class
        );
    }

    @Test
    public void testClassLoad() throws Exception {
        final ClassName className = ClassName.with("walkingkooka.classloader.TestClass");

        final ClassLoaderResourceProviderClassLoader classLoader = ClassLoaderResourceProviderClassLoader.with(//
                ClassLoaderResourceProviderClassLoaderTest.PARENT_CLASS_LOADER, //
                new ClassLoaderResourceProvider() {

                    @Override
                    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
                        final String classNameFilename = className.filename();
                        checkEquals(classNameFilename, path.value(), "load");

                        try {
                            final byte[] bytes = ClassLoader.getSystemClassLoader()
                                    .getResourceAsStream(classNameFilename)
                                    .readAllBytes();

                            return Optional.of(
                                    ClassLoaderResource.with(
                                            Binary.with(bytes)
                                    )
                            );
                        } catch (final IOException cause) {
                            throw new Error(cause);
                        }
                    }
                }
        );
        final Class<?> klass = classLoader.loadClass(
                className.value(),
                true // resolve
        );
        this.checkEquals(
                className.value(),
                klass.getName(),
                "class name"
        );
        this.checkEquals(
                classLoader,
                klass.getClassLoader()
        );

        final Object instance = klass.newInstance();
        this.checkEquals(
                "XYZ123",
                klass.getMethod("value")
                        .invoke(instance)
        );

        // cast should work.
        final Value<String> value = (Value<String>) instance;
        this.checkEquals(
                "XYZ123",
                value.value()
        );

        // cast should not work because there will be systemclassloader/TestInterface and classloader/TestInterface
        assertThrows(
                ClassCastException.class,
                () -> TestInterface.class.cast(instance)
        );
    }

    @Override
    public ClassLoaderResourceProviderClassLoader createClassLoader() {
        return ClassLoaderResourceProviderClassLoader.with(
                PARENT_CLASS_LOADER,
                new ClassLoaderResourceProvider() {

                    @Override
                    public Optional<ClassLoaderResource> load(final ClassLoaderResourcePath path) {
                        final InputStream inputStream = ClassLoader.getSystemClassLoader()
                                .getResourceAsStream(path.value());

                        try {
                            return Optional.ofNullable(
                                    null == inputStream ?
                                            null :
                                            ClassLoaderResource.with(
                                                    Binary.with(
                                                            inputStream.readAllBytes()
                                                    )
                                            )
                            );
                        } catch (final IOException cause) {
                            throw new Error(cause);
                        }
                    }
                }
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<ClassLoaderResourceProviderClassLoader> type() {
        return ClassLoaderResourceProviderClassLoader.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
