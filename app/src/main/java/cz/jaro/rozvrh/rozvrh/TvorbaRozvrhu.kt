package cz.jaro.rozvrh.rozvrh

import cz.jaro.rozvrh.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TvorbaRozvrhu {

    private val dny = listOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne", "Rden", "Pi")

    suspend fun vytvoritRozvrhPodleJinych(
        vjec: Vjec,
        repo: Repository,
        tridy: List<Vjec.TridaVjec>,
    ): Tyden = withContext(Dispatchers.IO) {
        require(vjec is Vjec.MistnostVjec || vjec is Vjec.VyucujiciVjec)

        val novaTabulka = MutableList(6) { MutableList(11) { mutableListOf<Bunka>() } }

        tridy.map { trida ->

            val result = repo.rozvrh(trida.jmeno)!!

            result.forEachIndexed trida@{ i, den ->
                den.forEachIndexed den@{ j, hodina ->
                    hodina.forEach hodina@{ bunka ->
                        if (i == 0 || j == 0) {
                            if (novaTabulka[i][j].isEmpty()) novaTabulka[i][j] += bunka
                            return@hodina
                        }
                        if (bunka.ucitel.isEmpty() || bunka.predmet.isEmpty()) {
                            return@hodina
                        }
                        val zajimavaVec = when (vjec) {
                            is Vjec.VyucujiciVjec -> bunka.ucitel.split(",").first()
                            is Vjec.MistnostVjec -> bunka.ucebna
                            else -> throw IllegalArgumentException()
                        }
                        if (zajimavaVec == vjec.zkratka) {
                            novaTabulka[i][j] += bunka.copy(tridaSkupina = "${trida.zkratka} ${bunka.tridaSkupina}".trim()).let {
                                when (vjec) {
                                    is Vjec.VyucujiciVjec -> it.copy(ucitel = "")
                                    is Vjec.MistnostVjec -> it.copy(ucebna = "")
                                    else -> throw IllegalArgumentException()
                                }
                            }
                        }
                    }
                }
            }
        }
        novaTabulka.forEachIndexed { i, den ->
            if (den.getOrNull(1)?.singleOrNull()?.typ == TypBunky.Volno) return@forEachIndexed
            den.forEachIndexed { j, hodina ->
                hodina.ifEmpty {
                    novaTabulka[i][j] += Bunka.prazdna
                }
            }
        }
        novaTabulka[0][0][0] = novaTabulka[0][0][0].copy(predmet = vjec.zkratka)
        novaTabulka
    }

    suspend fun vytvoritSpecialniRozvrh(
        vjec: Vjec,
        repo: Repository,
        tridy: List<Vjec.TridaVjec>,
    ): Tyden = withContext(Dispatchers.IO) {
        require(vjec is Vjec.DenVjec || vjec is Vjec.HodinaVjec)

        val vyska = when (vjec) {
            is Vjec.DenVjec -> tridy.count()
            is Vjec.HodinaVjec -> 5
            else -> throw IllegalArgumentException()
        }
        val sirka = when (vjec) {
            is Vjec.DenVjec -> 10
            is Vjec.HodinaVjec -> tridy.count()
            else -> throw IllegalArgumentException()
        }

        val novaTabulka = MutableList(vyska + 1) { MutableList(sirka + 1) { mutableListOf<Bunka>() } }

        tridy.map { trida ->

            val result = repo.rozvrh(trida.jmeno)!!

            if (vjec is Vjec.DenVjec) {
                novaTabulka[tridy.indexOf(trida) + 1][0] = mutableListOf(Bunka.prazdna.copy(predmet = trida.zkratka))
                result[vjec.index].forEachIndexed den@{ j, hodina ->
                    novaTabulka[0][j] = result[0][j].toMutableList()
                    hodina.forEach hodina@{ bunka ->
                        if (j == 0) return@hodina

                        novaTabulka[tridy.indexOf(trida) + 1][j] += bunka
                    }
                }
            }

            if (vjec is Vjec.HodinaVjec) {
                novaTabulka[0][tridy.indexOf(trida) + 1] = mutableListOf(Bunka.prazdna.copy(predmet = trida.zkratka))
                result.forEachIndexed trida@{ i, den ->
                    novaTabulka[i][0] = result[i][0].toMutableList()
                    den.drop(1).singleOrGet(vjec.index - 1).forEach hodina@{ bunka ->
                        if (i == 0) return@hodina

                        novaTabulka[i][tridy.indexOf(trida) + 1] += bunka
                    }
                }
            }

            novaTabulka[0][0] = result[0][0].toMutableList()
        }
        novaTabulka.forEachIndexed { i, den ->
            if (den.getOrNull(1)?.singleOrNull()?.typ == TypBunky.Volno) return@forEachIndexed
            den.forEachIndexed { j, hodina ->
                hodina.ifEmpty {
                    novaTabulka[i][j] += Bunka.prazdna
                }
            }
        }
        novaTabulka[0][0][0] = novaTabulka[0][0][0].copy(predmet = vjec.zkratka)
        novaTabulka
    }
}

private fun <E> List<E>.singleOrGet(index: Int) = singleOrNull() ?: get(index)