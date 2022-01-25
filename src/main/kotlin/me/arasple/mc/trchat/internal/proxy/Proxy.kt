package me.arasple.mc.trchat.internal.proxy

import me.arasple.mc.trchat.api.TrChatFiles
import me.arasple.mc.trchat.internal.proxy.bungee.Bungees
import me.arasple.mc.trchat.internal.proxy.velocity.Velocity
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getProxyPlayer
import taboolib.module.lang.sendLang

/**
 * Proxy
 * me.arasple.mc.trchat.internal.proxy
 *
 * @author wlys
 * @since 2021/8/21 13:24
 */
@PlatformSide([Platform.BUKKIT])
object Proxy {

    var isEnabled = false

    val platform by lazy {
        if (Bukkit.getServer().spigot().config.getBoolean("settings.bungeecord", false)) {
            TrChatFiles.settings["GENERAL.PROXY"] = "BUNGEE"
        }
        when (val p = TrChatFiles.settings.getString("GENERAL.PROXY", "NONE")!!.uppercase()) {
            "NONE" -> {
                console().sendLang("Plugin-Proxy-None")
                Platform.UNKNOWN
            }
            "BUNGEE" -> {
                Platform.BUNGEE
            }
            "VELOCITY" -> {
                Platform.VELOCITY
            }
            else -> error("Unsupported proxy $p.")
        }
    }

    fun init() {
        when (platform) {
            Platform.BUNGEE -> Bungees.init()
            Platform.VELOCITY -> Velocity.init()
            else -> return
        }
    }

    fun sendBukkitMessage(player: Player, vararg args: String) {
        when (platform) {
            Platform.BUNGEE -> Bungees.sendBukkitMessage(player, *args)
            Platform.VELOCITY -> Velocity.sendBukkitMessage(player, *args)
            else -> return
        }
    }

    fun sendProxyLang(player: Player, target: String, node: String, vararg args: String) {
        if (!isEnabled || Bukkit.getPlayerExact(target) != null) {
            getProxyPlayer(target)?.sendLang(node, *args)
        } else {
            when (platform) {
                Platform.BUNGEE -> sendBukkitMessage(player, "SendLang", target, node, *args)
                Platform.VELOCITY -> sendBukkitMessage(player, "SendLang", target, node, *args)
                else -> return
            }
        }
    }
}

fun Player.sendBukkitMessage(vararg args: String) {
    Proxy.sendBukkitMessage(this, *args)
}