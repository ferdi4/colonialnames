package eu.dalaran.colonialnames

import java.io.InputStream

fun Any.ownResource(name: String): InputStream = javaClass.getResourceAsStream(name)!!
