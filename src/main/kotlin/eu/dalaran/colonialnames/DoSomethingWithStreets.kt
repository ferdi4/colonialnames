package eu.dalaran.colonialnames

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DoSomethingWithStreets : ApplicationRunner {
	@OptIn(ExperimentalSerializationApi::class)
	override fun run(args: ApplicationArguments?) {

		val locations = Json.decodeFromStream<List<List<String>>>(ownResource("streets.json"))
			.map {
				Location(
					street = it[3],
					streetLowercase = it[3].lowercase(),
					city = it[4]
				)
			}

		val names = Json.decodeFromStream<List<String>>(ownResource("names.json")).map {
			val nameLowercase = it.lowercase()
			Name(
				name = it,
				nameLowercase,
				nameParts = nameLowercase.split(" ")

			)
		}


		val result = names.map { name ->
			Result(name.name, locations =
			locations.map {
				val matches = name.nameParts.filter { np -> it.streetLowercase.contains(np) }.size
				ResultMatch(
					matches = matches,
					location = it
				)
			}.filter { it.matches >= 1 }

			)

		}

	}
}
