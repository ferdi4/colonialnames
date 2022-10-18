package eu.dalaran.colonialnames

data class Result(
	val name: String,
	val locations: List<ResultMatch>
)

data class ResultMatch(
	val matches: Int,
	val location: Location,
)