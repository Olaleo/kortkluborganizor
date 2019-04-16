package model

data class PotentialPlayDate(val date: String, val week: Int, var attendances: MutableList<Attendance>) {
}