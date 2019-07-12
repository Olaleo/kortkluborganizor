package model

class PlayDate(val date: String, val week: Int, val players: List<String>, val potentialPlayers: List<String>) {
    var host = ""
}


fun MutableList<PlayDate>.playDatesPerPlayer(): List<Pair<String, Int>> {
    val map = HashMap<String, Int>()
    this.players().forEach { map[it] = 0 }
    this.forEach { it.players.forEach { map[it] = map[it]!! + 1 } }
    return map.toList().sortedBy { it.second }
}

fun MutableList<PlayDate>.players(): List<String> {
    return this.flatMap { it.players }.associate { Pair(it, 0) }.keys.toList()
}

fun MutableList<PlayDate>.hostList(): List<String> {
    return this.map { it.host }
}

fun MutableList<PlayDate>.numberOfPotentialHostings(): MutableMap<String, Int> {
    val map = this.players().associate { Pair(it,0) }.toMutableMap()
    for(it in this){
        if (it.host != ""){
            continue
        }
        it.players.forEach {player -> map[player] = map[player]!! + 1 }
    }
    return map
}


fun MutableList<PlayDate>.numberOfHostings(): MutableMap<String, Int> {
    val map = this.players().associate { Pair(it,0) }.toMutableMap()
    for(it in this){
        if (it.host != ""){
           map[it.host] = map[it.host]!! + 1
        }
    }
    return map
}