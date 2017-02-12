package portweather


import redis.clients.jedis.Jedis
import java.util.*

var m_con: Jedis = Jedis("localhost")


/**
 *
 */
fun open(): Boolean {

    try {
        m_con.connect()

        return m_con != null
    } catch (e: Exception) {
        return false
    }

}   // open

public fun gendata(date: String) {

    if (open()) {

        var runner: Int = 0


        while (runner < 1000) {


            val randomPort: String = Random().nextInt(65000).toString()
            val randomHour: String = Random().nextInt(23).toString()
            val randomOccurence: String = Random().nextInt(100).toString()

            m_con.set(date + ":" + randomHour + ":" + randomPort, randomOccurence)

            runner++;
        }


    }

}


fun getAvarage() {

    //
    // addiere einzelwerte und teile durch anzahl einzelwerte
    //

    //
    // Standardabweichung Wurzel aus Summe (Wert - Mittelwert) zum Quadrat / Stichprobengröße
    //

    var runner: Int = 0

    while (runner < 65000) {

        // get all keys
        // calculate hourly avarage except for current value
        // store avarage and used values

        var keys = m_con.keys("*:" + runner)
        var counter: Int = 0
        var sumPerPort: Int = 0
        var standardAbweichung: Int = 0
        var average: Int = 0

        var it = keys.iterator()
        while (it.hasNext()) {
            val s = it.next()
            val number: Int = m_con.get(s).toInt()
            sumPerPort += number

            //println("Key: " + s + " Value: " + number)

            counter++
        }


        if (counter != 0) {

            average = sumPerPort / counter


            it = keys.iterator()
            while (it.hasNext()) {
                val s = it.next()
                val number: Int = m_con.get(s).toInt()

                val equation : Double = number.toDouble() - average.toDouble()
                standardAbweichung += (equation * equation).toInt()

            }

            standardAbweichung = (Math.sqrt(standardAbweichung.toDouble()) / counter).toInt()

            println("Number of keys for port " + runner + " found: " + counter + " with average value of " + average + " and standard abweichung " + standardAbweichung)

        }


        runner++;
    }

}


fun main(args: Array<String>) {
    println("Hello World!")

    gendata("2017:01:10")
    gendata("2017:01:11")
    gendata("2017:01:12")

    getAvarage()

}