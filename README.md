## doddle-model
<table>
<tr>
  <td>Latest Release</td>
  <td>
    <a href="https://github.com/picnicml/doddle-model/releases">
    <img src="https://img.shields.io/github/release/picnicml/doddle-model.svg?style=flat-square&label=version" alt="latest release"/>
    </a>
  </td>
</tr>
<tr>
  <td>Build Status</td>
  <td>
    <a href="https://circleci.com/gh/picnicml/doddle-model">
    <img src="https://img.shields.io/circleci/project/github/picnicml/doddle-model/master.svg?style=flat-square" alt="build status"/>
    </a>
  </td>
</tr>
<tr>
  <td>Coverage</td>
  <td>
    <a href="https://codecov.io/gh/picnicml/doddle-model">
    <img src="https://img.shields.io/codecov/c/github/picnicml/doddle-model.svg?style=flat-square&label=codecov" alt="coverage"/>
    </a>
  </td>
</tr>
<tr>
  <td>Chat</td>
  <td>
    <a href="https://gitter.im/picnicml/doddle-model">
    <img src="https://img.shields.io/gitter/room/nwjs/nw.js.svg?style=flat-square&label=picnicml" alt="chat"/>
    </a>
  </td>
</tr>
<tr>
  <td>License</td>
  <td>
    <a href="https://github.com/picnicml/doddle-model/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/picnicml/doddle-model.svg?style=flat-square" alt="license"/>
    </a>
  </td>
</tr>
</table>

`doddle-model` is an in-memory machine learning library that can be summed up with three main characteristics:
* it is built on top of [Breeze](https://github.com/scalanlp/breeze)
* it provides [immutable objects](https://en.wikipedia.org/wiki/Immutable_object) that are a _doddle_ to use in parallel code
* it exposes its functionality through a [scikit-learn](https://github.com/scikit-learn/scikit-learn)-like API [2]

**Caveat emptor!** `doddle-model` is in an early-stage development phase. Any kind of contributions are much appreciated.

### Installation
Publish the project to a local Ivy repository:
```bash
git clone https://github.com/picnicml/doddle-model.git
cd doddle-model
sbt publish-local
```

Add dependency to your SBT project definition:
```scala
libraryDependencies += "com.picnicml" %% "doddle-model" % "0.0.0"
```

### Getting Started
For a complete list of code examples see [doddle-model-examples](https://github.com/picnicml/doddle-model-examples). For an example of how to serve a trained `doddle-model` in a pipeline implemented with Apache Beam see [doddle-beam-example](https://github.com/picnicml/doddle-beam-example).

### Performance
`doddle-model` is developed with performance in mind, for benchmarks see the [doddle-benchmark](https://github.com/picnicml/doddle-benchmark) repository.

##### Native Linear Algebra Libraries
[Breeze](https://github.com/scalanlp/breeze) utilizes [netlib-java](https://github.com/fommil/netlib-java) for accessing hardware optimised linear algebra libraries. TL;DR seeing something like
```
INFO: successfully loaded /var/folders/9h/w52f2svd3jb750h890q1x4j80000gn/T/jniloader3358656786070405996netlib-native_system-osx-x86_64.jnilib
```
means that BLAS/LAPACK/ARPACK implementations are used. For more information see the [Breeze](https://github.com/scalanlp/breeze) documentation.

##### Memory
If you encounter `java.lang.OutOfMemoryError: Java heap space` increase the maximum heap size with `-Xms` and `-Xmx` JVM properties. E.g. use `-Xms8192m -Xmx8192m` for initial and maximum heap space of 8Gb. Note that the maximum heap limit for the 32-bit JVM is 4Gb (at least in theory) so make sure to use 64-bit JVM if more memory is needed. If the error still occurs and you are using hyperparameter search or cross validation, see the next section.

##### Parallelism
To limit the number of threads running at one time (and thus memory consumption) when doing cross validation and hyperparameter search, a `FixedThreadPool` executor is used. By default, only a single thread is allowed. Set the `-DmaxNumThreads` JVM property to relax that, e.g. to allow for 100 threads use `-DmaxNumThreads=100`.

### Development
Run the tests with `sbt test`. Concerning the code style, [PayPal Scala Style](https://github.com/paypal/scala-style-guide) and [Databricks Scala Guide](https://github.com/databricks/scala-style-guide) are roughly followed. Note that a maximum line length of 120 characters is used.

### Resources
* [1] [Pattern Recognition and Machine Learning, Christopher Bishop](http://www.springer.com/gp/book/9780387310732)
* [2] [API design for machine learning software: experiences from the scikit-learn project, L. Buitinck et al.](https://arxiv.org/abs/1309.0238)
* [3] [UCI Machine Learning Repository. Irvine, CA: University of California, School of Information and Computer Science, Dua, D. and Karra Taniskidou, E.](http://archive.ics.uci.edu/ml)
