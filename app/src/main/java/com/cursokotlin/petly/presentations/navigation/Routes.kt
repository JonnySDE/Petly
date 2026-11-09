package com.cursokotlin.petly.presentations.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"

    const val ARG_ID = "id"
    const val DETAIL = "detail/{$ARG_ID}"


    const val FORM = "form?$ARG_ID={$ARG_ID}"

    fun detail(id: String) = "detail/$id"

    fun form(id: String? = null) = if (id == null) "form" else "form?$ARG_ID=$id"
}