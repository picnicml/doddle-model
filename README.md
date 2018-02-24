## Doddle
<table>
<tr>
  <td>Latest Release</td>
  <td>
    <a href="https://github.com/inejc/doddle/releases">
    <img src="https://img.shields.io/github/release/inejc/doddle.svg?style=flat-square&label=version" alt="latest release"/>
    </a>
  </td>
</tr>
<tr>
  <td>Build Status</td>
  <td>
    <a href="https://circleci.com/gh/inejc/doddle">
    <img src="https://img.shields.io/circleci/project/github/inejc/doddle.svg?style=flat-square" alt="build status"/>
    </a>
  </td>
</tr>
<tr>
  <td>Coverage</td>
  <td>
    <a href="https://codecov.io/gh/inejc/doddle">
    <img src="https://img.shields.io/codecov/c/github/inejc/doddle.svg?style=flat-square&label=codecov" alt="coverage"/>
    </a>
  </td>
</tr>
<tr>
  <td>Code Quality</td>
  <td>
    <a href="https://app.codacy.com/app/inejc/doddle">
    <img src="https://img.shields.io/codacy/grade/4b13da3c6435458dac117ac4cd2deca8.svg?style=flat-square&label=codacy" alt="code quality"/>
    </a>
  </td>
</tr>
<tr>
  <td>License</td>
  <td>
    <a href="https://github.com/inejc/doddle/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/inejc/doddle.svg?style=flat-square" alt="license"/>
    </a>
  </td>
</tr>
</table>

Doddle is an in-memory machine learning library built on top of [Breeze](https://github.com/scalanlp/breeze). It provides immutable objects that are a _doddle_ to use in parallel code.

### Example usage
To train a linear regression model run:
```scala
import com.doddle.data.DataLoaders.loadBostonDataset
import com.doddle.linear.LinearRegression
import com.doddle.metrics.Regression.rmse

val (x, y) = loadBostonDataset
val model = LinearRegression()

val trainedModel = model.fit(x, y)
val yPred = trainedModel.predict(x)

rmse(y, yPred)
// res1: Double = 4.679242291386644
```

Or instantiate and train a model in a single line, e.g.:
```scala
val trainedModel = LinearRegression().fit(x, y)
```

### Resources
- [Pattern Recognition and Machine Learning, Christopher Bishop](http://www.springer.com/gp/book/9780387310732)
- Datasets from [UCI Machine Learning Repository](http://archive.ics.uci.edu/ml)
