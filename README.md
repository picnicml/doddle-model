<img src="https://github.com/picnicml/doddle-model/blob/master/.github/doddle-model-header.png" alt="doddle-model"/>

---

<table>
    <tr>
        <th>Latest Release</th>
        <th>Build Status</th>
        <th>Coverage</th>
        <th>Code Quality</th>
        <th>License</th>
        <th>Chat</th>
    </tr>
    <tr>
        <td>
            <a href="https://search.maven.org/search?q=g:io.github.picnicml">
                <img src="https://img.shields.io/maven-central/v/io.github.picnicml/doddle-model_2.12.svg?style=flat-square&label=maven%20central" alt="latest release"/>
            </a>
        </td>
        <td>
            <a href="https://circleci.com/gh/picnicml/doddle-model">
                <img src="https://img.shields.io/circleci/project/github/picnicml/doddle-model/master.svg?style=flat-square&label=circleci" alt="build status"/>
            </a>
        </td>
        <td>
            <a href="https://app.codacy.com/project/inejc/doddle-model/dashboard">
                <img src="https://img.shields.io/codacy/coverage/9f1dad5c6d6041dd85db71adabba3e72.svg?style=flat-square&label=codacy" alt="coverage"/>
            </a>
        </td>
        <td>
            <a href="https://app.codacy.com/project/inejc/doddle-model/dashboard">
                <img src="https://img.shields.io/codacy/grade/9f1dad5c6d6041dd85db71adabba3e72/master.svg?style=flat-square&label=codacy" alt="code quality"/>
            </a>
        </td>
        <td>
            <a href="https://github.com/picnicml/doddle-model/blob/master/LICENSE">
                <img src="https://img.shields.io/github/license/picnicml/doddle-model.svg?style=flat-square&label=picnicml" alt="license"/>
            </a>
        </td>
        <td>
            <a href="https://gitter.im/picnicml/doddle-model">
                <img src="https://img.shields.io/gitter/room/nwjs/nw.js.svg?style=flat-square&label=picnicml" alt="chat"/>
            </a>
        </td>
    </tr>
</table>

---

`doddle-model` is an in-memory machine learning library that can be summed up with three main characteristics:
* it is built on top of [Breeze](https://github.com/scalanlp/breeze)
* it provides [immutable estimators](https://en.wikipedia.org/wiki/Immutable_object) that are a _doddle_ to use in parallel code
* it exposes its functionality through a [scikit-learn](https://github.com/scikit-learn/scikit-learn)-like API [2] in idiomatic Scala using [typeclasses](https://en.wikipedia.org/wiki/Type_class)

#### How does it compare to existing solutions?
`doddle-model` takes the position of scikit-learn in Scala and as a consequence, it's much more lightweight than e.g. Spark ML. Fitted models can be deployed anywhere, from simple applications to concurrent, distributed systems built with Akka, Apache Beam or a framework of your choice. Training of estimators happens in-memory, which is advantageous unless you are dealing with enormous datasets that absolutely cannot fit into RAM.

### Installation
The project is published for Scala versions 2.11, 2.12 and 2.13. Add the dependency to your SBT project definition:
```scala
libraryDependencies  ++= Seq(
  "io.github.picnicml" %% "doddle-model" % "<latest_version>",
  // add optionally to utilize native libraries for a significant performance boost
  "org.scalanlp" %% "breeze-natives" % "1.0"
)
```
Note that the latest version is displayed in the _Latest Release_ badge above and that the _v_ prefix should be removed from the SBT definition.

### Getting Started
For a complete list of code examples see [doddle-model-examples](https://github.com/picnicml/doddle-model-examples).

### Contributing
Want to help us? :raised_hands: We have a [document](https://github.com/picnicml/doddle-model/blob/master/.github/CONTRIBUTING.md) that will make deciding how to do that much easier.

### Performance
Performance of implementations is described [here](https://github.com/picnicml/doddle-model/wiki/Performance). Also, take a peek at what's written in that document if you encounter `java.lang.OutOfMemoryError: Java heap space`.

### Core Maintainers
This is a collaborative project which wouldn't be possible without all the [awesome contributors](https://github.com/picnicml/doddle-model/graphs/contributors). The core team currently consists of the following developers:
- [@inejc](https://github.com/inejc)
- [@matejklemen](https://github.com/matejklemen)

### Resources
* [1] [Pattern Recognition and Machine Learning, Christopher Bishop](http://www.springer.com/gp/book/9780387310732)
* [2] [API design for machine learning software: experiences from the scikit-learn project, L. Buitinck et al.](https://arxiv.org/abs/1309.0238)
* [3] [UCI Machine Learning Repository. Irvine, CA: University of California, School of Information and Computer Science, Dua, D. and Karra Taniskidou, E.](http://archive.ics.uci.edu/ml)
