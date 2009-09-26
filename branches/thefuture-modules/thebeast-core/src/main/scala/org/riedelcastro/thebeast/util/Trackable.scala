package org.riedelcastro.thebeast.util


import collection.mutable.ArrayBuffer

/**
 * @author Sebastian Riedel
 */


trait Tracker {
  def marked(text:String, start:Boolean) 
}

class PrintingTracker extends Tracker {
  def marked(text: String, start: Boolean) = {
    print(System.currentTimeMillis + " ")
    println(if (start) "Start: " + text else "End: " + text)
  }
}

class TimingTracker extends Tracker {
  private val starts = new ArrayBuffer[Long]
  private val texts = new ArrayBuffer[String]
  def marked(text: String, start: Boolean) = {
    if (start) {
      starts += System.currentTimeMillis
      texts += text
    } else {
      val duration = System.currentTimeMillis - starts.remove(starts.size - 1)
      val text = texts.remove(texts.size - 1)
      println("Track " + text + " took " + duration + "ms")
    }
  }
}



object Trackers extends ArrayBuffer[Tracker]{
  //this += new TimingTracker 


}


trait Trackable {
  def mark(text:String, start:Boolean) = {for (t <- Trackers) t.marked(text,start)}
  def |**(text:String) = mark(text,true);
  def **|(text:String) = mark(text,false);
  def **| = mark("NO MESSAGE",false);

}