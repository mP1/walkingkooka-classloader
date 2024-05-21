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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ClassLoaderResourceTest implements ClassTesting<ClassLoaderResource>,
        HashCodeEqualsDefinedTesting2<ClassLoaderResource>,
        ToStringTesting<ClassLoaderResource> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResource.with(null)
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
                ClassLoaderResource.with(
                        Binary.with("Different".getBytes(StandardCharsets.UTF_8))
                )
        );
    }

    // hashcode/equals..................................................................................................

    @Override
    public ClassLoaderResource createObject() {
        return ClassLoaderResource.with(
                Binary.with("ABC".getBytes(StandardCharsets.UTF_8))
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                ClassLoaderResource.with(
                        Binary.with("Different".getBytes(StandardCharsets.UTF_8))
                ),
                "[68, 105, 102, 102, 101, 114, 101, 110, 116]"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<ClassLoaderResource> type() {
        return ClassLoaderResource.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
