
import com.google.gson.GsonBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import model.Attendance
import model.AttendanceType
import model.AttendanceType.*
import model.PotentialPlayDate
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


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
            val attendances = mutableListOf<Attendance>()
            for (i in 1 until it.size) {
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
            potentialPlayDates.add(PotentialPlayDate(date, attendances))
        }
        potentialPlayDates.forEach { it.attendances = it.attendances.filter { it.type != AttendanceType.Cannot } }
        potentialPlayDates = potentialPlayDates.filter { it.attendances.size >= 4 }.toMutableList()

        val actualPlayDates = potentialPlayDates.filter { it.attendances.size == 4 }.toMutableList()
        potentialPlayDates.removeAll(actualPlayDates)


        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(potentialPlayDates)
        println(json)


        val listOfDates = mutableListOf<MutableList<String>>()
        for (i in 1 until records[0].size) {
            listOfDates.add(mutableListOf(records[0][i]))
        }
        for (i in 1 until records.size) {
            records[i].forEachIndexed { index, s ->
                if (s != "0" && s.length == 1) {
                    listOfDates[index - 1]
                            .add(
                                    records
                                            [i]
                                            [0])
                }
            }
        }
        //listOfDates.forEach { println(it) }

//        val gson = GsonBuilder().setPrettyPrinting().create()
//        val json = gson.toJson(listOfDates)
//        //println(json)

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