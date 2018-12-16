package com.screw.mediumer.repository

class RepositoryFactory {
    companion object {
        fun get(): Repository {
            return RepositoryImpl()
        }
    }
}