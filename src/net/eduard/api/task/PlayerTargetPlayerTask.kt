package net.eduard.api.task

import net.eduard.api.EduardAPI
import net.eduard.api.lib.event.PlayerTargetPlayerEvent
import net.eduard.api.lib.kotlin.call
import net.eduard.api.lib.modules.Mine
import org.bukkit.scheduler.BukkitRunnable

class PlayerTargetPlayerTask : BukkitRunnable() {
    override fun run() {
        for (p in Mine.getPlayers()) {
            try {
                val event = PlayerTargetPlayerEvent(
                        Mine.getTarget(p, Mine.getPlayerAtRange(p.location, 100.0)), p)
               event.call()
            } catch (ex: Exception) {
                EduardAPI.Companion.instance.log("Erro ao causar o Evento PlayerTargetEvent ")
                ex.printStackTrace()
            }

        }
    }
}
