package com.projectlumina.luna

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform