//package scripts
//import slideshow.Projector
//import java.io.File
//import utils.extensions.*superGC
//
///**
// * Measures the approximate size of an object in memory, given a Class which
// * has a no-argument constructor.
// */
//fun main(aArguments: Array<String>) {
//  val root = File(path)
//
//  val startMemoryUse = scripts.currentMemory()
//  val p = slideshow.Projector(root)
//  Thread.sleep(10000)
//  val endMemoryUse = scripts.currentMemory()
//  p.exit()
//  println("Approximate size of slideshow.Projector :${endMemoryUse - startMemoryUse}")
//}
//
//fun currentMemory(): Long {
//  utils.extensions.superGC()
//  val totalMem = Runtime.getRuntime().totalMemory()
//  utils.extensions.superGC()
//  val freeMem = Runtime.getRuntime().freeMemory()
//  return totalMem - freeMem
//}
