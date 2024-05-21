/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.io.FileExtension;
import walkingkooka.naming.NameTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;

import java.util.Optional;

public final class ClassLoaderResourceNameTest implements ClassTesting2<ClassLoaderResourceName>,
        NameTesting<ClassLoaderResourceName, ClassLoaderResourceName> {

    // fileExtension........................................................................................................

    @Test
    public void testFileExtensionMissing() {
        this.fileExtensionAndCheck(
                ClassLoaderResourceName.with("xyz")
        );
    }

    @Test
    public void testFileExtensionEmpty() {
        this.fileExtensionAndCheck(
                ClassLoaderResourceName.with("xyz."),
                FileExtension.with("")
        );
    }

    @Test
    public void testFileExtensionPresent() {
        this.fileExtensionAndCheck(
                ClassLoaderResourceName.with("xyz.txt"),
                FileExtension.with("txt")
        );
    }

    @Test
    public void testFileExtensionPresent2() {
        this.fileExtensionAndCheck(
                ClassLoaderResourceName.with("xyz.EXE"),
                FileExtension.with("EXE")
        );
    }

    private void fileExtensionAndCheck(final ClassLoaderResourceName name) {
        this.fileExtensionAndCheck(
                name,
                Optional.empty()
        );
    }

    private void fileExtensionAndCheck(final ClassLoaderResourceName name,
                                       final FileExtension extension) {
        this.fileExtensionAndCheck(
                name,
                Optional.of(extension)
        );
    }

    private void fileExtensionAndCheck(final ClassLoaderResourceName name,
                                       final Optional<FileExtension> extension) {
        this.checkEquals(
                extension,
                name.fileExtension(),
                "file extension within " + name
        );
    }

    // NameTesting......................................................................................................

    @Override
    public ClassLoaderResourceName createName(final String name) {
        return ClassLoaderResourceName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.fileSystem();
    }

    @Override
    public String nameText() {
        return "file123.txt";
    }

    @Override
    public String differentNameText() {
        return "different.txt";
    }

    @Override
    public String nameTextLess() {
        return "abc.txt";
    }

    @Override
    public Class<ClassLoaderResourceName> type() {
        return ClassLoaderResourceName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
