package ru.skillbranch.skillarticles.data.adapters

import ru.skillbranch.skillarticles.data.local.User

class UserJsonAdapter() : JsonAdapter<User>{

    override fun fromJson(json: String): User? {
        return null
    }

    override fun toJson(obj: User?): String {
        return ""
    }

}