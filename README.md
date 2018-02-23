## Doddle
[![CircleCI](https://circleci.com/gh/inejc/doddle.svg?style=shield)](https://circleci.com/gh/inejc/doddle)
[![codecov](https://codecov.io/gh/inejc/doddle/branch/master/graph/badge.svg)](https://codecov.io/gh/inejc/doddle)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Github Tag](https://img.shields.io/github/tag/inejc/doddle.svg?label=release)](https://github.com/inejc/doddle/releases)

Doddle is an in-memory machine learning library built on top of [Breeze](https://github.com/scalanlp/breeze).

### Example usage
```scala
import com.doddle.data.DataLoaders.loadBoston
import com.doddle.linear.LinearRegression
import com.doddle.metrics.Regression.rmse

val (x, y) = loadBoston
val model = LinearRegression()

model.fit(x, y)
val yPred = model.predict(x)

rmse(y, yPred)
// res1: Double = 4.679242291386644
```

### Resources
- Pattern Recognition and Machine Learning, Christopher Bishop
- Datasets from [UCI Machine Learning Repository](http://archive.ics.uci.edu/ml)
