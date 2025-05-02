package ru.kotlix.frame.voice.server.exception

class ServerNotFoundException(name: String, region: String) :
    RuntimeException("Server $name $region is not found")
