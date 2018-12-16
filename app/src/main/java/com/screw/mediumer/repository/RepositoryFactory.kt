package com.screw.mediumer.repository

class RepositoryFactory {
    companion object {
        fun getRepository() : Repository {
            return RepositoryImpl()
        }
    }
}