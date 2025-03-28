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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.PathSeparator;
import walkingkooka.naming.PathTesting;
import walkingkooka.reflect.ClassName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class ClassLoaderResourcePathTest implements PathTesting<ClassLoaderResourcePath, ClassLoaderResourceName>,
        ClassTesting2<ClassLoaderResourcePath>,
        ParseStringTesting<ClassLoaderResourcePath> {

    @Override
    public void testAllConstructorsVisibility() {
    }

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
    }

    // parse............................................................................................................

    @Test
    public void testParseMissingRequiredLeadingSlashFails() {
        this.parseStringFails(
                "without-leading-slash",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseEmptyComponentFails() {
        this.parseStringFails(
                "/before//after",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseSlash() {
        final String value = "/";

        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse(value);
        this.valueCheck(path, value);
        this.rootCheck(path);
        this.nameCheck(path, ClassLoaderResourcePath.ROOT_NAME);
        this.parentAbsentCheck(path);
    }

    @Test
    public void testParseManifestAllUpperCase() {
        final String value = "/META-INF/MANIFEST.MF";

        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse(value);

        assertSame(
                path,
                ClassLoaderResourcePath.MANIFEST
        );

        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(path, ClassLoaderResourceName.with("MANIFEST.MF"));
        this.parentCheck(path, "/META-INF");
    }

    @Test
    public void testParseFlat() {
        final String value = "/path to";

        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse(value);
        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(path, ClassLoaderResourceName.with("path to"));
        this.parentSame(path, ClassLoaderResourcePath.ROOT);
    }

    @Test
    public void testParseHierarchical() {
        final String value = "/path/to";
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse(value);
        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(path, ClassLoaderResourceName.with("to"));
        this.parentCheck(path, "/path");
    }

    @Test
    public void testParseHierarchical2() {
        final String value = "/path/to/xyz";

        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse(value);
        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(path, ClassLoaderResourceName.with("xyz"));

        this.parentCheck(path, "/path/to");

        final ClassLoaderResourcePath parent = path.parent()
                .get();

        this.valueCheck(parent, "/path/to");
        this.rootNotCheck(parent);
        this.nameCheck(parent, ClassLoaderResourceName.with("to"));
        this.parentCheck(parent, "/path");
    }

    @Test
    public void testParseIncludesDot() {
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse("/path1/./path2/./path3");
        this.valueCheck(
                path,
                "/path1/path2/path3"
        );
        this.rootNotCheck(path);
        this.nameCheck(
                path,
                ClassLoaderResourceName.with("path3")
        );

        this.parentCheck(
                path,
                "/path1/path2"
        );
    }

    @Test
    public void testParseIncludesTrailingDot() {
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse("/path1/path2/path3/.");
        this.valueCheck(
                path,
                "/path1/path2/path3"
        );
        this.rootNotCheck(path);
        this.nameCheck(
                path,
                ClassLoaderResourceName.with("path3")
        );

        this.parentCheck(
                path,
                "/path1/path2"
        );
    }

    @Test
    public void testParseIncludesDoubleDot() {
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse("/path1/./path2/../path3");
        this.valueCheck(
                path,
                "/path1/path3"
        );
        this.rootNotCheck(path);
        this.nameCheck(
                path,
                ClassLoaderResourceName.with("path3")
        );

        this.parentCheck(
                path,
                "/path1"
        );
    }

    @Test
    public void testParseIncludesTrailingDoubleDot() {
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.parse("/path1/path2/path3/..");
        this.valueCheck(
                path,
                "/path1/path2"
        );
        this.rootNotCheck(path);
        this.nameCheck(
                path,
                ClassLoaderResourceName.with("path2")
        );

        this.parentCheck(
                path,
                "/path1"
        );
    }

    // ParseStringTesting ..............................................................................................

    @Test
    public void testParseStartsWithDoubleDotFails() {
        this.parseStringFails(
                "/../hello",
                new IllegalArgumentException("Invalid path \"/../hello\"")
        );
    }

    @Override
    public ClassLoaderResourcePath parseString(final String text) {
        return ClassLoaderResourcePath.parse(text);
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    // fromClassName....................................................................................................

    @Test
    public void testFromClassNameNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> ClassLoaderResourcePath.from(null)
        );
    }

    @Test
    public void testFromClassName() {
        this.checkEquals(
                ClassLoaderResourcePath.parse("/java/lang/String.class"),
                ClassLoaderResourcePath.from(ClassName.fromClass(String.class))
        );
    }

    // path.............................................................................................................

    @Test
    public void testRoot() {
        final ClassLoaderResourcePath path = ClassLoaderResourcePath.ROOT;
        this.rootCheck(path);
        this.valueCheck(path, "/");
        this.nameSameCheck(path, ClassLoaderResourcePath.ROOT_NAME);
        this.parentAbsentCheck(path);
    }

    // appendName.......................................................................................................

    @Test
    public void testAppendNameToRoot2() {
        final ClassLoaderResourceName name = ClassLoaderResourceName.with("name1");

        final ClassLoaderResourcePath path = ClassLoaderResourcePath.ROOT.append(name);
        this.rootNotCheck(path);
        this.valueCheck(path, "/name1");
        this.nameCheck(
                path,
                name
        );
    }

    @Test
    public void testAppendNameToNonRoot() {
        final ClassLoaderResourcePath parent = ClassLoaderResourcePath.parse("/parent1");
        final ClassLoaderResourceName name = ClassLoaderResourceName.with("name2");

        final ClassLoaderResourcePath path = parent.append(name);
        this.rootNotCheck(path);
        this.valueCheck(path, "/parent1/name2");
        this.nameCheck(
                path,
                name
        );
    }

    // appendPath.......................................................................................................

    @Test
    public void testAppendPathToRoot2() {
        final ClassLoaderResourceName name = ClassLoaderResourceName.with("name1");

        final ClassLoaderResourcePath parent = ClassLoaderResourcePath.ROOT.append(name);
        this.rootNotCheck(parent);
        this.valueCheck(parent, "/name1");
        this.nameCheck(
                parent,
                name
        );
    }

    @Test
    public void testAppendPathToNonRoot() {
        final ClassLoaderResourcePath parent = ClassLoaderResourcePath.parse("/parent1");
        final ClassLoaderResourcePath path2 = ClassLoaderResourcePath.parse("/path2");

        final ClassLoaderResourcePath path = parent.append(path2);
        this.rootNotCheck(path);
        this.valueCheck(
                path,
                "/parent1/path2"
        );
        this.nameCheck(
                path,
                ClassLoaderResourceName.with("path2")
        );
        this.parentCheck(
                path,
                "/parent1"
        );
    }

    @Test
    public void testAppendPathToNonRootTwice() {
        final ClassLoaderResourcePath parent = ClassLoaderResourcePath.parse("/parent1");
        final ClassLoaderResourcePath path2 = ClassLoaderResourcePath.parse("/path2");
        final ClassLoaderResourcePath path34 = ClassLoaderResourcePath.parse("/path3/path4");

        final ClassLoaderResourcePath path = parent.append(path2)
                .append(path34);
        this.rootNotCheck(path);
        this.valueCheck(
                path,
                "/parent1/path2/path3/path4"
        );
        this.nameCheck(
                path,
                ClassLoaderResourceName.with("path4")
        );
        this.parentCheck(
                path,
                "/parent1/path2/path3"
        );
    }

    // equals/Compare...................................................................................................

    @Test
    public void testEqualsManifestDifferentCase() {
        this.checkEquals(
                ClassLoaderResourcePath.MANIFEST,
                ClassLoaderResourcePath.parse("/meta-inf/manifest.mf")
        );
    }

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(ClassLoaderResourcePath.parse("/different"));
    }

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(ClassLoaderResourcePath.parse("/zebra"));
    }

    @Test
    public void testCompareMore() {
        this.compareToAndCheckMore(ClassLoaderResourcePath.parse("/before"));
    }

    @Override
    public ClassLoaderResourcePath root() {
        return ClassLoaderResourcePath.ROOT;
    }

    @Override
    public ClassLoaderResourcePath createPath() {
        return ClassLoaderResourcePath.parse("/path");
    }

    @Override
    public ClassLoaderResourcePath parsePath(final String path) {
        return ClassLoaderResourcePath.parse(path);
    }

    @Override
    public ClassLoaderResourceName createName(final int n) {
        return ClassLoaderResourceName.with("string-name-" + n);
    }

    @Override
    public PathSeparator separator() {
        return ClassLoaderResourcePath.SEPARATOR;
    }

    // ComparableTesting................................................................................................

    @Override
    public ClassLoaderResourcePath createComparable() {
        return ClassLoaderResourcePath.parse("/path");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<ClassLoaderResourcePath> type() {
        return ClassLoaderResourcePath.class;
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ConstantTesting ..................................................................................................

    @Override
    public Set<ClassLoaderResourcePath> intentionalDuplicateConstants() {
        return Sets.empty();
    }
}
