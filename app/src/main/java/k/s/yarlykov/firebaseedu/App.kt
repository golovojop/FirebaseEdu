package k.s.yarlykov.firebaseedu

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

class App : Application() {

    lateinit var db: FirebaseFirestore
        private set

    lateinit var auth: FirebaseAuth
        private set

    override fun onCreate() {
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }
}