package io.picnicml.doddlemodel.data

import java.io.{File, FileOutputStream}

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.data.CsvLoader.loadCsvDataset

object ResourceDatasetLoaders {

  def loadBostonDataset: Dataset = {
    val data = loadDatasetFromResources("boston_housing_prices")
    (data(::, 0 to -2), data(::, -1))
  }

  def loadBreastCancerDataset: Dataset = {
    val data = loadDatasetFromResources("breast_cancer")
    (data(::, 0 to -2), data(::, -1))
  }

  def loadIrisDataset: Dataset = {
    val data = loadDatasetFromResources("iris")
    (data(::, 0 to -2), data(::, -1))
  }

  /** An artificial dataset with a Poisson target variable. */
  def loadHighSchoolTestDataset: Dataset = {
    val data = loadDatasetFromResources("high_school_test")
    (data(::, 0 to -2), data(::, -1))
  }

  private[data] def loadDummyCsvReadingDataset: Dataset = {
    val data = loadDatasetFromResources("dummy_csv_reading")
    (data(::, 0 to -2), data(::, -1))
  }

  private def loadDatasetFromResources(datasetName: String): DenseMatrix[Double] =
    loadCsvDataset(getResourceFile(s"/datasets/$datasetName.csv"))

  private def getResourceFile(path: String): File = {
    val resourceUrl = getClass.getResource(path)
    val file = if (resourceUrl.toString.startsWith("jar:"))
      // reads file from JAR
      readResourceFileWithinJar(path)
    else
      // reads file when using IDE
      new File(resourceUrl.getFile)
    if (file != null && !file.exists)
      throw new RuntimeException(s"Error: File $file not found!")
    file
  }

  private def readResourceFileWithinJar(path: String): File = {
    val inputStream = getClass.getResourceAsStream(path)
    val tempFile = File.createTempFile("tempfile", ".tmp")
    val outputStream = new FileOutputStream(tempFile)

    val buffer = new Array[Byte](130 * 1024)
    Iterator.continually(inputStream.read(buffer)).takeWhile(_ != -1).foreach { bytesRead =>
      outputStream.write(buffer, 0, bytesRead)
      outputStream.flush()
    }

    inputStream.close()
    outputStream.close()

    tempFile.deleteOnExit()
    tempFile
  }
}
