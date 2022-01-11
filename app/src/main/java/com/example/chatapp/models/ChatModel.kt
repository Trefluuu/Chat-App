package com.example.chatapp.models

class ChatModel(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long ){
    constructor(): this("","","","", -1)
}