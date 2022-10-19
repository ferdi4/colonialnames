package eu.dalaran.colonialnames

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.File

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
			val nameLowercase = it.lowercase().replace("von", "").replace("-", " ")

			val withUmlauts = nameLowercase.split("\\s+".toRegex())

			val withoutUmlauts = nameLowercase
				.replace("\u00f6", "oe")
				.replace("\u00e4", "ae")
				.replace("\u00fc", "ue")
				.replace("\u00df", "ss")
				.split("\\s+".toRegex())

			val nameParts = withUmlauts.plus(withoutUmlauts).groupBy { t -> t }.map { z -> z.key }

			Name(
				name = it,
				nameLowercase,
				nameParts = nameParts
			)
		}

		val result = names.map { name ->
			Result(name.name, searchElements = name.nameParts, locations =
			locations.map {
				val matches = name.nameParts.filter { np -> it.streetLowercase.contains(np) }.size
				ResultMatch(
					matches = matches,
					location = it
				)
			}.filter {
				if (name.nameParts.size == 1) {
					it.matches == 1
				} else {
					it.matches >= 2
				}
			}
			)
		}

		val mapper = ObjectMapper()
		val resultText = mapper.writeValueAsString(result)
		File("colonial-names.json").writeText(resultText)
	}
}
