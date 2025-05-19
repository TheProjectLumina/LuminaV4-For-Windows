package com.projectlumina.luna.component

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.URI
import com.mucheng.mucute.relay.util.authorize
import com.projectlumina.luna.AccountManager
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode

@Composable
fun AuthBrowserLogin(onComplete: (Throwable?) -> Unit) {
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            runCatching {
                val fullBedrockSession = authorize(
                    cache = false,
                    msaDeviceCodeCallback = StepMsaDeviceCode.MsaDeviceCodeCallback {
                        // Open the verification URI in the system browser
                        Desktop.getDesktop().browse(URI(it.directVerificationUri))
                    }
                )

                val containedAccount = AccountManager.accounts.find {
                    it.mcChain.displayName == fullBedrockSession.mcChain.displayName
                }
                if (containedAccount != null) {
                    AccountManager.removeAccount(containedAccount)
                }
                AccountManager.addAccount(fullBedrockSession)

                if (containedAccount == AccountManager.selectedAccount) {
                    AccountManager.selectAccount(fullBedrockSession)
                }

                onComplete(null)
            }.onFailure {
                onComplete(it)
            }
        }
    }
}
