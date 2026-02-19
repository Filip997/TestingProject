package com.example.localinformant.core.domain.network

interface NetworkChecker {

    fun hasInternetConnection(): Boolean
}