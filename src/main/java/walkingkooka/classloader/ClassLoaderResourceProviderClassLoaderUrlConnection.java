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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * The {@link URLConnection} returned by {@link ClassLoaderResourceProviderClassLoaderUrlStreamHandler}.
 */
final class ClassLoaderResourceProviderClassLoaderUrlConnection extends URLConnection {

  /**
   * Factory that is called by {@link ClassLoaderResourceProviderClassLoaderUrlStreamHandler}
   */
  static ClassLoaderResourceProviderClassLoaderUrlConnection with(final URL url,
                                                                  final InputStream input) {
    return new ClassLoaderResourceProviderClassLoaderUrlConnection(url, input);
  }

  /**
   * Private constructor use factory
   */
  private ClassLoaderResourceProviderClassLoaderUrlConnection(final URL url,
                                                              final InputStream input) {
    super(url);
    this.input = input;
  }

  @Override
  public void connect() {
    // nop
  }

  @Override
  public InputStream getInputStream() {
    return this.input;
  }

  /**
   * The returned {@link InputStream}.
   */
  private final InputStream input;

  /**
   * Returns {@link URL#toString()}
   */
  @Override
  public String toString() {
    return this.url.toString();
  }
}
