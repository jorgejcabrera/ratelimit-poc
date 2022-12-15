package delivery

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")
    ServiceApp().run(*args)

}