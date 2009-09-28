package org.riedelcastro.thebeast.apps


import alchemy.MLN
import env.TheBeastEnv
import java.io.{InputStreamReader, FileReader}
/**
 * @author Sebastian Riedel
 */

object EntityResolution extends TheBeastEnv {
  def main(args:Array[String]):Unit = {
    //load entity resolution mln
    println(System.getProperty("user.dir"))
    println(System.getProperty("java.class.path"))
    val mln = new MLN
    mln.loadMLN(new InputStreamReader(getClass.getResourceAsStream("/alchemy/er/er-bnct.mln")))
    println(mln.getFormulae.mkString("\n"))
    //mln.loadMLN(new FileReader("resources/alchemy/er/er-bnct.mln"))
    //val data = args.map(mln.loadAtoms(new FileReader(_)))
    null
  }
}