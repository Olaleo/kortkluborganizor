package model

class PlayDate(val date: String, val week: Int, val players: List<String>, val potentialPlayers: List<String>) {
}


fun MutableList<PlayDate>.playDatesPerPlayer(): List<Pair<String, Int>> {
    val map = HashMap<String, Int>()
    this.players().forEach { map[it] = 0 }
    this.forEach { it.players.forEach { map[it] = map[it]!! + 1 } }
    return map.toList().sortedBy { it.second }
}

fun MutableList<PlayDate>.players(): List<String> {
    val list = mutableListOf<Pair<String, Int>>()
    return this.flatMap { it.players }.associate { Pair(it, 0) }.keys.toList()
}