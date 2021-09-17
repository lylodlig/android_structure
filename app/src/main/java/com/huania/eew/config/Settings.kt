package com.huania.eew.config

import com.huania.eew.data.Preference

object Settings {

    object Account {
        var username by Preference("username", "")
    }
}