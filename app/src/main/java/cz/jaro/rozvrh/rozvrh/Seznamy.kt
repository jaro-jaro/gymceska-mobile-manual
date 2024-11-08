package cz.jaro.rozvrh.rozvrh

object Seznamy {
    val dny1Pad = listOf(
        "Pondělí",
        "Úterý",
        "Středa",
        "Čtvrtek",
        "Pátek",
    )
    val dny4Pad = listOf(
        "v pondělí",
        "v úterý",
        "ve středu",
        "ve čtvrtek",
        "v pátek",
    )
    val hodiny1Pad = listOf(
        "0. hodina",
        "1. hodina",
        "2. hodina",
        "3. hodina",
        "4. hodina",
        "5. hodina",
        "6. hodina",
        "7. hodina",
        "8. hodina",
        "9. hodina",
    )
    val hodiny4Pad = listOf(
        "0. hodinu",
        "1. hodinu",
        "2. hodinu",
        "3. hodinu",
        "4. hodinu",
        "5. hodinu",
        "6. hodinu",
        "7. hodinu",
        "8. hodinu",
        "9. hodinu",
    )
}

val Seznamy.dny
    get() = dny1Pad.mapIndexed { index, it ->
        Vjec.DenVjec(
            jmeno = it,
            zkratka = it.take(2),
            index = index + 1,
        )
    }
val Seznamy.hodiny
    get() = hodiny1Pad.mapIndexed { index, it ->
        Vjec.HodinaVjec(
            jmeno = it,
            zkratka = it.split(".")[0],
            index = index + 1,
        )
    }
