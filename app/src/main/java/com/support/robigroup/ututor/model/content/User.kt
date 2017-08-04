package com.support.robigroup.ututor.model.content

import io.realm.RealmObject

open class User(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var phone: Long? = null
): RealmObject()