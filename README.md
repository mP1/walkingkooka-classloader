[![Build Status](https://github.com/mP1/walkingkooka-classloader/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-classloader/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-classloader/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-classloader?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-classloader.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-classloader/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-classloader.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-classloader/alerts/)

# walkingkooka-classloader
A repo that *ONLY* provides ClassLoader support for the following:

Goals

- Provide support for hot deployments.
- Provide support for starting/stopping plugins.

Non goals

- Blacklisting APIs on any level.
- Mirror support for JRE reflective APIs.
- A module system of any kind.
- A security manager or system of any kind.
