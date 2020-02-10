package k.s.yarlykov.firebaseedu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import k.s.yarlykov.firebaseedu.entities.User

/**
 * Подготовка приложения и первый запуск
 * https://firebase.google.com/docs/firestore/quickstart
 *
 * Нам нужен именно FirebaseFirestore, а не FirebaseDatabase
 *
 * FirebaseDatabase - это RealTimeDatabase
 * https://stackoverflow.com/questions/46549766/whats-the-difference-between-cloud-firestore-and-the-firebase-realtime-database
 */

private const val NONSECURE_USERS = "users"


class MainActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = (application as App).db

//        addUsers()
        readUsers()
    }

    /**
     * Чтобы добавить документы в несуществующую коллекцию, нужно просто указать имя
     * создаваемой коллекции в db.collection(<new_collection_name>)
     *
     * Создать документ с имененм вот так:
     *  db.collection(<collection_name>).document(<doc_name>).set(data_ref)
     *
     *  Если имя документа не имеет значения, то можно просто вот так:
     *  db.collection(<collection_name>).add(data_ref)
     *
     */
    private fun addUsers() {

        val users = listOf(
            User("Anna", 11, "anna@yakovlera"),
            User("Papa", 48, "papa@anna"),
            User("Gosha", 10, "gosha@gosha"),
            User("Masha", 17, "masha@masha")
        )

        for (user in users) {
            db.collection(NONSECURE_USERS).document(user.name).set(user)
        }

        val friend = hashMapOf<String, Any>(
            "name" to "Leha",
            "age" to 48,
            "email" to "leha@leha"
        )

        db.collection(NONSECURE_USERS).document(friend["name"] as String).set(friend)

        val incognito = hashMapOf<String, Any>(
            "name" to "Incognito",
            "age" to 10,
            "email" to "Incognito@Incognito"
        )

        db.collection(NONSECURE_USERS).add(incognito)
    }

    /**
     * Чиатем всю коллекцию. При этом в ответ прилетает некий Snapshot
     * в качестве контейнера для документов коллекции. В цикле извлекаем
     * по одному документу и преобразовываем в инстансы User. Кстати у
     * класса User должен быть конструктор с дефолтовыми значениями.
     */
    private fun readUsers(): List<User> {
        val users = mutableListOf<User>()

        db.collection(NONSECURE_USERS).get()
            .addOnSuccessListener { snapshot ->

                for (document in snapshot) {
                    users.add(document.toObject(User::class.java))
                }

                users.forEach {
                    Log.d("APP_TAG", it.name)
                }
            }
            .addOnFailureListener { e ->
                Log.d("APP_TAG", e.toString())
            }

        return users
    }
}
