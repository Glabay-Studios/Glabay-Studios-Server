package io.xeros.util.tools

import com.google.gson.GsonBuilder
import io.xeros.util.ProgressbarUtils
import io.xeros.util.tools.wiki.MediaWiki
import io.xeros.util.tools.wiki.MediaWikiTemplate
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.Pair
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File


data class WorldItemData(
    val id : Int = 0,
    val location : Location = Location(0,0,0),
    val amt : Int = 0,
    val respawn : Int = 0,
    val description : String
)

data class Location(val x : Int, val y : Int , val z : Int)

object WorldItemsDumper {

    private val wiki = MediaWiki("https://oldschool.runescape.wiki")

    var totalSpawns = 0
    var totalDone = 0
    var total = 0

    var gson = GsonBuilder().setPrettyPrinting().create()

    val itemSpawns : MutableList<WorldItemData> = emptyList<WorldItemData>().toMutableList()

    fun init() {

        val doc = Jsoup.connect("https://oldschool.runescape.wiki/w/Item_spawn").get().html()

        val filter = StringUtils.substringBetween(doc,"A</span></h3>", "<!-- ").replace("<ul>","").replace("</a></li>","").replace("</ul>","").replace("</div>","").replace("          ","").replace("<li><a href=\"","")
        val extractName = StringUtils.substringsBetween(filter,"/w/"," title=")

        total = extractName.size

        val testLink = ""


        val dataProgress = ProgressbarUtils.progress("Retrieving Ground Items", extractName.size.toLong())

        if(testLink.isNotEmpty()) {
            getItem(Jsoup.connect(testLink).get())
        } else {
            extractName.forEach {
                val link = "https://oldschool.runescape.wiki/w/${it.replace("\"","")}"
                val document = Jsoup.connect(link).get()
                getItem(document)
                dataProgress.step()
            }
            dataProgress.close()
        }


        val writeProgress = ProgressbarUtils.progress("Writing ground items", 1)

        File("spawnsFull.json").writeText(gson.toJson(itemSpawns))
        writeProgress.step()
        writeProgress.close()
        println("Total Spawns: $totalSpawns")

    }

    private fun getItem(doc : Document) {

        val itemString = StringUtils.substringBetween(doc.html(),"Item ID","</td>")
        val itemID: Int
        try {
            itemID = if(!itemString.contains(",")){
                StringUtils.substringBetween(doc.html(),"Item ID","</td>").filter { it.isLetterOrDigit() }.replace("thtdcolspan13","").replace("dataattrparamid","").toInt()
            } else {
                StringUtils.substringBetween(doc.html(),"Item ID","</td>").replace("<td colspan=13>","").replace("dataattrparamid","").replace(Regex("[^|$£0-9,]"),"").split(",")[0].toInt()
            }
        }catch (e : Exception) {
            println("[FAILED] Link : ${doc.location()}")
            return
        }

        doc.select("a[href][title]").forEach { it ->
            val title = it.attr("title")
            if (title.contains("Edit section: spawn",true)) {
                val link = it.attr("href").replace("&veaction=edit","&action=edit")

                if (link.contains("section",true)) {

                    val editLink = "https://oldschool.runescape.wiki/${link}"
                    val document = Jsoup.connect(editLink).get()

                    val body = document.text().replace("{{FloorNumber|uk=1}}","")
                        .replace("{{FloorNumber|uk=2}}","")
                        .replace("{{FloorNumber|uk=3}}","")
                        .replace("{{FloorNumber|uk=4}}","")


                    StringUtils.substringsBetween(body,"{{ItemSpawnLine", "}}").forEach {
                        val name = StringUtils.substringBetween(it, "location=[[", "]] - ")?.replace(" ", "ffffff")?.filter { it.isLetterOrDigit() }?.replace("ffffff", " ") ?: "UNDEFINED"
                        val plane = Regex("plane=(\\d)").find(it)?.value?.split("=")?.get(1)?.toInt() ?: 0

                        var result = it.replace("plane=1","").replace("plane=2","").replace("plane=3","").replace("\n","").replace("respawn:","$").replace("qty:","£")

                        if(result.contains("|mapID=")) {
                            val id = StringUtils.substringBetween(result,"|mapID=","|")
                            result = result.replace("|mapID=${id}","")
                        }

                        result = result.replace(Regex("[^|$£0-9,]"),"")

                        result = result.replace("£","qty:").replace("$","respawn: ")
                            .replace("|1|","")
                            .replace("|,|","")
                            .replace("|2|","")
                            .replace("|4|","")
                            .replace(",27|","")
                            .replace("||0","")
                            .replace("||","")
                            .replace("||||","").replace("|||","").replace("notes:(on table),","")


                        try {
                            result.split("|").forEach {
                                if(it.isNotEmpty() && itemID != -1) {
                                    val data = it.split(",")
                                    if (data.size != 1) {
                                        if (data[0].isNotEmpty() && data[1].isNotEmpty()) {
                                            val x = data[0].toInt()
                                            val y = data[1].toInt()

                                            var qty = 1
                                            var respawn = 0

                                            if (it.contains("qty:") && it.contains("respawn:")) {
                                                qty = data[2].replace("qty:", "").toInt()
                                                if (data[3].filter { it.isDigit() } != "") {
                                                    respawn = data[3].filter { it.isDigit() }.toInt()
                                                }
                                            } else if (it.contains("qty:")) {
                                                qty = data[2].filter { it.isDigit() }.toInt()
                                            } else if (it.contains("respawn:")) {
                                                respawn = data[2].filter { it.isDigit() }.toInt()
                                            }

                                            if(respawn == 0) {
                                                respawn = getRespawnTime(itemID)
                                            }

                                            val def = WorldItemData(itemID, Location(x, y, plane), qty, respawn,name)

                                            if(!itemSpawns.contains(def)) {
                                                itemSpawns.add(def)
                                            }

                                        }
                                    }
                                }
                            }

                        } catch (e : Exception) {
                            e.printStackTrace()
                            println("[FAILED] Link : ${doc.location()}")
                        }
                    }
                }
            }
        }


        totalDone++

    }

    fun getRespawnTime(itemID : Int) : Int {
        val pageData: Pair<String, String>? = wiki.getSpecialLookupData("item", itemID, 0)
        if (pageData == null)
        {
            System.out.println("UNABLE TO FIND ITEM: $itemID")
            return 0
        }

        val data = pageData.right

        if (data.isNullOrEmpty()) {
            println("NO Info Box")
            return 0
        }

        val base = MediaWikiTemplate.parseWikitext("Infobox Item", data)

        return try {
            base!!.getInt("respawn")
        } catch (e : Exception) {
            return 0
        }
    }

}

fun main() {
    WorldItemsDumper.init()
}