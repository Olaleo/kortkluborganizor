import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import model.*
import model.AttendanceType.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

fun main(args: Array<String>?) {

    println(args!![0])
    var fileReader: BufferedReader? = null
    var csvReader: CSVReader? = null

    try {
        fileReader = BufferedReader(FileReader(args[0]))
        csvReader = CSVReaderBuilder(fileReader).build()

        val records = csvReader.readAll()
        for (_record in records) {
            var s = ""
            _record.forEach {
                if (it != "") {
                    s += "$it | "
                }
            }
            println(s)
        }

        val players = HashMap<Int, String>()
        var potentialPlayDates = mutableListOf<PotentialPlayDate>()
        records[0].forEachIndexed { i, s ->
            players[i] = s
        }
        records.removeAt(0)
        records.forEach {
            val date = it[0]
            val week = it[1].toInt()
            val attendances = mutableListOf<Attendance>()
            for (i in 2 until it.size) {
                val a = players[i]?.let { it1 ->
                    Attendance(it1, when {
                        it[i] == "0" -> {
                            Cannot
                        }
                        it[i] == "1" -> {
                            Can
                        }
                        it[i] == "0.5" -> {
                            Maybe
                        }
                        else -> {
                            Cannot
                        }
                    })
                }
                a?.let { it1 -> attendances.add(it1) }
            }
            potentialPlayDates.add(PotentialPlayDate(date, week, attendances))
        }
        potentialPlayDates.forEach {
            it.attendances = it.attendances.filter { it.type != Cannot }.toMutableList()
            potentialPlayDates = potentialPlayDates.filter { it.attendances.size >= 4 }.toMutableList()
        }

        val actualPlayDates = potentialPlayDates.filter { it.attendances.size == 4 }.map { PlayDate(it.date, it.week, it.attendances.map { it.name }, listOf()) }.toMutableList()

        potentialPlayDates.removeIf { actualPlayDates.map { it.date }.contains(it.date) }


        potentialPlayDates.forEach {

            val weightedPlayers = actualPlayDates.playDatesPerPlayer()
            val reservePlayers = mutableListOf<String>()
            while (it.attendances.size > 4) {

                if (it.attendances.removeIf { it.name == weightedPlayers.last().first }) {
                    reservePlayers.add(weightedPlayers.last().first)
                } else {
                    if (it.attendances.removeIf { it.name == weightedPlayers[weightedPlayers.size - 2].first }) {
                        reservePlayers.add(weightedPlayers[weightedPlayers.size - 2].first)
                    }
                }
            }
            actualPlayDates.add(PlayDate(it.date, it.week, it.attendances.map { it.name }, reservePlayers.reversed()))
        }



        actualPlayDates.sortBy { it.week }


        while (true) {
            var index = -1
            for (actualPlayDate in actualPlayDates) {
                if (actualPlayDates.indexOf(actualPlayDate) == 0 || actualPlayDate.week - actualPlayDates[actualPlayDates.indexOf(actualPlayDate) - 1].week > 2) {

                } else {
                    index = actualPlayDates.indexOf(actualPlayDate)
                    break
                }
            }
            if (index == -1) {
                break
            } else {
                actualPlayDates.removeAt(index)
                index = -1
            }
        }



        while (actualPlayDates.numberOfPotentialHostings().values.filter { it != 0 }.isNotEmpty()){
            val potential = actualPlayDates.numberOfPotentialHostings()
            val actual = actualPlayDates.numberOfHostings()
            val allowedToHost = actual.
        }




        val fileWriter = FileWriter(args[0].split(".")[0] + "OUTPUT" + ".csv")

        val CSV_HEADER = "date,week,player1,player2,player3,player4,player5?,player6?"

        val playTimes = actualPlayDates.players().associate { s ->
            var counter = 0
            counter = actualPlayDates.filter { it.players.contains(s) }.size
            Pair(s, counter)
        }

        fileWriter.append(CSV_HEADER)
        fileWriter.append('\n')

        for (playDate in actualPlayDates) {
            fileWriter.append(playDate.date)
            fileWriter.append(',')
            fileWriter.append(playDate.week.toString())
            fileWriter.append(',')
            playDate.players.forEach {
                fileWriter.append(it)
                fileWriter.append(',')
            }
            playDate.potentialPlayers.forEach {
                fileWriter.append(it)
                fileWriter.append(',')
            }
            fileWriter.append('\n')
        }
        fileWriter.append('\n')
        fileWriter.append('\n')

        playTimes.forEach { t, u ->
            fileWriter.append(t)
            fileWriter.append(',')
            fileWriter.append(u.toString())
            fileWriter.append('\n')

        }

        fileWriter.flush()
        fileWriter.close()

    } catch (e: Exception) {
        println("Reading CSV Error!")
        e.printStackTrace()
    } finally {
        try {
            fileReader!!.close()
            csvReader!!.close()
        } catch (e: IOException) {
            println("Closing fileReader/csvParser Error!")
            e.printStackTrace()
        }
    }
}