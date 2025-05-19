package com.projectlumina.luna

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession.FullBedrockSession
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.writeText

object AccountManager {

    private val coroutineScope =
        CoroutineScope(Dispatchers.IO + CoroutineName("AccountManagerCoroutine"))

    private val _accounts: MutableList<FullBedrockSession> = mutableStateListOf()

    val accounts: List<FullBedrockSession>
        get() = _accounts

    var selectedAccount: FullBedrockSession? by mutableStateOf(null)
        private set

    private val accountsDir: Path = Paths.get(System.getProperty("user.home"), ".luna", "accounts")

    init {
        val fetchedAccounts = fetchAccounts()
        _accounts.addAll(fetchedAccounts)
        selectedAccount = fetchSelectedAccount()
    }

    fun addAccount(fullBedrockSession: FullBedrockSession) {
        _accounts.add(fullBedrockSession)

        coroutineScope.launch {
            Files.createDirectories(accountsDir)
            val json = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN.toJson(fullBedrockSession)
            accountsDir.resolve("${fullBedrockSession.mcChain.displayName}.json")
                .writeText(json.toString())
        }
    }

    fun containsAccount(fullBedrockSession: FullBedrockSession): Boolean {
        return _accounts.find { it.mcChain.displayName == fullBedrockSession.mcChain.displayName } != null
    }

    fun removeAccount(fullBedrockSession: FullBedrockSession) {
        _accounts.remove(fullBedrockSession)

        coroutineScope.launch {
            Files.createDirectories(accountsDir)
            accountsDir.resolve("${fullBedrockSession.mcChain.displayName}.json")
                .toFile().delete()
        }
    }

    fun selectAccount(fullBedrockSession: FullBedrockSession?) {
        this.selectedAccount = fullBedrockSession

        coroutineScope.launch {
            Files.createDirectories(accountsDir)
            val selectedAccountFile = accountsDir.resolve("selectedAccount")
            runCatching {
                if (fullBedrockSession != null) {
                    selectedAccountFile.writeText(fullBedrockSession.mcChain.displayName)
                } else {
                    Files.deleteIfExists(selectedAccountFile)
                }
            }
        }
    }

    private fun fetchAccounts(): List<FullBedrockSession> {
        Files.createDirectories(accountsDir)
        val accounts = ArrayList<FullBedrockSession>()
        val files = accountsDir.listDirectoryEntries("*.json")
        for (file in files) {
            runCatching {
                val account = MinecraftAuth.BEDROCK_DEVICE_CODE_LOGIN
                    .fromJson(JsonParser.parseString(file.readText()).asJsonObject)
                accounts.add(account)
            }
        }
        return accounts
    }

    private fun fetchSelectedAccount(): FullBedrockSession? {
        Files.createDirectories(accountsDir)
        val selectedAccountFile = accountsDir.resolve("selectedAccount")
        if (!selectedAccountFile.exists() || selectedAccountFile.isDirectory()) {
            return null
        }
        val displayName = selectedAccountFile.readText()
        return accounts.find { it.mcChain.displayName == displayName }
    }
}