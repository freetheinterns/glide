package scripts


fun main(args: Array<String>) {
  var hi: String? = "wo"
  if (hi ?: 0 == 2)
    println("working")
  println("end")
}
