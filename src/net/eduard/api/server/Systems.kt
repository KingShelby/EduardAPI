package net.eduard.api.server


object Systems {

    @JvmStatic
    var cashSystem: CashSystem? = null
    @JvmStatic
    var soulSystem: SoulSystem? = null

    @JvmStatic
    var scoreSystem: ScoreSystem? = null
    @JvmStatic
    var partySystem: PartySystem? = null
    @JvmStatic
    var generatorSystem: GeneratorSystem? = null
}
