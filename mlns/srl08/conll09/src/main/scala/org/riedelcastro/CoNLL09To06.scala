package org.riedelcastro

import scala.io.Source
import java.io.{File, FileOutputStream}
import java.util.ArrayList


object SplitCoNLLData {
  def main(args: Array[String]) {
    val from = args(0).toInt
    val to = args(1).toInt
    val out = new java.io.PrintStream(System.out, true, "UTF-8")
    var current = 0
    for (line: String <- Source.fromInputStream(System.in).getLines) {
      if (current >= from && current < to)
        out.print(line)
      if (line.trim == ""){
        current += 1
      }
    }
    out.close

  }
}

/**
 * CoNLL 09
 * ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs
 * CoNLL 06
 * ID FORM LEMMA CPOS POS FEATS HEAD DEPREL PHEAD PDEPREL 
 * @author Sebastian Riedel
 */
object CoNLL09To06 {
  def main(args: Array[String]) {
    //create folds
    val src = Source.fromInputStream(System.in)
    val out = new java.io.PrintStream(System.out, true, "UTF-8")
    var sentences = Array[String]()
    val sentence = new scala.StringBuilder()
    val processors = if (args.length == 3 && args(2) == "guess")
      Array(Col(0), Col(1), Col(3), Coarse(Col(5)), Col(5), Col(7), Col(9), Col(11), Text("_"), Text("_"))
    else
      Array(Col(0), Col(1), Col(3), Coarse(Col(5)), Col(5), Col(7), Col(8), Col(10), Text("_"), Text("_"))
    for (line: String <- src.getLines) {
      val split = line.split("\\s+");
      if (split.length == 0) {
        sentences = sentences ++ Array(sentence.toString)
        sentence.setLength(0)
      } else {
        sentence.append(separate(LineProcessor(split,processors),
          "\t")).append("\n")
      }
    }
    val fileName = args(0)
    val splits = args(1).toInt
    if (splits > 0) {
      val indices = splitIndices(0 until sentences.length, splits)
      val splitSize = sentences.length / splits
      for (split <- 0 until splits) {
        val train = new java.io.PrintStream(new FileOutputStream(fileName + "." + split + ".conll06.train"), true, "UTF-8")
        val test = new java.io.PrintStream(new FileOutputStream(fileName + "." + split + ".conll06.test"), true, "UTF-8")
        for (i <- 0 until splits if i != split) {
          for (sentenceNr <- indices(i))
            train.println(sentences(sentenceNr))
        }
        for (sentenceNr <- indices(split))
          test.println(sentences(sentenceNr))
      }
    }
    for (s <- sentences) {
      out.println(s)
    }
  }



  def splitIndices(range: Seq[Int], splits: Int): List[Seq[Int]] = {
    if (splits == 1) List(range) else {
      val splitSize = range.length / splits
      List(range.take(splitSize)) ::: splitIndices(range.drop(splitSize), splits - 1)
    }

  }

  def separate(items: Seq[String], separator: String): String = {
    return if (items.length == 0) "" else items.drop(1).foldLeft(items(0)){(result, item) => result + separator + item}
  }

  def subsequence[T](list: Seq[T], indices: Seq[Int]): Seq[T] = {
    return indices.foldLeft(List[T]()){(result, index: Int) => list(index) :: result}
  }

}

object LineProcessor {
  def apply(cols: Seq[String], processors: Iterable[TokenProcessor]): Seq[String] = {
    return processors.foldRight(List[String]()){(processor, result) => processor(cols) :: result}
  }
}

abstract class TokenProcessor {
  def apply(cols: Seq[String]): String
}

case class Coarse(delegate: TokenProcessor) extends TokenProcessor {
  def apply(cols: Seq[String]): String = {
    delegate(cols).substring(0, 1)
  }
}

case class Col(val col: Int) extends TokenProcessor {
  def apply(cols: Seq[String]): String = {
    return cols(col)
  }
}

case class Text(val text: String) extends TokenProcessor {
  def apply(cols: Seq[String]): String = text
}



