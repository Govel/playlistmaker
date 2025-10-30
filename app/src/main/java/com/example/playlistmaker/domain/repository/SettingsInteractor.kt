package com.example.playlistmaker.domain.repository

import com.google.android.material.materialswitch.MaterialSwitch

interface SettingsInteractor {
    fun getFrameShare()
    fun writeInSupport()
    fun getUserAgreement()
}