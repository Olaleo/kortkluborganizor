
import com.google.gson.GsonBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
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
        listOfDates.forEach { println(it) }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(listOfDates)
        println(json)

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