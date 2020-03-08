package com.graphqltest

import com.apurebase.kgraphql.KGraphQL
import org.litote.kmongo.async.KMongo
import org.litote.kmongo.coroutine.findOne
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq


class AppSchema {

    private val client = KMongo.createClient("mongodb+srv://john:1234@cluster0-vhryk.gcp.mongodb.net/test?retryWrites=true&w=majority")

    val schema = KGraphQL.schema {

        configure {
            useDefaultPrettyPrinter = true
        }

        query("login") {
            description = "login a user"

            resolver { email: String, password: String ->
                val hasMatch = client.getDatabase("Account")
                    .getCollection("users")
                    .findOne(User::email eq email, User::password eq password)


                if (hasMatch != null) "Logged In." else "Account does not exist."
            }
        }

        query("phone") {
            description = "return some phones"

            resolver { limit: Int? ->
                val list = ArrayList<Phone>()
                val phoneList = client.getDatabase("Technology")
                    .getCollection("phone")
                    .find().limit(limit ?: 0).toList()
                for (document in phoneList) {
                    val phone = Phone(
                        document.getObjectId("_id").toString(),
                        document.getString("model"),
                        document.getString("first_release"),
                        document.getString("country")
                    )
                    list += phone
                }

                list
            }
        }

        type<Phone>()
    }
}