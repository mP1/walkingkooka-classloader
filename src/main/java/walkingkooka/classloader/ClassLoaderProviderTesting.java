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

import walkingkooka.reflect.ClassName;
import walkingkooka.test.Testing;

import java.util.Optional;

public interface ClassLoaderProviderTesting extends Testing {

    default void classFileAndCheck(final ClassLoaderResourceProvider provider,
                                   final ClassName name) {
        this.classFileAndCheck(
                provider,
                name,
                Optional.empty()
        );
    }

    default void classFileAndCheck(final ClassLoaderResourceProvider provider,
                                   final ClassName name,
                                   final ClassLoaderResource expected) {
        this.classFileAndCheck(
                provider,
                name,
                Optional.of(expected)
        );
    }

    default void classFileAndCheck(final ClassLoaderResourceProvider provider,
                                   final ClassName name,
                                   final Optional<ClassLoaderResource> expected) {
        this.checkEquals(
                expected,
                provider.classFile(name),
                () -> "load " + name
        );
    }
}
