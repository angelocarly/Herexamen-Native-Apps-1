package be.magnias.stahb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tab_table")
data class Tab(
    @PrimaryKey
    var _id: String,
    var artist: String,
    var song: String,
    var author: String?,
    var tab: String?
)