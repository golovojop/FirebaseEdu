package k.s.yarlykov.firebaseedu.entities

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val name: String = "",
                val age : Int = 0,
                val email : String = "")