package com.support.robigroup.ututor.model.content

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User(
        @PrimaryKey var token: String? = null,
        var email: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var phone: Long? = null
): RealmObject()