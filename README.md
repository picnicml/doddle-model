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
    <img src="https://img.shields.io/circleci/project/github/picnicml/doddle-model.svg?style=flat-square" alt="build status"/>
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
  <td>Code Quality</td>
  <td>
    <a href="https://app.codacy.com/app/picnicml/doddle-model">
    <img src="https://img.shields.io/codacy/grade/2c21167da0154c44afd8381fe82f93d7.svg?style=flat-square&label=codacy" alt="code quality"/>
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
For a complete list of examples see [doddle-model-examples](https://github.com/picnicml/doddle-model-examples).

### Resources
* [1] [Pattern Recognition and Machine Learning, Christopher Bishop](http://www.springer.com/gp/book/9780387310732)
* [2] [API design for machine learning software: experiences from the scikit-learn project, L. Buitinck et al.](https://arxiv.org/abs/1309.0238)
* [3] [UCI Machine Learning Repository. Irvine, CA: University of California, School of Information and Computer Science, Dua, D. and Karra Taniskidou, E.](http://archive.ics.uci.edu/ml)
