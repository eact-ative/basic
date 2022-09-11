package com.eactative.resource.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ResoureEntity(
    @Id
    var id: Long = 0,
    var url: String,
    var path: String,
    var hashCode: String,
    var cacheCtrl: String
)
