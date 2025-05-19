package com.projectlumina.luna.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mucheng.mucute.relay.MuCuteRelay
import com.mucheng.mucute.relay.address.MuCuteAddress
import com.mucheng.mucute.relay.listener.AutoCodecPacketListener
import com.mucheng.mucute.relay.listener.GamingPacketHandler
import com.mucheng.mucute.relay.listener.OfflineLoginPacketListener
import com.mucheng.mucute.relay.listener.OnlineLoginPacketListener
import com.mucheng.mucute.relay.util.captureGamePacket
import com.projectlumina.luna.AccountManager
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
object Service {

    private var muCuteRelay: MuCuteRelay? = null

    var isActive by mutableStateOf(false)

    fun toggle() {
        if (isActive) {
            off()
        } else {
            on()
        }
    }

    private fun on() {
        if (isActive) return

        isActive = true
        thread(name = "MuCuteRelayThread", priority = Thread.MAX_PRIORITY) {
            runCatching {
                val selectedAccount = AccountManager.selectedAccount
                muCuteRelay = captureGamePacket(
                    remoteAddress = MuCuteAddress("play.lbsg.net", 19132)
                ) {
                    listeners.add(AutoCodecPacketListener(this))
                    listeners.add(
                        if (selectedAccount == null) OfflineLoginPacketListener(this)
                        else OnlineLoginPacketListener(this, selectedAccount)
                    )
                    listeners.add(GamingPacketHandler(this))
                }
            }.exceptionOrNull()?.let {
                it.printStackTrace()
            }
        }
    }

    private fun off() {
        thread(name = "MuCuteRelayThread") {
            isActive = false
            muCuteRelay?.disconnect()
            muCuteRelay = null
        }
    }
}